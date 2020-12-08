package at.bigb.tennis;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DynamicSophisticatedPlanGenerator {

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
	  HashMap<Integer, ArrayList<TennisPlayer>> trainingPlan;
	  ArrayList<TennisPlayer> players = new ArrayList<TennisPlayer>();
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
	  // get TennisPlayers entered
	  for (int playerNo = 6;playerNo < args.length;playerNo++) {
	    TennisPlayer currTennisPlayer = new TennisPlayer();
	    String playerData = args[playerNo];
	    currTennisPlayer.setName(playerData.split("\\!")[0].replaceAll("_", " "));
	    if (playerData.indexOf('!') > -1) {
	      currTennisPlayer.setMaxOccurences(Integer.parseInt(playerData.split("\\!")[1]));
	    }
	    players.add(currTennisPlayer);
	  }
	  // shuffle TennisPlayers
	  Collections.shuffle(players);
	  // set correct number of TennisPlayers
	  for (int cnt = 0;cnt < players.size();cnt++) {
	    players.get(cnt).setNumber(cnt);
	  }
	  // generate a dynamic plan for given TennisPlayers
	  trainingPlan = new HashMap<Integer, ArrayList<TennisPlayer>>();
	  DynamicSophisticatedPlanGenerator.generateDynamicPlan(players, noOfCourtsToFill, noOfRoundsToPlay, noOfValidEqualPairings, noOfValidRoundsToPause, trainingPlan, singleGame);
	  // save result to csv-file
	  DynamicSophisticatedPlanGenerator.generateCsvFile(trainingPlan, fullFileName, players, singleGame);
	}
	
	private static void generateDynamicPlan(ArrayList<TennisPlayer> players, int noOfCourtsToFill, int noOfRoundsToPlay, 
	    int noOfValidEqualPairings, int noOfValidRoundsToPause, HashMap<Integer, ArrayList<TennisPlayer>> trainingPlan, boolean singleGame) {
	  for(int roundNo = 1; roundNo <= noOfRoundsToPlay; roundNo++) {
	    boolean criteriasFullfilled = false;
	    ArrayList<TennisPlayer> currentPlayers = null;
	    while (!criteriasFullfilled) {
	      System.out.println("round: " + roundNo);
	      // initialize currentTennisPlayers (if criteria were not full filled, empty list)
	      currentPlayers = new ArrayList<TennisPlayer>();
	  	  // first mix available TennisPlayers each time
	      Collections.shuffle(players);
	      // now try to create pairings with first TennisPlayers available
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
	    	                        && noOfValidRoundsToPauseNotExceeded(roundNo, noOfValidRoundsToPause, trainingPlan, players, currentPlayers)
	    	                        && notMaxOccurencesOfPlayersExceeded(currentPlayers);
	    }
	    currentPlayers.forEach(TennisPlayer::increaseNoOfOccurencies);
	   	trainingPlan.put(roundNo, currentPlayers);
	  }
	}
	
	private static boolean notMaxOccurencesOfPlayersExceeded(ArrayList<TennisPlayer> currentPlayers) {		
		return currentPlayers.stream().allMatch(player -> player.getMaxOccurences() == 0 || 
				player.getNoOfOccurences() < player.getMaxOccurences());
	}

	private static boolean pairingNotTheSameAsLastRound(int roundNo, HashMap<Integer, ArrayList<TennisPlayer>> trainingPlan, ArrayList<TennisPlayer> currentTennisPlayers, int noOfCourtsToFill, boolean singleGame) {
	  if (roundNo == 1) return true;
      // get TennisPlayers of last round
      ArrayList<TennisPlayer> lastTennisPlayers = trainingPlan.get(roundNo - 1);
	  if (!singleGame) {
  	    // bring list of each round to a standard order (over TennisPlayer.number)
  	    ArrayList<Integer> lastTennisPlayerNumbersSorted = getSortedPlayerNumbers(lastTennisPlayers);
  	    ArrayList<Integer> currentTennisPlayerNumbersSorted = getSortedPlayerNumbers(currentTennisPlayers);
  	    // return if list match exactly
  	    return !lastTennisPlayerNumbersSorted.equals(currentTennisPlayerNumbersSorted);
	  } else {
        // get all pairings of last and this round
	    HashMap<Integer, ArrayList<TennisPlayer>> lastRoundsPairings = new HashMap<Integer, ArrayList<TennisPlayer>>();
	    HashMap<Integer, ArrayList<TennisPlayer>> thisRoundsPairings = new HashMap<Integer, ArrayList<TennisPlayer>>();
	    int posNumber = 0;
        for(int courtNo = 0;courtNo < noOfCourtsToFill;courtNo = courtNo+2) {
          ArrayList<TennisPlayer> lastRoundsPairing = new ArrayList<TennisPlayer>();
          ArrayList<TennisPlayer> thisRoundsPairing = new ArrayList<TennisPlayer>();
	      // get the current pairings
	      lastRoundsPairing.add(lastTennisPlayers.get(courtNo));
	      lastRoundsPairing.add(lastTennisPlayers.get(courtNo+1));
	      thisRoundsPairing.add(currentTennisPlayers.get(courtNo));
	      thisRoundsPairing.add(currentTennisPlayers.get(courtNo+1));
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
          ArrayList<TennisPlayer> currentTennisPlayerPair = thisRoundsPairings.get(pairingNo);
          for(int lastPosNo = 0;lastPosNo < lastRoundsPairings.size();lastPosNo++) {
            ArrayList<TennisPlayer> lastTennisPlayerPair = lastRoundsPairings.get(lastPosNo);
            if (lastTennisPlayerPair.equals(currentTennisPlayerPair)) return false;
          }
        }
        return true;
	  }
	}
	
	private static ArrayList<Integer> getSortedPlayerNumbers(ArrayList<TennisPlayer> players) {
	  ArrayList<Integer> sortedPlayerNumbers = new ArrayList<Integer>();
	  for (TennisPlayer currPlayer : players) {
	    sortedPlayerNumbers.add(currPlayer.getNumber()); 
	  }
	  Collections.sort(sortedPlayerNumbers);
	  return sortedPlayerNumbers;
	}
	
  private static boolean noOfValidEqualPairingsNotExceeded(int roundNo, int noOfValidEqualPairings, int noOfCourtsToFill, HashMap<Integer, ArrayList<TennisPlayer>> trainingPlan, ArrayList<TennisPlayer> currentPlayers, boolean singleGame) {
    if (roundNo == 1) return true;
    if (!singleGame) {
      // compare current double with all previous doubles
      int previousDoubleCount = 0;
      for(int prevRoundNo = 1; prevRoundNo <= trainingPlan.size(); prevRoundNo++) {
        ArrayList<TennisPlayer> prevPlayers = trainingPlan.get(prevRoundNo);
        // bring list of rounds to a standard order (over TennisPlayer.number)
        ArrayList<Integer> prevPlayerNumbersSorted = getSortedPlayerNumbers(prevPlayers);
        ArrayList<Integer> currentPlayerNumbersSorted = getSortedPlayerNumbers(currentPlayers);        
        if (currentPlayerNumbersSorted.equals(prevPlayerNumbersSorted)) previousDoubleCount++;
      }
      return previousDoubleCount <= noOfValidEqualPairings-1;
    } else {
      // compare this rounds pairings with all previous - if count is too much exit!
      HashMap<Integer, ArrayList<TennisPlayer>> thisRoundsPairings = new HashMap<Integer, ArrayList<TennisPlayer>>();
      int posNumber = 0;
      for(int courtNo=0;courtNo < noOfCourtsToFill;courtNo = courtNo+2) {
        ArrayList<TennisPlayer> thisRoundsPairing = new ArrayList<TennisPlayer>();
        thisRoundsPairing.add(currentPlayers.get(courtNo));
        thisRoundsPairing.add(currentPlayers.get(courtNo+1));
        Collections.sort(thisRoundsPairing);
        thisRoundsPairings.put(posNumber, thisRoundsPairing);
        posNumber++;
      }
      // check each of the current pairings
      for(int pairingNo=0;pairingNo < thisRoundsPairings.size();pairingNo++) {
        ArrayList<TennisPlayer> currentPairing = thisRoundsPairings.get(pairingNo);
        int previousPairingCount = 0;
        // loop through all previous rounds and count occurrences of currentPairing
        for(int prevRoundNo = 1; prevRoundNo <= trainingPlan.size(); prevRoundNo++) {
          ArrayList<TennisPlayer> allPlayersPreviousRound = trainingPlan.get(prevRoundNo);
          for(int courtNo=0;courtNo < noOfCourtsToFill;courtNo = courtNo+2) {
            ArrayList<TennisPlayer> previousRoundsPairing = new ArrayList<TennisPlayer>();
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

  private static boolean noOfValidRoundsToPauseNotExceeded(int roundNo, int noOfValidRoundsToPause, HashMap<Integer, ArrayList<TennisPlayer>> trainingPlan, ArrayList<TennisPlayer> players, ArrayList<TennisPlayer> currentPlayers) {
    if (roundNo <= noOfValidRoundsToPause) return true;
    // get all the players not active in current round
    ArrayList<TennisPlayer> notActivePlayers = new ArrayList<TennisPlayer>();
    for(TennisPlayer checkPlayer : players) {
      if (!currentPlayers.contains(checkPlayer)) notActivePlayers.add(checkPlayer);
    }
    // get all the TennisPlayers of last round(s)
    ArrayList<TennisPlayer> lastRoundsPlayers = new ArrayList<TennisPlayer>();
    for(int prevRoundNo = trainingPlan.size(); prevRoundNo > trainingPlan.size()-noOfValidRoundsToPause; prevRoundNo--) {
      ArrayList<TennisPlayer> prevPlayers = trainingPlan.get(prevRoundNo);
      for(TennisPlayer prevPlayer : prevPlayers) {
        if (!lastRoundsPlayers.contains(prevPlayer)) lastRoundsPlayers.add(prevPlayer);
      }
    }
    // check if each not active TennisPlayer played within the last rounds
    for(TennisPlayer notActivePlayer : notActivePlayers) {
      if (!lastRoundsPlayers.contains(notActivePlayer)) return false;
    }
    // reached this point - all OK
    return true;
  }

	private static void generateCsvFile(HashMap<Integer, ArrayList<TennisPlayer>> trainingPlan, String fullFileName, ArrayList<TennisPlayer> players, boolean singleGame) {
	  try {
       BufferedWriter writer = new BufferedWriter
       (new OutputStreamWriter(new FileOutputStream(fullFileName),"UTF-8"));
       // loop over trainingPlan (and write line for each round)
       for(int roundNo = 1; roundNo <= trainingPlan.size(); roundNo++) {
         writer.append(Integer.toString(roundNo));
         writer.append(',');
         ArrayList<TennisPlayer> roundPlayers = trainingPlan.get(roundNo);
         if (roundPlayers != null) {
           if (!singleGame) {
        	 // sort players only in double plan
             roundPlayers.sort(new Comparator<TennisPlayer>() {
               @Override
			   public int compare(TennisPlayer o1, TennisPlayer o2) {
				 return o1.getName().compareTo(o2.getName());
			   }
		     });
           }
           for(TennisPlayer currPlayer : roundPlayers) {
             writer.append(currPlayer.getName());
             writer.append(',');
           }
         }
         // new line
         writer.append('\n');
       }
       writer.append("\n\n");
       // print summary of players and their number of games
       for(TennisPlayer currPlayer : players) {
         writer.append(currPlayer.getName());
         writer.append(',');
         // get number of times played
         writer.append(Integer.toString(currPlayer.getNoOfOccurences()));
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
}