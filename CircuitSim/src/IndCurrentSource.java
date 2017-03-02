import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

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
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		double[] val = new double[2];
		if(!(nodeOne == 0)){
			val = B.getQuick(indexOne);
			val[0] -= current;
			B.setQuick(indexOne,  val);
		}
		if(!(nodeTwo == 0)){
			val = B.getQuick(indexTwo);
			val[0] += current;
			B.setQuick(indexTwo,  val);
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
