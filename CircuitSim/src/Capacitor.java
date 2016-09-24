import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class Capacitor implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected double capacitance;
	
	
	public Capacitor(String id, int nodeOne, int nodeTwo, double capacitance){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.capacitance = capacitance;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + capacitance;
	}

	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(nodeOne == 0){
			C.setImaginary(indexTwo, indexTwo, C.getImaginary(indexTwo, indexTwo) + capacitance);
		}
		else if(nodeTwo == 0){
			C.setImaginary(indexOne, indexOne, C.getImaginary(indexOne, indexOne) + capacitance);
		}
		else{
			C.setImaginary(indexOne, indexOne, C.getImaginary(indexOne, indexOne) + capacitance);
			C.setImaginary(indexTwo, indexTwo, C.getImaginary(indexTwo, indexTwo) + capacitance);
			C.setImaginary(indexOne, indexTwo, C.getImaginary(indexOne, indexTwo) - capacitance);
			C.setImaginary(indexTwo, indexOne, C.getImaginary(indexTwo, indexOne) - capacitance);
		}
		
		/*
		// show changes in C Matrix to debug
		System.out.println("Inserted Element " + this.id);
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
		return val;
	}

	@Override
	public int numCurrentsToAdd() {
		return 0;
	}
}
