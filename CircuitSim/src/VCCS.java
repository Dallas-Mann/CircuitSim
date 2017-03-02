import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

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
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		int indexThree = nodeThree-1;
		int indexFour = nodeFour-1;
		double[] val = new double[2];
		if(!(nodeOne == 0 || nodeThree == 0)){
			val = G.getQuick(indexThree, indexOne);
			val[0] += gain;
			G.setQuick(indexThree, indexOne, val);
		}
		if(!(nodeTwo == 0 || nodeThree == 0)){
			val = G.getQuick(indexThree, indexTwo);
			val[0] -= gain;
			G.setQuick(indexThree, indexTwo, val);
		}
		if(!(nodeOne == 0 || nodeFour == 0)){
			val = G.getQuick(indexFour, indexOne);
			val[0] -= gain;
			G.setQuick(indexFour, indexOne, val);
		}
		if(!(nodeTwo == 0 || nodeFour == 0)){
			val = G.getQuick(indexFour, indexTwo);
			val[0] += gain;
			G.setQuick(indexFour, indexTwo, val);
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
		return 0;
	}

}
