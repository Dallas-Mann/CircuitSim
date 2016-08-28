import org.ejml.simple.SimpleMatrix;

public interface Component {
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B);
}
