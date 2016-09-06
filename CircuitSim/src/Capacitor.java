import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class Capacitor implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected double capacitance;
	
	
	public Capacitor(String id, int nodeOne, int nodeTwo, double capacitance){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.capacitance = capacitance;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + capacitance;
	}

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(nodeOne == 0){
			C.set(indexTwo, indexTwo, G.get(indexTwo, indexTwo) + capacitance);
		}
		else if(nodeTwo == 0){
			C.set(indexOne, indexOne, G.get(indexOne, indexOne) + capacitance);
		}
		else{
			C.set(indexOne, indexOne, G.get(indexOne, indexOne) + capacitance);
			C.set(indexTwo, indexTwo, G.get(indexTwo, indexTwo) + capacitance);
			C.set(indexOne, indexTwo, G.get(indexOne, indexTwo) - capacitance);
			C.set(indexTwo, indexOne, G.get(indexTwo, indexOne) - capacitance);
		}
		// show changes in C Matrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + C.toString());
	}

	@Override
	public int numVoltagesToAdd(List<Integer> nodes) {
			return 0;
	}

	@Override
	public int numCurrentsToAdd() {
		return 0;
	}
}
