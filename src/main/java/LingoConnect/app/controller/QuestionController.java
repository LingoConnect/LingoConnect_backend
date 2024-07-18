package LingoConnect.app.controller;

import LingoConnect.app.dto.SecondQuestionDTO;
import LingoConnect.app.dto.TopQuestionDTO;
import LingoConnect.app.entity.SecondQuestion;
import LingoConnect.app.response.SuccessResponse;
import LingoConnect.app.service.SecondQuestionService;
import LingoConnect.app.service.TopQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/question")
public class QuestionController {

    private TopQuestionService topQuestionService;

    private SecondQuestionService secondQuestionService;

    private String imagePath;
    public QuestionController(TopQuestionService topQuestionService,
                              SecondQuestionService secondQuestionService,
                              @Value("${image.file-path}") String imagePath) {
        this.topQuestionService = topQuestionService;
        this.secondQuestionService = secondQuestionService;
        this.imagePath = imagePath;
    }

    @GetMapping("/main")
    @Transactional
    @Operation(
            summary = "topic에 맞는 메인 질문을 받아온다.",
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
    public ResponseEntity<?> getQuestion(@RequestParam(name = "topic") String topic) {
        try {
            TopQuestionDTO topQuestionDTO = topQuestionService.findByTopic(topic);
            if (topQuestionDTO == null) {
                return ResponseEntity.notFound().build();
            }

            HashMap<String, String> responseBody = new HashMap<>();
            responseBody.put("question", topQuestionDTO.getQuestion());
            responseBody.put("topic", topic);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
    @GetMapping("/sub")
    @Transactional
    @Operation(
            summary = "topic에 맞는 꼬리 질문들을 받아온다.",
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
    public ResponseEntity<?> getSubQuestion(@RequestParam(name = "topic") String topic) throws IOException {
        TopQuestionDTO topQuestionDTO = topQuestionService.findByTopic(topic);

        if (topQuestionDTO == null) {
            return ResponseEntity.notFound().build();
        }

        ArrayList<SecondQuestionDTO> secondQuestionDTOS = secondQuestionService.findByTopQuestionId(topQuestionDTO.getId());

        List<Map<String, Object>> response = new ArrayList<>();

        for(SecondQuestionDTO secondQuestionDTO : secondQuestionDTOS) {
            Resource resource = new FileSystemResource(imagePath + secondQuestionDTO.getImageName());
            if (!resource.exists()) {
                log.error("Resource not found: {}", resource);
            }

            byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            String encodedImage = Base64Utils.encodeToString(imageBytes);

            Map<String, Object> topicObject = new HashMap<>();
            topicObject.put("main_question_id", secondQuestionDTO.getTopQuestionId());
            topicObject.put("question", secondQuestionDTO.getQuestion());
            topicObject.put(topic + "_image", encodedImage);
            topicObject.put("topic", topic);
            topicObject.put("id", secondQuestionDTO.getId());

            response.add(topicObject);
        }

        return ResponseEntity.ok(response);
    }


}
