import org.ejml.simple.SimpleMatrix;

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
		this.nodeThree = nodeOne;
		this.nodeFour = nodeTwo;
		this.gain = gain;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + nodeThree + " " + nodeFour + " " + gain;
	}
	
	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNodeOne() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNodeTwo() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numVoltagesToAdd(int numVoltages) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numCurrentsToAdd() {
		// TODO Auto-generated method stub
		return 0;
	}

}
