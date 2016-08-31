import org.ejml.simple.SimpleMatrix;

public class IndCurrentSource implements Component{
	protected String id;
	protected int nodeOne;
	protected int nodeTwo;
	protected double current;
	
	public IndCurrentSource(String id, int nodeOne, int nodeTwo, double current){
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.current = current;
		this.id = id;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + current;
	}

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// 0th node is ground node, and thus not implemented in our matrices
		// because of this we need to offset all the matrix indices by -1
		int indexOne = nodeOne-1;
		int indexTwo = nodeTwo-1;
		if(nodeOne == 0){
			B.set(indexTwo, 0, B.get(indexTwo, 0) + current);
		}
		else if(nodeTwo == 0){
			B.set(indexOne, 0, B.get(indexOne, 0) - current);
		}
		else{
			B.set(indexOne, 0, B.get(indexOne, 0) - current);
			B.set(indexTwo, 0, B.get(indexTwo, 0) + current);
		}
		
		// show changes in G Matrix to debug
		System.out.println("Inserted Element " + this.id + "\n" + B.toString());
	}

	@Override
	public int getNodeOne() {
		return this.nodeOne;
	}

	@Override
	public int getNodeTwo() {
		return this.nodeTwo;
	}

}
