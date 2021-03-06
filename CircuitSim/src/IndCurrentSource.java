import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class IndCurrentSource implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected double current;
	
	public IndCurrentSource(String id, int nodeOne, int nodeTwo, double current){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.current = current;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + current;
	}

	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(!(nodeOne == 0)){
			B.setReal(indexOne, 0, B.getReal(indexOne, 0) - current);
		}
		if(!(nodeTwo == 0)){
			B.setReal(indexTwo, 0, B.getReal(indexTwo, 0) + current);
		}
		
		/*
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id);
		B.print();
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
		return val;
	}

	@Override
	public int numCurrentsToAdd() {
		return 0;
	}
}
