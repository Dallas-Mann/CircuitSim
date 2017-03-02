import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

public class VCVS implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected int nodeThree;
	protected int nodeFour;
	protected int newIndex;
	protected double gain;

	public VCVS(String id, int nodeOne, int nodeTwo, int nodeThree, int nodeFour, double gain){
		this.id = id;
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.nodeThree = nodeThree;
		this.nodeFour = nodeFour;
		this.gain = gain;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + nodeFour + " " + newIndex + " " + gain;
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
		if(!(nodeOne == 0 || nodeTwo == 0)){
			val = G.getQuick(newIndex, indexOne);
			val[0] += gain;
			G.setQuick(newIndex, indexOne, val);
			
			val = G.getQuick(newIndex, indexTwo);
			val[0] -= gain;
			G.setQuick(newIndex, indexTwo, val);
		}
		if(!(nodeThree == 0)){
			val = G.getQuick(newIndex, indexThree);
			val[0] += 1;
			G.setQuick(newIndex, indexThree, val);
			
			val = G.getQuick(indexThree, newIndex);
			val[0] += 1;
			G.setQuick(indexThree, newIndex, val);
		}
		if(!(nodeFour == 0)){
			val = G.getQuick(newIndex, indexFour);
			val[0] -= 1;
			G.setQuick(newIndex, indexFour, val);
			
			val = G.getQuick(indexFour, newIndex);
			val[0] -= 1;
			G.setQuick(indexFour, newIndex, val);
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
		return 1;
	}
}
