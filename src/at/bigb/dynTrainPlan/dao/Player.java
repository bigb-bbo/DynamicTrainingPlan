package at.bigb.dynTrainPlan.dao;

/**
 * contains data about a player that acts within the training plan
 */
public class Player {

    private String name;
    private int playCounter = 0;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayCounter() {
        return playCounter;
    }

    public void setPlayCounter(int playCounter) {
        this.playCounter = playCounter;
    }


}
