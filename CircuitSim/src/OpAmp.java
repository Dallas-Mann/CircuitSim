import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class OpAmp implements Component{

	@Override
	public void insertStamp(SimpleMatrix G, SimpleMatrix X, SimpleMatrix C, SimpleMatrix B) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int numVoltagesToAdd(List<Integer> nodes) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numCurrentsToAdd() {
		// TODO Auto-generated method stub
		return 0;
	}

}
