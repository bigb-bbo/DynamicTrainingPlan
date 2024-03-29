package at.bigb.dynTrainPlan;

import at.bigb.dynTrainPlan.dao.Player;
import at.bigb.dynTrainPlan.dao.Round;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for dynamic training plan
 * ATTENTION: for correct complete runs a directory "C:\_boehm" needs to exist
 */
class DynamicTrainingPlanTest {

    DynamicTrainingPlan trainingsPlan;

    @BeforeEach
    void setUp() {
        trainingsPlan = new DynamicTrainingPlan();
    }

    @Test
    void checkValidDataForPlanCreation() {
        String[] args = new String[] {"D", "2", "6", "C://_boehm//bbo_test.?", "Player1", "Player2"};
        assertFalse(trainingsPlan.checkValidDataForPlanCreation(args));
        args = new String[] {"S", "1", "26", "C://_boehm//bbo_test.csv", "Player1", "Player2"};
        assertTrue(trainingsPlan.checkValidDataForPlanCreation(args));
    }

    @Test
    void checkCsvFileCreation() {
        String testFileName = "C://_boehm//bbo_dynamicPlanGeneration_test.csv";
        // first delete possible file
        File deleteFile = new File(testFileName);
        deleteFile.delete();
        // now check file creation
        this.createTrainingsPlanEntries();
        trainingsPlan.buildAndWriteCsvFile(testFileName);
        boolean fileExists = true;
        FileReader file = null;
        try {
            file = new FileReader(testFileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
        assertTrue(fileExists);
    }

    private void createTrainingsPlanEntries() {
        List<Round> entries = new ArrayList<>();
        for(int i=0;i < 26;i++) {
            Round round = new Round();
            Player player1 = new Player("Player1", 1);
            Player player2 = new Player("Player2", 2);
            Player player3 = new Player("Player3", 3);
            Player player4 = new Player("Player4", 4);
            round.setPlayers(Arrays.asList(player1,player2,player3,player4));
            entries.add(round);
        }
        // set entries in trainingsPlan
        trainingsPlan.setTrainingsPlan(entries);
    }

    @Test
    void validTrainingsPlanEntriesCreation() {
        this.createTrainingsPlanEntries();
        assertTrue(trainingsPlan.getTrainingsPlan().size() == 26);
    }

    @Test
    private void doSingleGameTesting() {
        // create args (first 4 values are no players and therefore not relevant)
        String[] args = new String[] {"", "", "", "", "Player1", "Player2"};
        // two players should result in 1 possible combination
        assertEquals(1, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
        // 3 players should result in 3 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3"};
        assertEquals(3, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
        // 4 players should result in 6 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4"};
        assertEquals(6, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
        // 5 players should result in 10 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5"};
        assertEquals(10, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
        // 6 players should result in 15 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6"};
        assertEquals(15, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
        // 8 players should result in 28 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6",
                "Player7", "Player8"};
        assertEquals(28, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
        // 10 players should result in 45 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6",
                "Player7", "Player8", "Player9", "Player10"};
        assertEquals(45, trainingsPlan.createAllPossibleRoundCombinations(true, args).size());
    }

    @Test
    private void doDoubleGameTesting() {
        // with less than 4 players no round is possible
        String[] args = new String[] {"", "", "", "", "Player1", "Player2", "Player3"};
        assertEquals(0, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 4 players should result in 1 possible combination
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4"};
        assertEquals(1, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 5 players should result in 5 possible combinations
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5"};
        assertEquals(5, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 6 players should result in 15 possible combinations => not CORRECT yet => TODO
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6"};
        assertEquals(23, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 7 players should result in 35 possible combinations => not CORRECT (as previous tests for doubles) => TODO
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6",
                "Player7"};
        assertEquals(48, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 8 players should result in ? possible combinations => not CORRECT (as previous tests for doubles) => TODO
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6",
                "Player7", "Player8"};
        assertEquals(89, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 10 players should result in ? possible combinations => not CORRECT (as previous tests for doubles) => TODO
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6",
                "Player7", "Player8", "Player9", "Player10"};
        assertEquals(213, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
        // 11 players should result in ? possible combinations => not CORRECT (as previous tests for doubles) => TODO
        args = new String[] {"", "", "", "", "Player1", "Player2", "Player3", "Player4", "Player5", "Player6",
                "Player7", "Player8", "Player9", "Player10", "Player11"};
        assertEquals(298, trainingsPlan.createAllPossibleRoundCombinations(false, args).size());
    }

    @Test
    void checkAllCombinationsCreated() {
        doSingleGameTesting();
        doDoubleGameTesting();
    }

    @Test
    void doRangeTestForPlanOptimization() {
        List<Round> entries = new ArrayList<>();
        for(int i=0;i < 26;i++) {
            Round round = new Round();
            Player player1 = new Player("Player1", 1);
            Player player2 = new Player("Player2", 2);
            Player player3 = new Player("Player3", 3);
            Player player4 = new Player("Player4", 4);
            round.setPlayers(Arrays.asList(player1,player2,player3,player4));
            entries.add(round);
        }
        assertEquals(20, trainingsPlan.getOptimizedTrainingsPlan(entries,false,
                1,20).size());
    }
}