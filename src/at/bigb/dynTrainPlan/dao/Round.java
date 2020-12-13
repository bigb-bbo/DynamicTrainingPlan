package at.bigb.dynTrainPlan.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * represents a single round of a training plan
 */
public class Round {

    private List<Player> players = new ArrayList<>();

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public boolean validSingleRound() {
        return players.size() == 2;
    }

    public boolean validDoubleRound() {
        return players.size() == 4;
    }

    public void sortPlayersInRoundByNumber() {
        this.players.sort(Comparator.comparingInt(Player::getPlayerNumber));
    }
}
