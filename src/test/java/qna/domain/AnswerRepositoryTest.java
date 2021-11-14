package qna.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class AnswerRepositoryTest {

    @Autowired
    AnswerRepository answers;

    @Autowired
    QuestionRepository questions;

    @Autowired
    UserRepository users;

    @Autowired
    private EntityManagerFactory factory;

    @Autowired
    private TestEntityManager testEntityManager;

    Question QUESTION;
    Answer ANSWER;
    User USER;

    @BeforeEach
    public void setUp() throws Exception {
        USER = users.save(new User("answerJavajigi", "password", "javajigi", new Email("javajigi@slipp.net")));
        QUESTION = questions.save(new Question("title1", "contents1").writeBy(USER));
        ANSWER = new Answer(QUESTION.getWriter(), QUESTION, "Answers Contents1");
    }

    @Test
    @DisplayName("Answer 저장 후 ID not null 체크")
    void save() {
        // given
        // when
        Answer expect = answers.save(ANSWER);

        // then
        assertThat(expect.getId()).isNotNull();
    }

    @Test
    @DisplayName("Answer 저장 후 findById 조회 결과 동일성 체크")
    void identity() {
        // given
        // when
        Answer actual = answers.save(ANSWER);
        Answer expect = answers.findById(actual.getId()).get();

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    @DisplayName("remove 처리 후 findByIdAndDeletedFalse 메소드 조회 미포함 체크 ")
    void deleted_findByIdAndDeletedFalse() {
        // given
        Answer expect = answers.save(ANSWER);
        expect.delete(USER);

        // when
        Answer answer = answers.findById(expect.getId()).get();

        // then
        assertAll(
            () -> assertThat(expect.isDeleted()).isTrue()
        );
    }

    @Test
    @DisplayName("Question,writer(User) lazy 로딩 확인")
    void question_lazy_loading() {
        // given
        PersistenceUnitUtil persistenceUnitUtil = factory.getPersistenceUnitUtil();
        Answer saveAnswer = answers.save(ANSWER);

        // when
        testEntityManager.clear();
        Answer actual = answers.findByIdAndDeletedFalse(saveAnswer.getId()).get();

        // then
        boolean questionExpect = persistenceUnitUtil.isLoaded(actual, "question");
        boolean writerExpect = persistenceUnitUtil.isLoaded(actual, "writer");
        assertAll(
            () -> assertThat(questionExpect).isFalse(),
            () -> assertThat(writerExpect).isFalse()
        );
    }
}
