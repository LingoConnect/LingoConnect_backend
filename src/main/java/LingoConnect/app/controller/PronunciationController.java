package LingoConnect.app.controller;

import LingoConnect.app.request.GptRequest;
import LingoConnect.app.response.SuccessResponse;
import LingoConnect.app.service.PronunciationEvalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/pronunciation")
public class PronunciationController {

    private PronunciationEvalService pronunciationEvalService;

    public PronunciationController(PronunciationEvalService pronunciationEvalService) {
        this.pronunciationEvalService = pronunciationEvalService;
    }

    @GetMapping("/")
    @Transactional
    @Operation(
            summary = "evaluate pronunciation",
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
    public ResponseEntity<?> evaluatePronunciation() {
        String result = pronunciationEvalService.evaluate("KOR_M_RM0276MKDH0135.pcm");
        log.info("result: {}", result);

        return ResponseEntity.ok().body("음성평가 결과: " + result + " 점");
    }
}
