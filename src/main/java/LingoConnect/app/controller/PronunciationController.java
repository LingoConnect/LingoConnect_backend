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
            summary = "Upload Int16Array and get pronunciation evaluation",
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
    public ResponseEntity<?> evaluateVoiceData(@RequestBody byte[] voiceData) {
        String fileName = null;

        try {
            fileName = AudioFileUtils.convertAndStorePcm(voiceData);
            log.info("File stored at {}", fileName);

        } catch (Exception e) {
            log.error("Error processing voice data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing voice data");
        }

        log.info("File successfully stored at: {}", fileName);

        String text = sttService.speechToText(fileName);
        String evaluated = pronunciationEvalService.evaluate(fileName);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text",text);
        jsonObject.addProperty("evaluated",evaluated);

        return ResponseEntity.ok().body(jsonObject);
    }
}
