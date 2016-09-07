import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class VCCS implements Component {
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int nodeFour;
	protected double gain;

	public VCCS(String id, int nodeOne, int nodeTwo, int nodeThree, int nodeFour, double gain){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.nodeFour = nodeFour;
		this.gain = gain;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + nodeFour + " " + gain;
	}
	
	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		int indexFour = nodeFour-1;
		if(!(nodeOne == 0 || nodeThree == 0)){
			G.set(indexThree, indexOne, G.get(indexThree, indexOne) + gain);
		}
		if(!(nodeTwo == 0 || nodeThree == 0)){
			G.set(indexThree, indexTwo, G.get(indexThree, indexTwo) - gain);
		}
		if(!(nodeOne == 0 || nodeFour == 0)){
			G.set(indexFour, indexOne, G.get(indexFour, indexOne) - gain);
		}
		if(!(nodeTwo == 0 || nodeFour == 0)){
			G.set(indexFour, indexTwo, G.get(indexFour, indexTwo) + gain);
		}
		
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + G.toString() + B.toString());
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
		// TODO Auto-generated method stub
		return 0;
	}

}
