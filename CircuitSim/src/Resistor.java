
public class Resistor implements Component{
	String id;
	int nodeOne;
	int nodeTwo;
	double resistance;
	
	public Resistor(String id, int nodeOne, int nodeTwo, double resistance){
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
		this.resistance = resistance;
		this.id = id;
	}
	
	public String toString(){
		return id + " " + nodeOne + " " + nodeTwo + " " + resistance;
	}

	@Override
	public void insertStamp() {
		// TODO Auto-generated method stub
		
	}
}
