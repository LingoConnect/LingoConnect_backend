package LingoConnect.app.client;


import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OpenAiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String instructions;

    public OpenAiClient(WebClient.Builder webClientBuilder,
                        @Value("${openai.api-key}") String apiKey,
                        @Value("${openai.instructions}") String instructions) {
        this.webClient = webClientBuilder
                .filter(logRequest())
                .filter(logResponse())
                .build();
        this.apiKey = apiKey;
        this.instructions = instructions;
    }

    public Mono<String> createAssistant(String model) {
        JsonObject json = new JsonObject();
        json.addProperty("name", "lingoConnect");
        json.addProperty("instructions", instructions);
        json.addProperty("model", model);
        JsonArray tools = new JsonArray();
        JsonObject tool = new JsonObject();
        tool.addProperty("type", "file_search");
        tools.add(tool);

        json.add("tools", tools);

        log.info("json to String: {}", json.toString());
        return webClient.post()
                .uri("https://api.openai.com/v1/assistants")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("OpenAI-Beta", "assistants=v2")
                .bodyValue(json.toString())
                .retrieve()
                .bodyToMono(String.class);
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}: {}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{}: {}", name, value)));
            return Mono.just(clientResponse);
        });
    }
}
