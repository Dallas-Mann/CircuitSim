
public class Capacitor {
	String id;
	int nodeOne;
	int nodeTwo;
	double capacitance;
	
	
	public Capacitor(String id, int nodeOne, int nodeTwo, double capacitance){
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.capacitance = capacitance;
		this.id = id;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + capacitance;
	}
	
	public void insertStamp(){
		
	}
}
