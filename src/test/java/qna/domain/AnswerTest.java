package qna.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import qna.common.exception.NotFoundException;
import qna.common.exception.UnAuthorizedException;

public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

    @Test
    void deleted_기본값_false() {
        // then
        assertThat(new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2").isDeleted()).isFalse();
    }

    @Test
    void deleted() {
        // given
        Answer answer = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

        // when
        answer.delete(UserTest.SANJIGI);

        // then
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    void isOwner_질문_작성자_확인() {

        // then
        assertAll(
            () -> assertThat(A1.isOwner(UserTest.JAVAJIGI)).isTrue(),
            () -> assertThat(A1.isOwner(UserTest.SANJIGI)).isFalse()
        );
    }

    @Test
    @DisplayName("writer(작성자)가 없으면 예외 발생")
    void exception_create_User_null() {
        assertThatExceptionOfType(UnAuthorizedException.class) // then
            .isThrownBy(() -> {
                // when
                new Answer(null, QuestionTest.Q1, "Answers Contents1");
            });
    }

    @Test
    @DisplayName("question(질문)이 없으면 예외 발생")
    void exception_create_Question_null() {
        assertThatExceptionOfType(NotFoundException.class) // then
            .isThrownBy(() -> {
                // when
                new Answer(UserTest.JAVAJIGI, null, "Answers Contents1");
            });
    }

    @Test
    @DisplayName("게스트는 답변 할 수 없습니다. 예외 발생")
    void guest_answer_fail() {
        // given
        User guest = User.GUEST_USER;

        assertThatExceptionOfType(UnAuthorizedException.class) // then
            .isThrownBy(() -> {
                // when
                new Answer(guest, QuestionTest.Q1, "Answers Contents1");
            });
    }
}
