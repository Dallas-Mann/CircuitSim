import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class MutualInductance implements Component {

	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int nodeFour;
	protected int newIndexOne;
	protected int newIndexTwo;
	protected double inductanceOne;
	protected double inductanceTwo;
	protected double inductanceCoupled;
	
	public MutualInductance(String id, int nodeOne, int nodeTwo, int nodeThree, int nodeFour, double inductanceOne, double inductanceTwo, double inductanceCoupled){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.nodeFour = nodeFour;
		this.inductanceOne = inductanceOne;
		this.inductanceTwo = inductanceTwo;
		this.inductanceCoupled = inductanceCoupled;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + newIndexOne + " " + newIndexTwo;
	}
	
	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		int indexFour = nodeFour-1;
		if(!(nodeOne == 0)){
			G.setReal(indexOne, newIndexOne, G.getReal(indexOne, newIndexOne) + 1);
			G.setReal(newIndexOne, indexOne, G.getReal(newIndexOne, indexOne) + 1);
		}
		if(!(nodeThree == 0)){
			G.setReal(indexThree, newIndexOne, G.getReal(indexThree, newIndexOne) + 1);
			G.setReal(newIndexOne, indexThree, G.getReal(newIndexOne, indexThree) + 1);
		}
		if(!(nodeTwo == 0)){
			G.setReal(indexTwo, newIndexOne, G.getReal(indexTwo, newIndexOne) - 1);
			G.setReal(newIndexOne, indexTwo, G.getReal(newIndexOne, indexTwo) - 1);
		}
		if(!(nodeFour == 0)){
			G.setReal(indexFour, newIndexOne, G.getReal(indexFour, newIndexOne) - 1);
			G.setReal(newIndexOne, indexFour, G.getReal(newIndexOne, indexFour) - 1);
		}
		C.setReal(newIndexOne, newIndexOne, C.getReal(newIndexOne, newIndexOne) - inductanceOne);
		C.setReal(newIndexTwo, newIndexTwo, C.getReal(newIndexTwo, newIndexTwo) - inductanceTwo);
		C.setReal(newIndexOne, newIndexTwo, C.getReal(newIndexOne, newIndexTwo) - inductanceCoupled);
		C.setReal(newIndexTwo, newIndexOne, C.getReal(newIndexTwo, newIndexOne) - inductanceCoupled);
		
		/*
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id);
		G.print();
		*/
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
