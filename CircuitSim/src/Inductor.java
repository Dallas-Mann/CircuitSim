import org.ejml.simple.SimpleMatrix;

public class Inductor implements Component{
	String id;
	int nodeOne;
	int nodeTwo;
	double inductance;
	
	public Inductor(String id, int nodeOne, int nodeTwo, double inductance){
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.inductance = inductance;
		this.id = id;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + inductance;
	}

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// TODO Auto-generated method stub
		
	}
}
