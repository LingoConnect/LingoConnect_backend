package LingoConnect.app;

import LingoConnect.app.entity.SecondQuestion;
import LingoConnect.app.entity.TopQuestion;
import LingoConnect.app.repository.SecondQuestionRepository;
import LingoConnect.app.repository.TopQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TopQuestionRepository topQuestionRepository;

    @Autowired
    private SecondQuestionRepository secondQuestionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (topQuestionRepository.count() == 0) { // 테이블이 비어있는지 확인
            insertTopData();
        }

        if (secondQuestionRepository.count() == 0) { // 테이블이 비어있는지 확인
            insertSecondData();
        }
    }

    private void insertSecondData() {
        createSecondQuestion("그 과목을 좋아하게 된 특별한 이유가 있나요?", "학교");
        createSecondQuestion("그 과목에서 배운 것 중 가장 인상 깊었던 내용은 무엇인가요?", "학교");
        createSecondQuestion("그 과목을 가르치는 선생님은 어떤 분인가요?", "학교");
        createSecondQuestion("그 과목과 관련된 특별한 활동이나 프로젝트에 참여한 적이 있나요?", "학교");
    }

    private void createSecondQuestion(String question, String topic) {
        TopQuestion topQuestion = topQuestionRepository.findByTopic(topic)
                .orElseThrow(() -> new IllegalArgumentException("No TopQuestion found with topic"));

        SecondQuestion secondQuestion = SecondQuestion.builder()
                .question(question)
                .topQuestion(topQuestion)
                .build();
        secondQuestionRepository.save(secondQuestion);
    }

    private void insertTopData() {
        createTopQuestion("학교", "가장 좋아하는 과목은 무엇인가요?", "src/main/resources/img/school.jpg");
        createTopQuestion("음식", "가장 좋아하는 음식은 무엇인가요?", "src/main/resources/img/food.jpg");
        createTopQuestion("여행", "가장 기억에 남는 여행지는 어디인가요?", "src/main/resources/img/travel.jpg");
        createTopQuestion("취미", "취미가 무엇인가요?", "src/main/resources/img/hobby.jpg");
        createTopQuestion("영화", "가장 좋아하는 영화는 무엇인가요?", "src/main/resources/img/movie.jpg");
        createTopQuestion("음악", "가장 좋아하는 음악 장르는 무엇인가요?", "src/main/resources/img/music.jpg");
        createTopQuestion("운동", "가장 좋아하는 운동은 무엇인가요?", "src/main/resources/img/exercise.jpg");
    }

    private void createTopQuestion(String topic, String question, String imagePath) {
        TopQuestion topQuestion = TopQuestion.builder()
                .topic(topic)
                .question(question)
                .imagePath(imagePath)
                .build();
        topQuestionRepository.save(topQuestion);
    }
}
