package at.bigb.dynTrainPlan.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerTest {

    private static final int MAX_ROUNDS_OF_TRAINING = 26;
    Player myPlayer = new Player("BBoTester");

    @BeforeAll
    void setUp() {
        myPlayer.setPlayCounter(1);
    }

    @AfterAll
    void tearDown() {
    }

    @Test
    void checkPlayCounter() {
        assertTrue(myPlayer.getPlayCounter() > 0 && myPlayer.getPlayCounter() < MAX_ROUNDS_OF_TRAINING);
    }
}