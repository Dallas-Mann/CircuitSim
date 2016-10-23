import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class Resistor implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected double resistance;
	
	public Resistor(String id, int nodeOne, int nodeTwo, double resistance){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.resistance = resistance;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + resistance;
	}

	@Override
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B) {
		double conductance = 1.0/resistance;
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(nodeOne == 0){
			G.setReal(indexTwo, indexTwo, G.getReal(indexTwo, indexTwo) + conductance);
		}
		else if(nodeTwo == 0){
			G.setReal(indexOne, indexOne, G.getReal(indexOne, indexOne) + conductance);
		}
		else{
			G.setReal(indexOne, indexOne, G.getReal(indexOne, indexOne) + conductance);
			G.setReal(indexTwo, indexTwo, G.getReal(indexTwo, indexTwo) + conductance);
			G.setReal(indexOne, indexTwo, G.getReal(indexOne, indexTwo) - conductance);
			G.setReal(indexTwo, indexOne, G.getReal(indexTwo, indexOne) - conductance);
		}
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
