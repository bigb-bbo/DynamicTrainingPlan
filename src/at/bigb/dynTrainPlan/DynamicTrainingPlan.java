package at.bigb.dynTrainPlan;

/**
 * creates a dynamic training plan depending on start parameters
 */
public class DynamicTrainingPlan {

    public static void main(String[] args) {

    }

    public boolean checkValidDataForPlanCreation(String[] args) {
        if (args == null || args.length < 3) {
            return false;
        }
        return true;
    }

    public void buildAndWriteCsvFile() {

    }
}
