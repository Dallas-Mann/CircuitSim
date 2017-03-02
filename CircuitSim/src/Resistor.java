import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

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
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		double conductance = 1.0/resistance;
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		double[] val = new double[2];
		if(nodeOne == 0){
			val = G.getQuick(indexTwo, indexTwo);
			val[0] += conductance;
			G.setQuick(indexTwo, indexTwo, val);
		}
		else if(nodeTwo == 0){
			val = G.getQuick(indexOne, indexOne);
			val[0] += conductance;
			G.setQuick(indexOne, indexOne, val);
		}
		else{
			val = G.getQuick(indexOne, indexOne);
			val[0] += conductance;
			G.setQuick(indexOne, indexOne, val);
			
			val = G.getQuick(indexTwo, indexTwo);
			val[0] += conductance;
			G.setQuick(indexTwo, indexTwo, val);
			
			val = G.getQuick(indexOne, indexTwo);
			val[0] -= conductance;
			G.setQuick(indexOne, indexTwo, val);
			
			val = G.getQuick(indexTwo, indexOne);
			val[0] -= conductance;
			G.setQuick(indexTwo, indexOne, val);
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
