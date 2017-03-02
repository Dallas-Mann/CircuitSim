import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

public class MutualInductance implements Component {

	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int nodeFour;
	protected int newIndexOne;
	protected int newIndexTwo;
	protected double inductanceOne;
	protected double inductanceTwo;
	protected double inductanceCoupled;
	
	public MutualInductance(String id, int nodeOne, int nodeTwo, int nodeThree, int nodeFour, double inductanceOne, double inductanceTwo, double inductanceCoupled){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.nodeFour = nodeFour;
		this.inductanceOne = inductanceOne;
		this.inductanceTwo = inductanceTwo;
		this.inductanceCoupled = inductanceCoupled;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + newIndexOne + " " + newIndexTwo;
	}
	
	@Override
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		int indexFour = nodeFour-1;
		double[] val = new double[2];
		if(!(nodeOne == 0)){
			val = G.getQuick(indexOne, newIndexOne);
			val[0] += 1;
			G.setQuick(indexOne, newIndexOne, val);
			
			val = G.getQuick(newIndexOne, indexOne);
			val[0] += 1;
			G.setQuick(newIndexOne, indexOne, val);
		}
		if(!(nodeThree == 0)){
			val = G.getQuick(indexThree, newIndexOne);
			val[0] += 1;
			G.setQuick(indexThree, newIndexOne, val);
			
			val = G.getQuick(newIndexOne, indexThree);
			val[0] += 1;
			G.setQuick(newIndexOne, indexThree, val);
		}
		if(!(nodeTwo == 0)){
			val = G.getQuick(indexTwo, newIndexOne);
			val[0] -= 1;
			G.setQuick(indexTwo, newIndexOne, val);
			
			val = G.getQuick(newIndexOne, indexTwo);
			val[0] -= 1;
			G.setQuick(newIndexOne, indexTwo, val);
		}
		if(!(nodeFour == 0)){
			val = G.getQuick(indexFour, newIndexOne);
			val[0] -= 1;
			G.setQuick(indexFour, newIndexOne, val);
			
			val = G.getQuick(newIndexOne, indexFour);
			val[0] -= 1;
			G.setQuick(newIndexOne, indexFour, val);
		}
		
		val = C.getQuick(newIndexOne, newIndexOne);
		val[0] -= inductanceOne;
		C.setQuick(newIndexOne, newIndexOne, val);
		
		val = C.getQuick(newIndexTwo, newIndexTwo);
		val[0] -= inductanceTwo;
		C.setQuick(newIndexTwo, newIndexTwo, val);
		
		val = C.getQuick(newIndexOne, newIndexTwo);
		val[0] -= inductanceCoupled;
		C.setQuick(newIndexOne, newIndexTwo, val);
		
		val = C.getQuick(newIndexTwo, newIndexOne);
		val[0] -= inductanceCoupled;
		C.setQuick(newIndexTwo, newIndexOne, val);
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
		// always return 2, the MutualInductance needs two current equations
		return 2;
	}
}
