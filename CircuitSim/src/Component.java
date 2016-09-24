import java.util.List;

import org.ejml.data.CDenseMatrix64F;

public interface Component {
	public void insertStamp(CDenseMatrix64F G, CDenseMatrix64F X, CDenseMatrix64F C, CDenseMatrix64F B);
	public int numVoltagesToAdd(List<Integer> nodes);
	public int numCurrentsToAdd();
}
