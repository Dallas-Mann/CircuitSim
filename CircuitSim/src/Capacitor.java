import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

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
	public void insertStamp(SparseDComplexMatrix2D G, SparseDComplexMatrix1D X, SparseDComplexMatrix2D C, SparseDComplexMatrix1D B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		double[] val = new double[2];
		if(nodeOne == 0){
			val = C.getQuick(indexTwo, indexTwo);
			val[0] += capacitance;
			C.setQuick(indexTwo, indexTwo, val);
		}
		else if(nodeTwo == 0){
			val = C.get(indexOne, indexOne);
			val[0] += capacitance;
			C.setQuick(indexOne, indexOne, val);
		}
		else{
			val = C.get(indexOne, indexOne);
			val[0] += capacitance;
			C.setQuick(indexOne, indexOne, val);
			
			val = C.get(indexTwo, indexTwo);
			val[0] += capacitance;
			C.setQuick(indexTwo, indexTwo, val);
			
			val = C.get(indexOne, indexTwo);
			val[0] -= capacitance;
			C.setQuick(indexOne, indexTwo, val);
			
			val = C.get(indexTwo, indexOne);
			val[0] -= capacitance;
			C.setQuick(indexTwo, indexOne, val);
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
