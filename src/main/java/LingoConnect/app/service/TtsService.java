package LingoConnect.app.service;

import LingoConnect.app.client.OpenAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.DataBuffer;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;


@Service
public class TtsService {

    private OpenAiClient openAiClient;
    private String filePath;
    @Autowired
    public TtsService(OpenAiClient openAiClient, @Value("${audio.file-path}") String filePath) {
        this.filePath = filePath;
        this.openAiClient = openAiClient;
    }

    public String makeAudio(String text, String audioName) {
        String completeFilePath = filePath + "/" + audioName;

        byte[] audio = openAiClient.getAudio(text);
        try (FileOutputStream fos = new FileOutputStream(completeFilePath)) {
            fos.write(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return completeFilePath.toString();
    }
}