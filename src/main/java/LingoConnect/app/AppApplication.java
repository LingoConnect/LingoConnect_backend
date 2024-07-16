package LingoConnect.app;

import LingoConnect.app.service.GptService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AppApplication.class, args);
//		GptService gptService = context.getBean(GptService.class);
//		String result = gptService.createAssistant("gpt-3.5-turbo-0125");
//		System.out.println(result);

	}

}
