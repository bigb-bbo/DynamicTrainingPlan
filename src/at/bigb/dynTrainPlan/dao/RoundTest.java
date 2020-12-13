package at.bigb.dynTrainPlan.dao;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoundTest {

    Round myRound;

    @BeforeEach
    void setUp() {
        myRound = new Round();
    }

    @AfterAll
    void tearDown() {
    }

    @Test
    void initialRoundIsEmpty() {
        assertTrue(myRound.getPlayers().isEmpty());
    }

    private void addPlayers(List<String> playerNames) {
        AtomicInteger counter = new AtomicInteger(1);
        playerNames.forEach(name -> myRound.getPlayers().add(new Player(name, counter.getAndIncrement())));
    }

    @Test
    void checkSingleGame() {
        this.addPlayers(Arrays.asList("Hugo", "Berti"));
        assertTrue(myRound.validSingleRound());
    }

    @Test
    void checkDoubleGame() {
        if (myRound.getPlayers().isEmpty()) {
            this.addPlayers(Arrays.asList("Hugo", "Berti"));
        }
        this.addPlayers(Arrays.asList("Sepp", "Kurt"));
        assertTrue(myRound.validDoubleRound());
    }
}