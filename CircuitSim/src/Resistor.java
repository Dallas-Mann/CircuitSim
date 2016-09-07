import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class Resistor implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected double resistance;
	
	public Resistor(String id, int nodeOne, int nodeTwo, double resistance){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.resistance = resistance;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + resistance;
	}

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		double conductance = 1.0/resistance;
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(nodeOne == 0){
			G.set(indexTwo, indexTwo, G.get(indexTwo, indexTwo) + conductance);
		}
		else if(nodeTwo == 0){
			G.set(indexOne, indexOne, G.get(indexOne, indexOne) + conductance);
		}
		else{
			G.set(indexOne, indexOne, G.get(indexOne, indexOne) + conductance);
			G.set(indexTwo, indexTwo, G.get(indexTwo, indexTwo) + conductance);
			G.set(indexOne, indexTwo, G.get(indexOne, indexTwo) - conductance);
			G.set(indexTwo, indexOne, G.get(indexTwo, indexOne) - conductance);
		}
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + G.toString());
	}

	@Override
	public int numVoltagesToAdd(List<Integer> nodes) {
		int val = 0;
		if(!nodes.contains(nodeOne)){
			nodes.add(nodeOne);
			val++;
		}
		if(!nodes.contains(nodeTwo)){
			nodes.add(nodeTwo);
			val++;
		}
		return val;
	}

	@Override
	public int numCurrentsToAdd() {
		return 0;
	}
}
