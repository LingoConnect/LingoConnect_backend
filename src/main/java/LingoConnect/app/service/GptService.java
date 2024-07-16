package LingoConnect.app.service;

import LingoConnect.app.client.OpenAiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GptService {

    private OpenAiClient openAiClient;

    @Autowired
    public GptService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public String createAssistant(String model) {
        return openAiClient.createAssistant(model).block();
    }
}
