import org.ejml.simple.SimpleMatrix;

public class Resistor implements Component{
	String id;
	int nodeOne;
	int nodeTwo;
	double resistance;
	
	public Resistor(String id, int nodeOne, int nodeTwo, double resistance){
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.resistance = resistance;
		this.id = id;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + resistance;
	}

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// TODO Auto-generated method stub
		double conductance = 1.0/resistance;
		G.set(nodeOne, nodeOne, G.get(nodeOne, nodeOne) + conductance);
		G.set(nodeTwo, nodeTwo, G.get(nodeTwo, nodeTwo) + conductance);
		G.set(nodeOne, nodeTwo, G.get(nodeOne, nodeTwo) - conductance);
		G.set(nodeTwo, nodeOne, G.get(nodeTwo, nodeOne) - conductance);
		
		// show changes in SimpleMatrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + G.toString());
	}
}
