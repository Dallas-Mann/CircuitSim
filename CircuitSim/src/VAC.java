import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class VAC implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int newIndex;
	protected double amplitude;
	protected double frequency;
	protected double minFrequency;
	protected double maxFrequency;
	protected int numSteps;

	public VAC(String id, int nodeOne, int nodeTwo, double amplitude, double frequency, double minFrequency, double maxFrequency, int numSteps){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.numSteps = numSteps;
	}
	
	public String toString(){
		//TODO add other parameters to this.
		return id + " " + nodeOne + " " + nodeTwo + " " + newIndex + " " + amplitude;
	}

	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		
		if(!(nodeOne == 0)){
			G.setReal(indexOne, newIndex, G.getReal(indexOne, newIndex) + 1);
			G.setReal(newIndex, indexOne, G.getReal(newIndex, indexOne) + 1);
		}
		if(!(nodeTwo == 0)){
			G.setReal(indexTwo, newIndex, G.getReal(indexTwo, newIndex) - 1);
			G.setReal(newIndex, indexTwo, G.getReal(newIndex, indexTwo) - 1);
		}
		B.setReal(newIndex, 0, B.getReal(newIndex, 0) + amplitude);
		
		/*
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id);
		G.print();
		B.print();
		*/
	}

	public int getNewIndex(){
		return newIndex;
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
		// dependent voltage source always adds a current equation to the G matrix
		return 1;
	}
}
