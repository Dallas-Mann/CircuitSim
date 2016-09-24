import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class OpAmp implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int newIndex;
	protected double gain;
	
	
	public OpAmp(String id, int nodeOne, int nodeTwo, int nodeThree, double gain){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.gain = gain;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + newIndex + " " + gain;
	}

	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		// TODO Auto-generated method stub
		// model the op amp as a voltange controlled voltage source at the output terminal and ground
		// the two input terminals are the control+ and control- for the VCVS at the output.
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		//int indexThree = nodeThree-1;
		//might need the above index to put values at (indexThree,newIndex) and (newIndex,indexThree)
		if(!(nodeOne == 0 || nodeThree == 0)){
			G.setReal(newIndex, indexOne, G.getReal(newIndex, indexOne) - 1);
		}
		if(!(nodeTwo == 0 || nodeThree == 0)){
			G.setReal(newIndex, indexTwo, G.getReal(newIndex, indexTwo) + 1);
		}
		
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
		return val;
	}

	@Override
	public int numCurrentsToAdd() {
		// always add a current equation because we are modeling the OpAmp as a VCVS
		return 1;
	}
}
