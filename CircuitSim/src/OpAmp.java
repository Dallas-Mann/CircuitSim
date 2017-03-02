import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

public class OpAmp implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int newIndex;	
	
	public OpAmp(String id, int nodeOne, int nodeTwo, int nodeThree){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + newIndex;
	}

	@Override
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		// model the op amp as a voltage controlled voltage source at the output terminal and ground
		// the two input terminals are the control+ and control- for the VCVS at the output.
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		double[] val = new double[2];
		
		// clear row because we will divide it by the infinite gain
		for(int tempIndex = 0, numCols = G.columns(); tempIndex < numCols; tempIndex++){
			G.setQuick(newIndex, tempIndex, 0, 0);
		}
		
		// the gain divided by itself will result in -1 or 1
		if(!(nodeOne == 0 || nodeThree == 0)){
			val = G.getQuick(newIndex, indexOne);
			val[0] += 1;
			G.setQuick(newIndex, indexOne, val);
		}
		if(!(nodeTwo == 0 || nodeThree == 0)){
			val = G.getQuick(newIndex, indexTwo);
			val[0] -= 1;
			G.setQuick(newIndex, indexTwo, val);
		}
		
		val = G.getQuick(indexThree, newIndex);
		val[0] += 1;
		G.setQuick(indexThree, newIndex, val);
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
		return val;
	}

	@Override
	public int numCurrentsToAdd() {
		// always add a current equation because we are modeling the OpAmp as a VCVS
		return 1;
		//return 0;
	}
}
