package at.bigb.dynTrainPlan;

import at.bigb.dynTrainPlan.dao.Round;
import at.bigb.dynamicTrainingPlan_old.tennis.TennisPlayer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * creates a dynamic training plan for single or double matches depending on start parameters
 */
public class DynamicTrainingPlan {

    private static List<Round> trainingsPlan = new ArrayList<>();

    /**
     * @param args
     *   [0] "S"ingle or "D"ouble plan
     *   [1] Number of courts to fill (only considered with single plans!)
     *   [2] Number of rounds to play
     *   [3] Number of valid equal pairings
     *   [4] Number of valid rounds to pause
     *   [5] FULL fileName including directory
     *   [6..n] Player names (use underscore to separate first and last name, use "!" to OPTIONALLY define maximum occurrences afterwards)
     */
    public static void main(String[] args) {
        // first step, check if parameters are valid
        if (!checkValidDataForPlanCreation(args)) {
            System.out.println("Error - Invalid number/type of arguments!");
            return;
        }
        String fileName = args[5];

        // last step, create the trainingsPlan file
        buildAndWriteCsvFile(fileName);
    }

    public static boolean checkValidDataForPlanCreation(String[] args) {
        // check correct amount of parameters and eventually types also
        if (args == null || args.length < 8) {
            return false;
        }
        return true;
    }

    public static void buildAndWriteCsvFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            // loop over trainingPlan (and write line for each round)
            for(int roundNo = 1; roundNo <= trainingsPlan.size(); roundNo++) {
                writer.append(Integer.toString(roundNo));
                writer.append(',');

                // new line
                writer.append('\n');
            }
            // write the data to the file
            writer.flush();
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Round> getTrainingsPlan() {
        return trainingsPlan;
    }

    public void setTrainingsPlan(List<Round> trainingsPlan) {
        this.trainingsPlan = trainingsPlan;
    }
}
