package gruppe_b.quizduell.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;

import gruppe_b.quizduell.application.common.GameSessionDto;
import gruppe_b.quizduell.application.common.PlayerRoundStatus;
import gruppe_b.quizduell.application.enums.RoundStatus;
import gruppe_b.quizduell.application.game.QuizSession;
import gruppe_b.quizduell.application.interfaces.SendToPlayerService;
import gruppe_b.quizduell.application.models.Quiz;
import gruppe_b.quizduell.application.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@SpringBootTest
class GameSessionTest {

    @Mock
    SendToPlayerService sendToPlayerService;

    QuizSession spySession;

    Quiz quiz;
    QuizSession session;

    @BeforeEach
    void setUp() {
        quiz = new Quiz(UUID.randomUUID());
        quiz.addPlayer(UUID.randomUUID());
        quiz.addPlayer(UUID.randomUUID());
        session = new QuizSession(quiz, sendToPlayerService);
    }

    @Test
    void simpleTest() throws Exception {
        // Arrange
        Quiz quizLocal = new Quiz(UUID.randomUUID());

        // Act
        QuizSession sessionLocal = new QuizSession(quizLocal, sendToPlayerService);

        // Assert
        assertNotNull(sessionLocal);
    }

    @Test
    void testCountdown() throws Exception {
        // Arrange

        // Act
        session.start();

        Thread.sleep(23_000);

        // Assert
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), 21);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 20);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 19);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 18);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 17);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 16);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 15);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 14);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 13);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 12);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 11);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 10);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 9);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 8);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 7);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 6);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 5);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 4);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 3);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 2);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 1);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 0);
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), -1);
    }

    @Test
    void whenStartGameThenSendGameDtoOnlyOnce() throws Exception {
        // Arrange

        // Act
        session.start();

        Thread.sleep(1_000);

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                any(GameSessionDto.class));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcher(1)));
    }

    @Test
    void whenPlayerSendAnswerThenSendUpdate() throws Exception {
        // Arrange
        UUID playerId = quiz.getPlayers().get(0).getUserId();

        ArgumentMatcher<GameSessionDto> matcherUpdate = x -> x.maxRounds == 6 &&
                x.currentRound == 1 &&
                x.correctAnswer == 0 &&
                x.questionText.length() > 0 &&
                x.answerOne.length() > 0 &&
                x.answerTwo.length() > 0 &&
                x.answerThree.length() > 0 &&
                x.answerFour.length() > 0 &&
                x.playerList.get(0).playerRoundStatus == PlayerRoundStatus.FINISH &&
                x.playerList.get(1).playerRoundStatus == PlayerRoundStatus.GUESS;

        // Act
        session.start();

        Thread.sleep(100);

        session.playerAnswer(playerId, 1);

        Thread.sleep(500);

        // Assert
        verify(sendToPlayerService, times(2)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                any(GameSessionDto.class));

        verify(sendToPlayerService, times(2)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcher(1)));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(matcherUpdate));
    }

    @Test
    @RepeatedTest(10)
    void whenTwoPlayerSendAnswerThenSendTwoUpdates() throws Exception {
        // Arrange
        UUID playerId1 = quiz.getPlayers().get(0).getUserId();
        UUID playerId2 = quiz.getPlayers().get(1).getUserId();
        assertNotEquals(playerId1, playerId2);

        // Act
        session.start();

        Thread.sleep(100);

        session.playerAnswer(playerId1, 1);

        Thread.sleep(1_000);

        session.playerAnswer(playerId2, 1);

        Thread.sleep(2000);

        // Assert
        verify(sendToPlayerService, times(3)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                any(GameSessionDto.class));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcherRoundOpen(1)));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcherRoundAndPlayerFinish(1)));
    }

    @Test
    @RepeatedTest(10)
    void whenTwoPlayerSendAnswerThenSendTwoUpdates2() throws Exception {
        // Arrange
        UUID playerId1 = quiz.getPlayers().get(0).getUserId();
        UUID playerId2 = quiz.getPlayers().get(1).getUserId();
        assertNotEquals(playerId1, playerId2);

        // Act
        session.start();

        Thread.sleep(100);

        session.playerAnswer(playerId1, 1);

        session.playerAnswer(playerId2, 1);

        Thread.sleep(2000);

        // Assert
        verify(sendToPlayerService, times(2)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                any(GameSessionDto.class));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcherRoundOpen(1)));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcherRoundAndPlayerFinish(1)));
    }

    @Test
    void whenPlayerSendAnswerThenReduceCountdown() throws Exception {
        // Arrange
        UUID playerId = quiz.getPlayers().get(0).getUserId();

        // Act
        session.start();

        Thread.sleep(2_000);

        session.playerAnswer(playerId, 1);

        Thread.sleep(3_000);

        // Assert
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 20);
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), 17);
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), 7);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 6);
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 5);
    }

    @Test
    void whenAllPlayerSendAnswerThenEndCountdown() throws Exception {
        // Arrange
        UUID playerId1 = quiz.getPlayers().get(0).getUserId();
        UUID playerId2 = quiz.getPlayers().get(1).getUserId();
        assertNotEquals(playerId1, playerId2);

        // Act
        session.start();

        Thread.sleep(1_000);

        session.playerAnswer(playerId1, 2);
        session.playerAnswer(playerId2, 2);

        Thread.sleep(3_000);

        // Assert
        verify(sendToPlayerService, times(1)).sendRoundCountdown(quiz.getLobbyId(), 20);
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), 17);
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), 6);
        verify(sendToPlayerService, never()).sendRoundCountdown(quiz.getLobbyId(), 5);
    }

    @Test
    void whenRoundEndThenStartNextRound() throws Exception {
        // Arrange
        UUID playerId1 = quiz.getPlayers().get(0).getUserId();
        UUID playerId2 = quiz.getPlayers().get(1).getUserId();
        assertNotEquals(playerId1, playerId2);

        // Act
        session.start();

        Thread.sleep(100);

        session.playerAnswer(playerId1, 1);
        session.playerAnswer(playerId2, 2);

        Thread.sleep(10_000);

        // Assert
        verify(sendToPlayerService, times(3)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                any(GameSessionDto.class));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcher(1)));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcherRoundFinish(1)));

        verify(sendToPlayerService, times(1)).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcher(2)));

        verify(sendToPlayerService, never()).sendGameSessionUpdate(eq(quiz.getLobbyId()),
                argThat(getGameSessionArgumentMatcher(3)));
    }

    @Test
    void calcRemainingSecondsTest() throws Exception {
        // Arrange

        // Act
        session.start();

        Thread.sleep(2000);

        int passSeconds = session.calcRemainingSeconds();

        // Assert
        assertTrue(passSeconds > 15 && passSeconds < 20);
    }

    ArgumentMatcher<GameSessionDto> getGameSessionArgumentMatcher(int currentRound) {
        return x -> x.maxRounds == 6 &&
                x.currentRound == currentRound &&
                x.correctAnswer == 0 &&
                x.questionText.length() > 0 &&
                x.answerOne.length() > 0 &&
                x.answerTwo.length() > 0 &&
                x.answerThree.length() > 0 &&
                x.answerFour.length() > 0;
    }

    ArgumentMatcher<GameSessionDto> getGameSessionArgumentMatcherRoundOpen(int currentRound) {
        return x -> x.maxRounds == 6 &&
                x.currentRound == currentRound &&
                x.correctAnswer == 0 &&
                x.questionText.length() > 0 &&
                x.answerOne.length() > 0 &&
                x.answerTwo.length() > 0 &&
                x.answerThree.length() > 0 &&
                x.answerFour.length() > 0 &&
                x.roundStatus == RoundStatus.OPEN &&
                x.playerList.get(0).playerRoundStatus == PlayerRoundStatus.GUESS &&
                x.playerList.get(1).playerRoundStatus == PlayerRoundStatus.GUESS;
    }

    ArgumentMatcher<GameSessionDto> getGameSessionArgumentMatcherRoundFinish(int currentRound) {
        return x -> x.maxRounds == 6 &&
                x.currentRound == currentRound &&
                x.correctAnswer != 0 &&
                x.questionText.length() > 0 &&
                x.answerOne.length() > 0 &&
                x.answerTwo.length() > 0 &&
                x.answerThree.length() > 0 &&
                x.answerFour.length() > 0 &&
                x.roundStatus == RoundStatus.CLOSE;
    }

    ArgumentMatcher<GameSessionDto> getGameSessionArgumentMatcherRoundAndPlayerFinish(int currentRound) {
        return x -> x.maxRounds == 6 &&
                x.currentRound == 1 &&
                x.correctAnswer != 0 &&
                x.questionText.length() > 0 &&
                x.answerOne.length() > 0 &&
                x.answerTwo.length() > 0 &&
                x.answerThree.length() > 0 &&
                x.answerFour.length() > 0 &&
                x.roundStatus == RoundStatus.CLOSE &&
                x.playerList.get(0).playerRoundStatus == PlayerRoundStatus.FINISH &&
                x.playerList.get(1).playerRoundStatus == PlayerRoundStatus.FINISH;
    }
}
