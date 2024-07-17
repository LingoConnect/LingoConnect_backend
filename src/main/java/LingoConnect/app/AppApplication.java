package LingoConnect.app;

import LingoConnect.app.service.GptService;
import LingoConnect.app.service.TtsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = SpringApplication.run(AppApplication.class, args);

		TtsService ttsService = context.getBean(TtsService.class);
		String text = "안녕하세요 주재원입니다.";
		String audioName = "output.mp3";

		ttsService.makeAudio(text,audioName);
	}

}
