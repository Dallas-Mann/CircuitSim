import java.util.List;

import org.ejml.data.CDenseMatrix64F;

import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

public interface Component {
	public void insertStamp(CDenseMatrix64F g, CDenseMatrix64F x, CDenseMatrix64F c, CDenseMatrix64F b);
	public int numVoltagesToAdd(List<Integer> nodes);
	public int numCurrentsToAdd();
}
