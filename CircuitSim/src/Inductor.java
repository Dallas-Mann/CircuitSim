import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public class Inductor implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int newIndex;
	protected double inductance;
	
	public Inductor(String id, int nodeOne, int nodeTwo, double inductance){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.inductance = inductance;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + newIndex + " " + inductance;
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
			G.setReal(indexTwo, newIndex, G.getReal(indexTwo, indexTwo) - 1);
			G.setReal(newIndex, indexTwo, G.getReal(newIndex, indexTwo) - 1);
		}
		C.setReal(newIndex, newIndex, C.getReal(newIndex, newIndex) - inductance);
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
		// inductor always adds a current equation to the G matrix
		return 1;
	}
}
