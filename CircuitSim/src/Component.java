import org.ejml.simple.SimpleMatrix;

public interface Component {
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B);
	public int getNodeOne();
	public int getNodeTwo();
	public String toString();
	public int numVoltagesToAdd(int numVoltages);
	public int numCurrentsToAdd();
}
