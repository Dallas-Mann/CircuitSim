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
		//testing the set method for the SimpleMatrix below, not correct
		//G.set(nodeOne, nodeTwo, G.get(nodeOne, nodeTwo) + (1.0/resistance));
		
		// show changes in SimpleMatrix to debug
		System.out.println(G.toString());
	}
}
