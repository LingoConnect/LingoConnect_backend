package LingoConnect.app.controller;

import LingoConnect.app.dto.SecondQuestionDTO;
import LingoConnect.app.dto.TopQuestionDTO;
import LingoConnect.app.response.SuccessResponse;
import LingoConnect.app.service.SecondQuestionService;
import LingoConnect.app.service.TopQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final TopQuestionService topQuestionService;

    private final SecondQuestionService secondQuestionService;

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
    public ResponseEntity<?> getQuestionAndImage(@RequestParam(name = "topic") String topic) {
        try {
            TopQuestionDTO topQuestionDTO = topQuestionService.findByTopic(topic);
            if (topQuestionDTO == null) {
                return ResponseEntity.notFound().build();
            }

            // Build the path to the image file
            Path imagePath = Paths.get(topQuestionDTO.getImagePath().trim());
            if (!Files.exists(imagePath) || !Files.isReadable(imagePath)) {
                return ResponseEntity.notFound().build();
            }

            // Read image file as byte array
            byte[] imageBytes = Files.readAllBytes(imagePath);
            String encodedImage = Base64Utils.encodeToString(imageBytes);

            // Prepare response body
            HashMap<String, String> responseBody = new HashMap<>();
            responseBody.put("topic", topic);
            responseBody.put("question", topQuestionDTO.getQuestion());
            responseBody.put("image", "data:image/jpeg;base64," + encodedImage);

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
    public ResponseEntity<?> getSubQuestion(@RequestParam(name = "topic") String topic) {
        TopQuestionDTO topQuestionDTO = topQuestionService.findByTopic(topic);

        if (topQuestionDTO == null) {
            return ResponseEntity.notFound().build();
        }

        ArrayList<SecondQuestionDTO> secondQuestionDTOS = secondQuestionService.findByTopQuestionId(topQuestionDTO.getId());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(secondQuestionDTOS);
    }


}
