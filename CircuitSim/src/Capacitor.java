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
		C.set(nodeOne, nodeOne, C.get(nodeOne, nodeOne) + capacitance);
		C.set(nodeTwo, nodeTwo, C.get(nodeTwo, nodeTwo) + capacitance);
		C.set(nodeOne, nodeTwo, C.get(nodeOne, nodeTwo) - capacitance);
		C.set(nodeTwo, nodeOne, C.get(nodeTwo, nodeOne) - capacitance);
		
		// I think I need to modify the X matrix as well
		// will come back to this
		
		// show changes in SimpleMatrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + C.toString());
	}
}
