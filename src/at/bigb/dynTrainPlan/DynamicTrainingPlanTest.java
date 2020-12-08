package at.bigb.dynTrainPlan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}