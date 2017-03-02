import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

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
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		double[] val = new double[2];
		if(!(nodeOne == 0)){
			val = G.getQuick(indexOne, newIndex);
			val[0] += 1;
			G.setQuick(indexOne, newIndex, val);
			
			val = G.getQuick(newIndex, indexOne);
			val[0] += 1;
			G.setQuick(newIndex, indexOne, val);
		}
		if(!(nodeTwo == 0)){
			val = G.getQuick(indexTwo, newIndex);
			val[0] -= 1;
			G.setQuick(indexTwo, newIndex, val);
			
			val = G.getQuick(newIndex, indexTwo);
			val[0] -= 1;
			G.setQuick(newIndex, indexTwo, val);
		}
		
		val = C.getQuick(newIndex, newIndex);
		val[0] -= inductance;
		C.setQuick(newIndex, newIndex, val);
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
