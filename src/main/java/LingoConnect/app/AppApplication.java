package LingoConnect.app;

import LingoConnect.app.service.GptService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = SpringApplication.run(AppApplication.class, args);
		GptService gptService = context.getBean(GptService.class);
		String assistantId = "asst_72rWdwPlhnx8wsH6kSZ2Nypk";
		String threadId = gptService.createThreadAndGetId();
		String messageId = gptService.createMessageAndGetId(threadId,
				"주제: 일상 대화 연습\n" +
						"친구: 학교에서 가장 좋아하는 활동이 뭐야?\n" +
						"사용자: 나는 미술 수업을 정말 좋아해!\n");
		String runId = gptService.createRun(threadId, assistantId);
		String response = gptService.getResponse(threadId, runId);
		System.out.println(response);
	}

}
