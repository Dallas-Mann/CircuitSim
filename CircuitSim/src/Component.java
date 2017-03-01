import java.util.List;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;

public interface Component {
	public void insertStamp(SparseDComplexMatrix2D g, SparseDComplexMatrix1D x, SparseDComplexMatrix2D c, SparseDComplexMatrix1D b);
	public int numVoltagesToAdd(List<Integer> nodes);
	public int numCurrentsToAdd();
}
