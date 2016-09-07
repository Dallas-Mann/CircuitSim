import java.util.List;

import org.ejml.simple.SimpleMatrix;

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
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// TODO Auto-generated method stub
		// model the op amp as a voltange controlled voltage source at the output terminal and ground
		// the two input terminals are the control+ and control- for the VCVS at the output.
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		/*
		if(!(nodeOne == 0 || nodeTwo == 0)){
			G.set(newIndex, indexOne, G.get(newIndex, indexOne) - gain);
			G.set(newIndex, indexTwo, G.get(newIndex, indexTwo) + gain);
		}
		if(!(nodeThree == 0)){
			G.set(newIndex, indexThree, G.get(newIndex, indexThree) + 1);
			G.set(indexThree, newIndex, G.get(indexThree, newIndex) + 1);
		}
		if(!(nodeFour == 0)){
			G.set(newIndex, indexFour, G.get(newIndex, indexFour) - 1);
			G.set(indexFour, newIndex, G.get(indexFour, newIndex) - 1);
		}
		
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + G.toString() + C.toString());
		*/
	}

	@Override
	public int numVoltagesToAdd(List<Integer> nodes) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numCurrentsToAdd() {
		// TODO Auto-generated method stub
		return 0;
	}

}
