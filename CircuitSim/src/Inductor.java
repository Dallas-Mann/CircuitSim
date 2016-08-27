
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
	public void insertStamp() {
		// TODO Auto-generated method stub
		
	}
}
