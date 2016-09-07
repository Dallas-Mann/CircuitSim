import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class MutualInductance implements Component {

	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int nodeFour;
	protected int newIndexOne;
	protected int newIndexTwo;
	
	public MutualInductance(String id, int nodeOne, int nodeTwo, int nodeThree, int nodeFour){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.nodeFour = nodeFour;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + newIndexOne + " " + newIndexTwo;
	}
	
	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		int indexFour = nodeFour-1;
		if(!(nodeOne == 0)){
			G.set(indexOne, newIndexOne, G.get(indexOne, newIndexOne) + 1);
			G.set(newIndexOne, indexOne, G.get(newIndexOne, indexOne) + 1);
		}
		if(!(nodeThree == 0)){
			G.set(indexThree, newIndexOne, G.get(indexThree, newIndexOne) + 1);
			G.set(newIndexOne, indexThree, G.get(newIndexOne, indexThree) + 1);
		}
		if(!(nodeTwo == 0)){
			G.set(indexTwo, newIndexOne, G.get(indexTwo, newIndexOne) - 1);
			G.set(newIndexOne, indexTwo, G.get(newIndexOne, indexTwo) - 1);
		}
		if(!(nodeFour == 0)){
			G.set(indexFour, newIndexOne, G.get(indexFour, newIndexOne) - 1);
			G.set(newIndexOne, indexFour, G.get(newIndexOne, indexFour) - 1);
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
		if(!nodes.contains(nodeThree)){
			nodes.add(nodeThree);
			val++;
		}
		if(!nodes.contains(nodeFour)){
			nodes.add(nodeFour);
			val++;
		}
		return val;
	}

	@Override
	public int numCurrentsToAdd() {
		// always return 2, the MutualInductance needs two current equations
		return 2;
	}
}
