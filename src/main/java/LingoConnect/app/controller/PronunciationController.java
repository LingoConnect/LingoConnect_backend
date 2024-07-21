package LingoConnect.app.controller;

import LingoConnect.app.response.SuccessResponse;
import LingoConnect.app.service.PronunciationEvalService;
import LingoConnect.app.service.SttService;
import LingoConnect.app.utils.AudioFileUtils;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/pronunciation")
@RequiredArgsConstructor
public class PronunciationController {

    private final PronunciationEvalService pronunciationEvalService;
    private final SttService sttService;

    @PostMapping("/")
    @Transactional
    @Operation(
            summary = "Upload Int16Array to get pronunciation evaluation and to get text from voice data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Bad credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public String evaluateAndExtractVoiceData(@RequestParam("formData") MultipartFile voiceData) {
        String fileName = null;

        log.info("executed");
        if (voiceData.isEmpty()) {
            return "Failed to upload the file because it is empty";
        }

        String uploadDir = "uploaded_files/";
        Path targetPath = Paths.get(uploadDir + voiceData.getOriginalFilename());

        try {
            // InputStream을 사용하여 파일을 서버에 저장
            Files.copy(voiceData.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 저장된 WAV 파일을 PCM 형식으로 변환
            File pcmFile = convertWavToPcm(targetPath.toFile());
            return "File uploaded and converted successfully: " + pcmFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload or convert file due to IO exception";
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    private File convertWavToPcm(File wavFile) throws IOException, UnsupportedAudioFileException {
        AudioInputStream wavStream = AudioSystem.getAudioInputStream(wavFile);
        AudioFormat format = wavStream.getFormat();
        AudioFormat pcmFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(),
                format.getSampleSizeInBits(),
                format.getChannels(),
                format.getChannels() * 2,
                format.getSampleRate(),
                false
        );

        AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFormat, wavStream);
        File pcmFile = new File(wavFile.getParent(), wavFile.getName().replace(".wav", ".pcm"));
        try (OutputStream os = new FileOutputStream(pcmFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = pcmStream.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }

        wavStream.close();
        pcmStream.close();

        return pcmFile;
    }
}
