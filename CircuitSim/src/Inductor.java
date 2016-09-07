import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class Inductor implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int newIndex;
	protected double inductance;
	
	public Inductor(String id, int nodeOne, int nodeTwo, double inductance){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.inductance = inductance;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + newIndex + " " + inductance;
	}

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(!(nodeOne == 0)){
			G.set(indexOne, newIndex, G.get(indexOne, newIndex) + 1);
			G.set(newIndex, indexOne, G.get(newIndex, indexOne) + 1);
		}
		else if(!(nodeTwo == 0)){
			G.set(indexTwo, newIndex, G.get(indexTwo, indexTwo) - 1);
			G.set(newIndex, indexTwo, G.get(newIndex, indexTwo) - 1);
		}
		C.set(newIndex, newIndex, C.get(newIndex, newIndex) - inductance);
		
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + G.toString() + C.toString());
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
		// inductor always adds a current equation to the G matrix
		return 1;
	}
}
