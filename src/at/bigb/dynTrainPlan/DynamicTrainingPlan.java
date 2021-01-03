package at.bigb.dynTrainPlan;

import at.bigb.dynTrainPlan.dao.Player;
import at.bigb.dynTrainPlan.dao.Round;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        int noOfCourtsToFill = Integer.parseInt(args[1]);
        int noOfRoundsToPlay = Integer.parseInt(args[2]);
        String fileName = args[3];
        // create ALL possible round combinations (depending on number of players)
        List<Round> allPossibleRoundCombinations = createAllPossibleRoundCombinations(isSingleGame, args);
        // optimize rounds, meaning least possible number of equal pairings and fewest possible rounds to pause
        trainingsPlan = getOptimizedTrainingsPlan(allPossibleRoundCombinations, isSingleGame, noOfCourtsToFill, noOfRoundsToPlay);
        // last step, create the trainingsPlan file
        buildAndWriteCsvFile(fileName);
    }

    private static List<Round> getOptimizedTrainingsPlan(List<Round> allCombinations, boolean isSingleGame, int noOfCourtsToFill, int noOfRoundsToPlay) {
        List<Round> finalTrainingsPlan = new ArrayList<>();
        // initially simply use the created rounds as results
        for(int roundNo=0;roundNo < noOfRoundsToPlay && allCombinations.get(roundNo) != null;roundNo++) {
            finalTrainingsPlan.add(allCombinations.get(roundNo));
        }
        return finalTrainingsPlan;
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
        // find all available players from incoming arguments
        for(int i=4;i < args.length;i++) {
            Player player = new Player(args[i], i-3);
            allPlayers.add(player);
        }
        // depending on game type determine all possible round combinations
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
        } else if (allPlayers.size() >= 4) {
            // check possible double game combinations here
            allPlayers.stream().forEach(currentPlayer -> {
                List<Player> remainingPlayers = new ArrayList<>();
                allPlayers.stream().filter(filterPlayer -> !currentPlayer.equals(filterPlayer)).forEach(remainingPlayers::add);
                if (remainingPlayers.size() == 3) {
                    // get three of the remaining players, to get a valid double constellation
                    Round currentRound = new Round();
                    currentRound.getPlayers().add(currentPlayer);
                    remainingPlayers.forEach(currentRound.getPlayers()::add);
                    currentRound.sortPlayersInRoundByNumber();
                    if (!currentRoundExistsInAllCombinations(currentRound, allCombinations)) {
                        allCombinations.add(currentRound);
                    }
                } else {
                    // always get 3 different players and build valid rounds for each combination
                    addDoubleCombinationsForCurrentPlayer(currentPlayer, remainingPlayers, allCombinations);
                }
            });
        }
        return allCombinations;
    }

    private static void addDoubleCombinationsForCurrentPlayer(Player currentPlayer, List<Player> remainingPlayers, List<Round> allCombinations) {
        // get all possible 3-entries combinations of the remaining players
        List<List<Player>> threePlayerCombos = getAllThreePlayerCombos(remainingPlayers);
        threePlayerCombos.forEach(threePlayers -> {
            Round currentRound = new Round();
            currentRound.getPlayers().add(currentPlayer);
            threePlayers.forEach(currentRound.getPlayers()::add);
            currentRound.sortPlayersInRoundByNumber();
            if (!currentRoundExistsInAllCombinations(currentRound, allCombinations)) {
                allCombinations.add(currentRound);
            }
        });
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
                Round currentRound = trainingsPlan.get(roundNo-1);
                AtomicInteger playerCount = currentRound.validDoubleRound() ? new AtomicInteger(4) : new AtomicInteger(2);
                currentRound.getPlayers().forEach(player -> {
                    playerCount.getAndDecrement();
                    try {
                        writer.append(player.getName() + (playerCount.get() > 0 ? "," : ""));
                    } catch(IOException ioInLambda) {
                        ioInLambda.printStackTrace();
                    }
                });
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

    private static List<List<Player>> getAllThreePlayerCombos(List<Player> remainingPlayers) {
        List<List<Player>> allThreePlayerCombos = new ArrayList<>();
        int player1Pos = 0, player2Pos = 1, player3Pos = 2;
        // TODO - remainingPlayer.size() is still WRONG here
        // 4 remaining players => 4 results (1*3 + 1) => 4
        // 5 remaining players => 9 results (2*3 + 2) => 8
        // 9 remaining players => 9 results (6*3 + 6) => 24
        for(int listPos=0;listPos < remainingPlayers.size();listPos++) {
            List<Player> threePlayerCombo = new ArrayList<>();
            threePlayerCombo.add(remainingPlayers.get(player1Pos));
            threePlayerCombo.add(remainingPlayers.get(player2Pos));
            threePlayerCombo.add(remainingPlayers.get(player3Pos));
            allThreePlayerCombos.add(new ArrayList<>());
            allThreePlayerCombos.get(listPos).addAll(threePlayerCombo);
            // reset the position values
            boolean posChanged = false;
            if (player3Pos < remainingPlayers.size()-1) {
                player3Pos++;
                posChanged = true;
            }
            if (!posChanged && player2Pos < (player3Pos-1)) {
                player2Pos++;
                posChanged = true;
            }
            if (!posChanged) {
                player1Pos++;
            }
        }
        return allThreePlayerCombos;
    }
}
