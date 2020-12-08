package at.bigb.dynamicTrainingPlan_old;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@Deprecated
public class DynamicPlanGenerator {

	/** DEPRECATED - use DynamicSophisticatedPlanGenerator instead!!!!
	 * @param args
	 *   [0] "S"ingle or "D"ouble plan
	 *   [1] Number of courts to fill (only considered with single plans!)
	 *   [2] Number of rounds to play
	 *   [3] Number of valid equal pairings
     *   [4] Number of valid rounds to pause
	 *   [5] full fileName including directory
	 *   [6..n] Player names (use underscore to separate first and last name)
	 */
	public static void main(String[] args) {
	  HashMap<Integer, ArrayList<Player>> trainingPlan;
	  ArrayList<Player> players = new ArrayList<Player>();
	  boolean singleGame = false;
		// check valid number of args 
	  if (args.length < 7) {
	    System.out.println("Error - Invalid number of arguments!");
	    return;
	  }
	  // check special args
	  if (!"S".startsWith(args[0]) && !"D".startsWith(args[0])) {
      System.out.println("Error - Invalid plan style (Single or Double allowed)!");
      return;
	  } else if ("S".startsWith(args[0])) {
	    singleGame = true;
	  }
	  int noOfCourtsToFill = 0;
	  int noOfRoundsToPlay = 0;
	  int noOfValidEqualPairings = 0;
      int noOfValidRoundsToPause = 0;
	  String fullFileName = "";
	  // transform arguments to number
	  try {
	    noOfCourtsToFill = Integer.valueOf(args[1]);
	    noOfRoundsToPlay = Integer.valueOf(args[2]);
	    noOfValidEqualPairings = Integer.valueOf(args[3]);
        noOfValidRoundsToPause = Integer.valueOf(args[4]);
	    fullFileName = args[5];
	  } catch (Exception ex1) {
	    System.out.println("Error - Incorrect number format somewhere at args1-4 or wrong fileName format (args5)!");
	    return;
	  }
	  // get players entered
	  for (int playerNo = 6;playerNo < args.length;playerNo++) {
	    Player currPlayer = new Player();
	    currPlayer.setName(args[playerNo].replaceAll("_", " "));
	    players.add(currPlayer);
	  }
	  // shuffle players
	  Collections.shuffle(players);
	  // set correct number of players
	  for (int cnt = 0;cnt < players.size();cnt++) {
	    players.get(cnt).setNumber(cnt);
	  }
	  // generate a dynamic plan for given players
	  trainingPlan = new HashMap<Integer, ArrayList<Player>>();
	  DynamicPlanGenerator.generateDynamicPlan(players, noOfCourtsToFill, noOfRoundsToPlay, noOfValidEqualPairings, noOfValidRoundsToPause, trainingPlan, singleGame);
	  // save result to csv-file
	  DynamicPlanGenerator.generateCsvFile(trainingPlan, fullFileName, players, singleGame);
	}
	
	private static void generateDynamicPlan(ArrayList<Player> players, int noOfCourtsToFill, int noOfRoundsToPlay, 
	    int noOfValidEqualPairings, int noOfValidRoundsToPause, HashMap<Integer, ArrayList<Player>> trainingPlan, boolean singleGame) {
	  for(int roundNo = 1; roundNo <= noOfRoundsToPlay; roundNo++) {
	    boolean criteriasFullfilled = false;
	    ArrayList<Player> currentPlayers = null;
	    while (!criteriasFullfilled) {
	      // initialize currentPlayers (if criteria were not full filled, empty list)
	      currentPlayers = new ArrayList<Player>();
	  	  // first mix available players each time
	      Collections.shuffle(players);
	      // now try to create pairings with first players available
	      if (!singleGame) {
	        currentPlayers.add(players.get(0));
	    	currentPlayers.add(players.get(1));
	    	currentPlayers.add(players.get(2));
	    	currentPlayers.add(players.get(3));
	      } else {
	    	int playerNo = 0;
	    	for(int courtNo = 0;courtNo < noOfCourtsToFill;courtNo++) {
	    	   currentPlayers.add(players.get(playerNo+courtNo));
	    	   currentPlayers.add(players.get(playerNo+courtNo+1));
	           playerNo += 2;
	        }
	      }
	      // check if pairings where the not same last round and noOfValidEqualPairings not exceeded
	      criteriasFullfilled = pairingNotTheSameAsLastRound(roundNo, trainingPlan, currentPlayers, noOfCourtsToFill, singleGame) 
	    	                        && noOfValidEqualPairingsNotExceeded(roundNo, noOfValidEqualPairings, noOfCourtsToFill, trainingPlan, currentPlayers, singleGame)
	    	                        && noOfValidRoundsToPauseNotExceeded(roundNo, noOfValidRoundsToPause, trainingPlan, players, currentPlayers);
	    }
	   	trainingPlan.put(roundNo, currentPlayers);
	  }
	}
	
	private static boolean pairingNotTheSameAsLastRound(int roundNo, HashMap<Integer, ArrayList<Player>> trainingPlan, ArrayList<Player> currentPlayers, int noOfCourtsToFill, boolean singleGame) {
	  if (roundNo == 1) return true;
      // get players of last round
      ArrayList<Player> lastPlayers = trainingPlan.get(roundNo - 1);
	  if (!singleGame) {
  	    // bring list of each round to a standard order (over Player.number)
  	    ArrayList<Integer> lastPlayerNumbersSorted = getSortedPlayerNumbers(lastPlayers);
  	    ArrayList<Integer> currentPlayerNumbersSorted = getSortedPlayerNumbers(currentPlayers);
  	    // return if list match exactly
  	    return !lastPlayerNumbersSorted.equals(currentPlayerNumbersSorted);
	  } else {
        // get all pairings of last and this round
	    HashMap<Integer, ArrayList<Player>> lastRoundsPairings = new HashMap<Integer, ArrayList<Player>>();
	    HashMap<Integer, ArrayList<Player>> thisRoundsPairings = new HashMap<Integer, ArrayList<Player>>();
	    int posNumber = 0;
        for(int courtNo = 0;courtNo < noOfCourtsToFill;courtNo = courtNo+2) {
          ArrayList<Player> lastRoundsPairing = new ArrayList<Player>();
          ArrayList<Player> thisRoundsPairing = new ArrayList<Player>();
	      // get the current pairings
	      lastRoundsPairing.add(lastPlayers.get(courtNo));
	      lastRoundsPairing.add(lastPlayers.get(courtNo+1));
	      thisRoundsPairing.add(currentPlayers.get(courtNo));
	      thisRoundsPairing.add(currentPlayers.get(courtNo+1));
	      // sort the pairings
	      Collections.sort(lastRoundsPairing);
	      Collections.sort(thisRoundsPairing);
	      // add to hashMap
	      lastRoundsPairings.put(posNumber, lastRoundsPairing);
	      thisRoundsPairings.put(posNumber, thisRoundsPairing);
	      posNumber++;
        }
	    // check if pairing of thisRound exists in pairing of lastRound, then exit
        for(int pairingNo = 0;pairingNo < thisRoundsPairings.size();pairingNo++) {
          ArrayList<Player> currentPlayerPair = thisRoundsPairings.get(pairingNo);
          for(int lastPosNo = 0;lastPosNo < lastRoundsPairings.size();lastPosNo++) {
            ArrayList<Player> lastPlayerPair = lastRoundsPairings.get(lastPosNo);
            if (lastPlayerPair.equals(currentPlayerPair)) return false;
          }
        }
        return true;
	  }
	}
	
	private static ArrayList<Integer> getSortedPlayerNumbers(ArrayList<Player> players) {
	  ArrayList<Integer> sortedPlayerNumbers = new ArrayList<Integer>();
	  for (Player currPlayer : players) {
	    sortedPlayerNumbers.add(currPlayer.getNumber()); 
	  }
	  Collections.sort(sortedPlayerNumbers);
	  return sortedPlayerNumbers;
	}
	
  private static boolean noOfValidEqualPairingsNotExceeded(int roundNo, int noOfValidEqualPairings, int noOfCourtsToFill, HashMap<Integer, ArrayList<Player>> trainingPlan, ArrayList<Player> currentPlayers, boolean singleGame) {
    if (roundNo == 1) return true;
    if (!singleGame) {
      // compare current double with all previous doubles
      int previousDoubleCount = 0;
      for(int prevRoundNo = 1; prevRoundNo <= trainingPlan.size(); prevRoundNo++) {
        ArrayList<Player> prevPlayers = trainingPlan.get(prevRoundNo);
        // bring list of rounds to a standard order (over Player.number)
        ArrayList<Integer> prevPlayerNumbersSorted = getSortedPlayerNumbers(prevPlayers);
        ArrayList<Integer> currentPlayerNumbersSorted = getSortedPlayerNumbers(currentPlayers);        
        if (currentPlayerNumbersSorted.equals(prevPlayerNumbersSorted)) previousDoubleCount++;
      }
      return previousDoubleCount <= noOfValidEqualPairings-1;
    } else {
      // compare this rounds pairings with all previous - if count is too much exit!
      HashMap<Integer, ArrayList<Player>> thisRoundsPairings = new HashMap<Integer, ArrayList<Player>>();
      int posNumber = 0;
      for(int courtNo=0;courtNo < noOfCourtsToFill;courtNo = courtNo+2) {
        ArrayList<Player> thisRoundsPairing = new ArrayList<Player>();
        thisRoundsPairing.add(currentPlayers.get(courtNo));
        thisRoundsPairing.add(currentPlayers.get(courtNo+1));
        Collections.sort(thisRoundsPairing);
        thisRoundsPairings.put(posNumber, thisRoundsPairing);
        posNumber++;
      }
      // check each of the current pairings
      for(int pairingNo=0;pairingNo < thisRoundsPairings.size();pairingNo++) {
        ArrayList<Player> currentPairing = thisRoundsPairings.get(pairingNo);
        int previousPairingCount = 0;
        // loop through all previous rounds and count occurrences of currentPairing
        for(int prevRoundNo = 1; prevRoundNo <= trainingPlan.size(); prevRoundNo++) {
          ArrayList<Player> allPlayersPreviousRound = trainingPlan.get(prevRoundNo);
          for(int courtNo=0;courtNo < noOfCourtsToFill;courtNo = courtNo+2) {
            ArrayList<Player> previousRoundsPairing = new ArrayList<Player>();
            previousRoundsPairing.add(allPlayersPreviousRound.get(courtNo));
            previousRoundsPairing.add(allPlayersPreviousRound.get(courtNo+1));
            Collections.sort(previousRoundsPairing);
            if (previousRoundsPairing.equals(currentPairing)) previousPairingCount++;
          }
        }
        if (previousPairingCount > noOfValidEqualPairings-1) return false;
      }      
      return true;
    }
  }

  private static boolean noOfValidRoundsToPauseNotExceeded(int roundNo, int noOfValidRoundsToPause, HashMap<Integer, ArrayList<Player>> trainingPlan, ArrayList<Player> players, ArrayList<Player> currentPlayers) {
    if (roundNo <= noOfValidRoundsToPause) return true;
    // get all the players not active in current round
    ArrayList<Player> notActivePlayers = new ArrayList<Player>();
    for(Player checkPlayer : players) {
      if (!currentPlayers.contains(checkPlayer)) notActivePlayers.add(checkPlayer);
    }
    // get all the players of last round(s)
    ArrayList<Player> lastRoundsPlayers = new ArrayList<Player>();
    for(int prevRoundNo = trainingPlan.size(); prevRoundNo > trainingPlan.size()-noOfValidRoundsToPause; prevRoundNo--) {
      ArrayList<Player> prevPlayers = trainingPlan.get(prevRoundNo);
      for(Player prevPlayer : prevPlayers) {
        if (!lastRoundsPlayers.contains(prevPlayer)) lastRoundsPlayers.add(prevPlayer);
      }
    }
    // check if each not active player played within the last rounds
    for(Player notActivePlayer : notActivePlayers) {
      if (!lastRoundsPlayers.contains(notActivePlayer)) return false;
    }
    // reached this point - all OK
    return true;
  }

	private static void generateCsvFile(HashMap<Integer, ArrayList<Player>> trainingPlan, String fullFileName, ArrayList<Player> players, boolean singleGame) {
	  try {
       BufferedWriter writer = new BufferedWriter
       (new OutputStreamWriter(new FileOutputStream(fullFileName),"UTF-8"));
       // loop over trainingPlan (and write line for each round)
       for(int roundNo = 1; roundNo <= trainingPlan.size(); roundNo++) {
         writer.append(Integer.toString(roundNo));
         writer.append(',');
         ArrayList<Player> roundPlayers = trainingPlan.get(roundNo);
         if (roundPlayers != null) {
           if (!singleGame) {
        	 // sort players only in double plan
             roundPlayers.sort(new Comparator<Player>() {
               @Override
			   public int compare(Player o1, Player o2) {
				 return o1.getName().compareTo(o2.getName());
			   }
		     });
           }
           for(Player currPlayer : roundPlayers) {
             writer.append(currPlayer.getName());
             writer.append(',');
           }
         }
         // new line
         writer.append('\n');
       }
       writer.append("\n\n");
       // print summary of players and their number of games
       for(Player currPlayer : players) {
         writer.append(currPlayer.getName());
         writer.append(',');
         // get number of times played
         writer.append(Integer.toString(getNumberOfTimesPlayed(currPlayer, trainingPlan)));
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
	
	private static int getNumberOfTimesPlayed(Player player, HashMap<Integer, ArrayList<Player>> trainingPlan) {
	  int numberOfTimesPlayed = 0;
	  for(int roundNo = 1; roundNo <= trainingPlan.size(); roundNo++) {
	    ArrayList<Player> roundPlayers = trainingPlan.get(roundNo);
	    if (roundPlayers.contains(player)) numberOfTimesPlayed++;
	  }
	  return numberOfTimesPlayed;
	}
}