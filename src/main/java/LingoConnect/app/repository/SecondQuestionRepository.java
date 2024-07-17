package LingoConnect.app.repository;

import LingoConnect.app.entity.SecondQuestion;
import LingoConnect.app.entity.TopQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondQuestionRepository extends JpaRepository<SecondQuestion, Long> {
}
