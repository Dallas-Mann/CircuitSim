
public class Inductor {
	String id;
	int nodeOne;
	int nodeTwo;
	double inductance;
	
	public Inductor(String id, int nodeOne, int nodeTwo, double inductance){
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.inductance = inductance;
		this.id = id;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + inductance;
	}
	
	public void insertStamp(){
		
	}
}
