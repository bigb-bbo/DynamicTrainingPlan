package at.bigb.dynamicTrainingPlan_old;

public class Player implements Comparable<Player> {

  private int number;
	private String name;

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
  
  @Override
  public int compareTo(Player p) {
    if (p == null) return 1;
    if (this.getNumber() < p.getNumber()) return -1;
    if (this.getNumber() > p.getNumber()) return 1;
    return 0;
  }
}
