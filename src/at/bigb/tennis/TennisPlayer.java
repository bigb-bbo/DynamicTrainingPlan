package at.bigb.tennis;

public class TennisPlayer implements Comparable<TennisPlayer> {

  private int number;
  private String name;
  private int maxOccurences = 0;
  private int noOfOccurences = 0;

  public String getName() {
	return name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }
  
  public int getMaxOccurences() {
	return maxOccurences;
  }

  public void setMaxOccurences(int maxOccurences) {
	this.maxOccurences = maxOccurences;
  }

  public int getNoOfOccurences() {
	return noOfOccurences;
  }

  public void setNoOfOccurences(int noOfOccurences) {
	this.noOfOccurences = noOfOccurences;
  }

  public void increaseNoOfOccurencies() {
	  this.noOfOccurences++;
  }
  
  @Override
  public int compareTo(TennisPlayer p) {
    if (p == null) return 1;
    if (this.getNumber() < p.getNumber()) return -1;
    if (this.getNumber() > p.getNumber()) return 1;
    return 0;
  }
}
