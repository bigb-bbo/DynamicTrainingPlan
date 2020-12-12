package at.bigb.dynTrainPlan;

import at.bigb.dynTrainPlan.dao.Player;
import at.bigb.dynTrainPlan.dao.Round;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTrainingPlanTest {

    DynamicTrainingPlan trainingsPlan;

    @BeforeEach
    void setUp() {
        trainingsPlan = new DynamicTrainingPlan();
    }

    @Test
    void checkValidDataForPlanCreation() {
        String[] args = new String[] {"D", "2"};
        assertFalse(trainingsPlan.checkValidDataForPlanCreation(args));
        args = new String[] {"D", "2", "BigB"};
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
            Player player1 = new Player("Player1");
            Player player2 = new Player("Player2");
            Player player3 = new Player("Player3");
            Player player4 = new Player("Player4");
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
}