package LingoConnect.app.controller;

import LingoConnect.app.response.SuccessResponse;
import LingoConnect.app.service.SecondQuestionService;
import LingoConnect.app.service.TopQuestionService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/topic")
public class TopicController {

    private TopQuestionService topQuestionService;

    private String imagePath;

    public TopicController(TopQuestionService topQuestionService,
                           @Value("${image.file-path}") String imagePath) {
        this.topQuestionService = topQuestionService;
        this.imagePath = imagePath;
    }

    @GetMapping("/")
    @Transactional
    @Operation(
            summary = "get all topics",
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
    public ResponseEntity<List<Map<String, Object>>> listDistinctTopics() throws IOException {
        List<String> topics = topQuestionService.getDistinctTopics();
        List<Map<String, Object>> response = new ArrayList<>();

        for (String topic : topics) {
            Resource resource = new FileSystemResource(imagePath + topic + ".jpeg");
            if (!resource.exists()) {
                log.error("Resource not found: {}", resource);
                continue;  // or handle the error appropriately
            }

            byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            String encodedImage = Base64Utils.encodeToString(imageBytes);

            Map<String, Object> topicObject = new HashMap<>();
            topicObject.put(topic + "_image", encodedImage);
            topicObject.put("topic", topic);

            response.add(topicObject);
        }

        return ResponseEntity.ok(response);
    }
}
