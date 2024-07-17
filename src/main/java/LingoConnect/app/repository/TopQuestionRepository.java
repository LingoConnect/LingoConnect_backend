package LingoConnect.app.repository;

import LingoConnect.app.entity.TopQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopQuestionRepository extends JpaRepository<TopQuestion, Long> {
    Optional<TopQuestion> findByTopic(String topic);
}
