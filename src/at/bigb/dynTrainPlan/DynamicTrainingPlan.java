package at.bigb.dynTrainPlan;

import at.bigb.dynTrainPlan.dao.Player;
import at.bigb.dynTrainPlan.dao.Round;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
     *   [3] FULL fileName including directory
     *   [4..n] Player names (use underscore to separate first and last name, use "!" to OPTIONALLY define maximum occurrences afterwards)
     */
    public static void main(String[] args) {
        // first step, check if parameters are valid
        if (!checkValidDataForPlanCreation(args)) {
            System.out.println("Error - Invalid number/type of arguments!");
            return;
        }
        boolean isSingleGame = "S".equals(args[0]);
        String fileName = args[3];
        // create ALL possible round combinations (depending on number of players)
        List<Round> allPossibleRoundCombinations = createAllPossibleRoundCombinations(isSingleGame, args);

        // optimize rounds, meaning least possible number of equal pairings and fewest possible rounds to pause

        // last step, create the trainingsPlan file
        buildAndWriteCsvFile(fileName);
    }

    public static boolean checkValidDataForPlanCreation(String[] args) {
        // check correct amount of parameters and eventually types also
        if (args == null || args.length < 6) {
            System.out.println("not enough arguments provided at startup");
            return false;
        }
        if (!"S".equals(args[0]) && !"D".equals(args[0])) {
            System.out.println("first argument needs to be S or D");
            return false;
        }
        try {
            if (Integer.parseInt(args[1]) < 1) {
                System.out.println("please provide a valid number of courts to fill (>= 1)");
                return false;
            }
            // at least 10 trainings rounds should be created
            if (Integer.parseInt(args[2]) <= 10) {
                System.out.println("at least 10 trainings rounds should be created");
                return false;
            }
        } catch (NumberFormatException nfe) {
            // whenever no number is used for these parameters it is wrong
            System.out.println("no number provided at argument #2 or #3");
            return false;
        }
        if (!args[3].endsWith(".csv")) {
            System.out.println("file name need to end with CSV extension");
            return false;
        }
        return true;
    }

    public static List<Round> createAllPossibleRoundCombinations(boolean isSingleGame, String[] args) {
        List<Round> allCombinations = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();
        for(int i=4;i < args.length;i++) {
            Player player = new Player(args[i], i-3);
            allPlayers.add(player);
        }
        if (isSingleGame) {
            allPlayers.stream().forEach(currentPlayer -> {
                List<Player> remainingPlayers = new ArrayList<>();
                allPlayers.stream().filter(filterPlayer -> !currentPlayer.equals(filterPlayer)).forEach(remainingPlayers::add);
                remainingPlayers.stream().forEach(remainingPlayer -> {
                    Round currentRound = new Round();
                    currentRound.setPlayers(Arrays.asList(currentPlayer, remainingPlayer));
                    currentRound.sortPlayersInRoundByNumber();
                    if (!currentRoundExistsInAllCombinations(currentRound, allCombinations)) {
                        allCombinations.add(currentRound);
                    }
                });
            });
        } else {
            // check possible double game combinations here

        }
        return allCombinations;
    }

    private static boolean currentRoundExistsInAllCombinations(Round currentRoundSorted, List<Round> allCombinations) {
        if (allCombinations.isEmpty()) {
            return false;
        }
        return allCombinations.stream().anyMatch(round -> {
            boolean equalEntriesFound = round.getPlayers().get(0).getPlayerNumber() == currentRoundSorted.getPlayers().get(0).getPlayerNumber()
                    && round.getPlayers().get(1).getPlayerNumber() == currentRoundSorted.getPlayers().get(1).getPlayerNumber();
            if (round.getPlayers().size() == 2) {
                return equalEntriesFound;
            }
            return equalEntriesFound && round.getPlayers().get(2).getPlayerNumber() == currentRoundSorted.getPlayers().get(2).getPlayerNumber()
                    && round.getPlayers().get(3).getPlayerNumber() == currentRoundSorted.getPlayers().get(3).getPlayerNumber();
        });
    }

    public static void buildAndWriteCsvFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            // loop over trainingPlan (and write line for each round)
            for(int roundNo = 1; roundNo <= trainingsPlan.size(); roundNo++) {
                writer.append(Integer.toString(roundNo));
                writer.append(',');
                // write player data in each line

                // new line
                writer.append('\n');
            }
            // add a summary with count of how many times each player plays

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
