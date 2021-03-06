import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class VCVS implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int nodeFour;
	protected int newIndex;
	protected double gain;

	public VCVS(String id, int nodeOne, int nodeTwo, int nodeThree, int nodeFour, double gain){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.nodeFour = nodeFour;
		this.gain = gain;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + nodeFour + " " + newIndex + " " + gain;
	}
	
	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		int indexFour = nodeFour-1;
		if(!(nodeOne == 0 || nodeTwo == 0)){
			G.setReal(newIndex, indexOne, G.getReal(newIndex, indexOne) - gain);
			G.setReal(newIndex, indexTwo, G.getReal(newIndex, indexTwo) + gain);
		}
		if(!(nodeThree == 0)){
			G.setReal(newIndex, indexThree, G.getReal(newIndex, indexThree) + 1);
			G.setReal(indexThree, newIndex, G.getReal(indexThree, newIndex) + 1);
		}
		if(!(nodeFour == 0)){
			G.setReal(newIndex, indexFour, G.getReal(newIndex, indexFour) - 1);
			G.setReal(indexFour, newIndex, G.getReal(indexFour, newIndex) - 1);
		}
		
		/*
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id);
		G.print();
		C.print();
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
		return 1;
	}
}
