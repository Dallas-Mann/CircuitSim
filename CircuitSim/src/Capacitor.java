import org.ejml.simple.SimpleMatrix;

public class Capacitor implements Component{
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

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// TODO Auto-generated method stub
		
	}
}
