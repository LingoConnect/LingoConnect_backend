package LingoConnect.app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class SecondQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1024)
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_question_id", nullable = false)
    private TopQuestion topQuestion;

    // Constructors
    public SecondQuestion() {
    }

    @Builder
    public SecondQuestion(Long id, String question, TopQuestion topQuestion) {
        this.id = id;
        this.question = question;
        this.topQuestion = topQuestion;
    }
}
