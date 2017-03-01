import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.ejml.data.CDenseMatrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.CLinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CCommonOps;

import cern.colt.matrix.tdcomplex.algo.SparseDComplexAlgebra;
import cern.colt.matrix.tdcomplex.algo.decomposition.SparseDComplexLUDecomposition;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix1D;
import cern.colt.matrix.tdcomplex.impl.SparseDComplexMatrix2D;
import cern.jet.math.tdcomplex.DComplexFunctions;

public class Netlist{
	
	public enum solutionType{
		FREQ, DC, TIME
	}
	
	private List<Component> circuitElements;
	private List<Integer> nodes;
	private List<solutionType> solutions;
	// using EJML for matrix manipulation
	// Modified Nodal Analysis equation we wish to solve:
	// [G][X] + [C] d[X]/dt = [B]
	private SparseDComplexMatrix2D G;
	private SparseDComplexMatrix1D X;
	private SparseDComplexMatrix2D C;
	private SparseDComplexMatrix1D B;
	
	// used to calculate matrix sizes before hand so we don't have to resize them repeatedly
	private int numVoltages;
	private int numCurrents;
	private int nodeToTrack;
	
	public Netlist(){
		// our list of circuit elements and matrices to be populated
		circuitElements = new ArrayList<Component>();
		// list of solutions that need to be produced
		solutions = new ArrayList<solutionType>();
		// list of nodes used to calculate number of voltages required in the matrix
		nodes = new ArrayList<Integer>();
		// have to initialize the list of nodes with the ground node numbered 0
		nodes.add(0);
		// numVoltages and numCurrents used to calculate size of matrices later
		numVoltages = 0;
		numCurrents = 0;
		// for now initialize to zero
		G = new SparseDComplexMatrix2D(0, 0);
		X = new SparseDComplexMatrix1D(0);
		C = new SparseDComplexMatrix2D(0, 0);
		B = new SparseDComplexMatrix1D(0);
	}
	
	protected void incrVoltages(int amount){
		this.numVoltages += amount;
	}
	
	protected void incrCurrents(int amount){
		this.numCurrents += amount;
	}
	
	// sets all the relevant matrix sizes after reading in netlist
	private void resizeMatrices(){
		int size = numVoltages + numCurrents;
		G = new SparseDComplexMatrix2D(size, size);
		X = new SparseDComplexMatrix1D(size);
		C = new SparseDComplexMatrix2D(size, size);
		B = new SparseDComplexMatrix1D(size);
	}
	
	protected void readNetlist(String fileName){
		try{
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(fileName)));
			String nextLine = fileReader.readLine();
			while(!nextLine.toLowerCase().equals(".end")){
				this.parseLine(nextLine);
				nextLine = fileReader.readLine();
			}
			fileReader.close();			
			resizeMatrices();
		}
		catch(IOException e){
			System.out.println(e);
			Utilities.usage(2);
		}
	}
	
	private void parseLine(String nextLine){
		String[] tokens = nextLine.split("\\s+");
		if(tokens[0].charAt(0) == '.'){
			switch(tokens[0].toLowerCase().substring(1)){
				case "freq":
					solutions.add(solutionType.FREQ);
					nodeToTrack = Integer.parseInt(tokens[1]) - 1;
					break;
				case "dc":
					solutions.add(solutionType.DC);
					break;
				case "time":
					solutions.add(solutionType.TIME);
					nodeToTrack = Integer.parseInt(tokens[1]) - 1;
					break;
			}
		}
		else if(tokens[0].charAt(0) == '#'){
			// do nothing, it's a comment
			return;
		}
		else{
			circuitElements.add(this.parseComponent(nextLine));
		}
	}

	/*  		
	Letter codes for different circuit elements
-->	Components implemented
	
 	Resistor R
	Capacitor C
	Inductor L
	Independent current source & stimulus I
    Independent voltage source & stimulus V
    Voltage-controlled voltage source E
    Voltage-controlled current source G
    Mutual Inductance/Coupling K

-->	Components not currently implemented
	Current-controlled voltage source F
    Current-controlled current source H
    Diode D
    Bipolar transistor Q
    MOSFET M
    IGBT Z
    Junction FET J
    GaAsFET B
    Digital input (N device) N
    Digital output (O Device) O
    Digital primitive summary U
    Stimulus devices* U STIM		    
    Subcircuit instantiation X
    Transmission line T
    Transmission line coupling K
    Voltage-Controlled switch S
    Current-Controlled switch W
	 */
	// reads in a component at a time
	// also adjusts number of voltages/currents depending on the component for later use
	// these voltages and currents will be used to adjust the matrix sizes
	private Component parseComponent(String nextLine){
		String[] tokens = nextLine.split("\\s+");
		Component newComponent = null;
		int nodeOne = Integer.parseInt(tokens[1]);
		int nodeTwo = Integer.parseInt(tokens[2]);
		switch(tokens[0].toLowerCase().charAt(0)){
			case 'r':
				newComponent = new Resistor(tokens[0], nodeOne, nodeTwo, convert(tokens[3]));
				break;
			case 'c':
				newComponent = new Capacitor(tokens[0], nodeOne, nodeTwo, convert(tokens[3]));
				break;
			case 'l':
				newComponent = new Inductor(tokens[0], nodeOne, nodeTwo, convert(tokens[3]));
				break;
			case 'v':
				newComponent = new IndVoltageSource(tokens[0], nodeOne, nodeTwo, convert(tokens[3]));
				break;
			case 'i':
				newComponent = new IndCurrentSource(tokens[0], nodeOne, nodeTwo, convert(tokens[3]));
				break;
			case 'e':
				newComponent = new VCVS(tokens[0], nodeOne, nodeTwo, Integer.parseInt(tokens[3]), 
						Integer.parseInt(tokens[4]), convert(tokens[5]));
				break;
			case 'g':
				newComponent = new VCCS(tokens[0], nodeOne, nodeTwo, Integer.parseInt(tokens[3]), 
						Integer.parseInt(tokens[4]), convert(tokens[5]));
				break;
			case 'k':
				newComponent = new MutualInductance(tokens[0], nodeOne, nodeTwo, Integer.parseInt(tokens[3]), 
						Integer.parseInt(tokens[4]), convert(tokens[5]), convert(tokens[6]), convert(tokens[7]));
				break;
			case 'o':
				newComponent = new OpAmp(tokens[0], nodeOne, nodeTwo, Integer.parseInt(tokens[3]));
				break;
			case 'a':
				newComponent = new VAC(tokens[0], nodeOne, nodeTwo, convert(tokens[3]), 
						convert(tokens[4]), convert(tokens[5]), Integer.parseInt(tokens[6]));
				break;
			case 'p':
				newComponent = new VPulse(tokens[0], nodeOne, nodeTwo, convert(tokens[3]), 
						convert(tokens[4]), convert(tokens[5]), convert(tokens[6]), convert(tokens[7]), Integer.parseInt(tokens[8]));
				break;
			case 's':
				newComponent = new VStep(tokens[0], nodeOne, nodeTwo, convert(tokens[3]), 
						convert(tokens[4]), convert(tokens[5]), convert(tokens[6]), Integer.parseInt(tokens[7]));
				break;
			default:
				Utilities.usage(3);
				System.exit(-1);
				break;
		}
		
		this.incrVoltages(newComponent.numVoltagesToAdd(nodes));
		this.incrCurrents(newComponent.numCurrentsToAdd());
		return newComponent;
	}

	private static double convert(String token){
		if(Utilities.isNumeric(token)){
			return Double.parseDouble(token);
		}
		else{
			//there should be a trailing modifier after the number
			/*
			F	E-15	femto
			P	E-12	pico
			N	E-9		nano
			U	E-6		micro
			M	E-3		milli
			K	E+3		kilo
			MEG E+6 	mega
			G 	E+9 	giga
			T 	E+12 	tera
			 */
			String[] value = Utilities.splitString(token);
			double baseNum = Double.parseDouble(value[0]);
			
			switch(value[1].toLowerCase()){
				case "a":
					return baseNum *= Math.pow(10, -18);
				case "f":
					return baseNum *= Math.pow(10, -15);
				case "p":
					return baseNum *= Math.pow(10, -12);
				case "n":
					return baseNum *= Math.pow(10, -9);
				case "u":
					return baseNum *= Math.pow(10, -6);
				case "m":
					return baseNum *= Math.pow(10, -3);
				case "k":
					return baseNum *= Math.pow(10, 3);
				case "meg":
					return baseNum *= Math.pow(10, 6);
				case "g":
					return baseNum *= Math.pow(10, 9);
				case "t":
					return baseNum *= Math.pow(10, 12);
				default:
					return 0;
			}
		}
	}
	
	private void calculateNewIndicies(){
		// this calculates the new indices for components that need to augment a matrix
		// calculation of the new indices for components that need to use a 
		// current equation had to wait until the number of voltage equations was known
		// newIndex is really numVoltages + 1 to start at new rows/columns augmented on matrices
		// then we subtract 1 to offset the fact that matrix indices start at 0
		// this is why we post increment the value newIndex
		int newIndex = numVoltages;
		for(Component c : circuitElements){
			if(c instanceof Inductor){
				((Inductor)c).newIndex = newIndex++;
			}
			else if(c instanceof IndVoltageSource){
				((IndVoltageSource)c).newIndex = newIndex++;
			}
			else if(c instanceof VCVS){
				((VCVS)c).newIndex = newIndex++;
			}
			else if(c instanceof OpAmp){
				((OpAmp)c).newIndex = newIndex++;
			}
			else if(c instanceof MutualInductance){
				((MutualInductance)c).newIndexOne = newIndex++;
				((MutualInductance)c).newIndexTwo = newIndex++;
			}
			else if(c instanceof VAC){
				((VAC)c).newIndex = newIndex++;
			}
			else if(c instanceof VPulse){
				((VPulse)c).newIndex = newIndex++;
			}
			else if(c instanceof VStep){
				((VStep)c).newIndex = newIndex++;
			}
		}
	}

	public void populateMatricies(){
		this.calculateNewIndicies();
		for(Component c : circuitElements){
			c.insertStamp(G, X, C, B);
		}
	}
	
	public void simulate(String fileName){
		for(solutionType s : solutions){
			switch (s){
				case FREQ:
					solveFrequency(fileName);
					break;
				//TODO implement DC simulation(Transient at a single time point) 
				case DC:
					break;
				case TIME:
					solveTimeBackwardEuler(fileName);
					//solveTimeTrapezoidalRule(fileName);
					break;
			}
		}
	}
	
	public void solveFrequency(String fileName){
		int VACNewIndex = 0;
		double stepSize = 0;
		double currentFreq = 0;
		double numSteps = 0;
		double magnitude = 0;
		double phase = 0;
		
		// can only sweep one VAC source at the moment
		for(Component c : circuitElements){
			if(c instanceof VAC){
				VAC temp = (VAC)c;
				VACNewIndex = temp.getNewIndex();
				stepSize = (temp.maxFrequency - temp.minFrequency) / temp.numSteps;
				currentFreq = temp.minFrequency;
				numSteps = temp.numSteps;
				break;
			}
		}
				
		try{
			PrintStream writer = new PrintStream(fileName);
			PrintStream orig = System.out;
			
			SparseDComplexMatrix1D BNew;
			SparseDComplexMatrix1D XNew;
			
			// have to assign stdout stream to file
			System.setOut(writer);
			
			for(int i = 0; i < numSteps; i++){
				// initialize new matrices to store G+SC, B, and X, so original matrices are not modified
				SparseDComplexMatrix2D GPlusSC = new SparseDComplexMatrix2D(C.rows(), C.columns());
				// initialize BNew and XNew
				BNew = B;
				XNew = X;
				double wSweep = 2 * Math.PI * currentFreq;

				// create S for the current frequency
				//Complex s = new Complex(0, wSweep);
				double[] s = {0, wSweep};
				
				// remove dc value from b if f != 0
				for(int index = 0; index < BNew.size(); index++){
					//keep dc component at f=0Hz
					if(currentFreq != 0){
						if(index != VACNewIndex){
							BNew.set(index, 0, 0);
						}
					}
				}
				
				// set GPlusSC to S*C
				//CCommonOps.elementMultiply(C, s.getReal(), s.getImaginary(), GPlusSC);
				GPlusSC.assign(C);
				GPlusSC.assign(DComplexFunctions.mult(s));
				
				// set GPlusSC to (G + SC)
				//CCommonOps.add(G, GPlusSC, GPlusSC);
				
				GPlusSC.assign(G, DComplexFunctions.plus);
				
				
				// LU decomposition
				//LinearSolver<CDenseMatrix64F> solver = CLinearSolverFactory.lu(numVoltages + numCurrents);
				SparseDComplexAlgebra solver = new SparseDComplexAlgebra();
				SparseDComplexLUDecomposition lu = solver.lu(GPlusSC, 1);
				
				// Solve for this frequency
				//solver.setA(GPlusSC);
				//solver.solve(BNew, XNew);
				solver.solve(GPlusSC, BNew);
				
				magnitude = calcMagnitude(nodeToTrack, XNew);
				phase = calcPhase(nodeToTrack, XNew);
				
				writer.println(currentFreq + "\t" + magnitude + "\t" + phase);
				// next step, increase frequency
				currentFreq += stepSize;
			}
			System.setOut(orig);
			writer.close();
		}
		catch (Exception e){
			System.out.println("Couldn't write to file.");
			System.out.println(e);
		}
	}
	
	//not accounting for non-linear elements
	public void solveTimeBackwardEuler(String fileName){
		int VNewIndex = 0;
		double stepSize = 0;
		double currentTime = 0;
		double numSteps = 0;
		double amplitude = 0;
		double riseTime = 0;
		double pulseWidth = 0;
		double magnitude = 0;
		double vPulseValue = 0;
		
		// can only sweep one VPulse/VStep source at the moment
		for(Component c : circuitElements){
			if(c instanceof VPulse){
				VPulse temp = (VPulse)c;
				VNewIndex = temp.newIndex;
				stepSize = (temp.maxTime - temp.minTime) / temp.numSteps;
				currentTime = temp.minTime;
				numSteps = temp.numSteps;
				amplitude = temp.amplitude;
				riseTime = temp.riseTime;
				pulseWidth = temp.pulseWidth;
				break;
			}
			else if(c instanceof VStep){
				VStep temp = (VStep)c;
				VNewIndex = temp.newIndex;
				stepSize = (temp.maxTime - temp.minTime) / temp.numSteps;
				currentTime = temp.minTime;
				numSteps = temp.numSteps;
				amplitude = temp.amplitude;
				riseTime = temp.riseTime;
				pulseWidth = temp.maxTime;
				break;
			}
		}
				
		try{
			PrintStream writer = new PrintStream(fileName);
			PrintStream orig = System.out;
			
			// AStatic = G+(C/h), set below (doesn't change)
			SparseDComplexMatrix2D COverH = new SparseDComplexMatrix2D(C.rows(), C.columns());
			SparseDComplexMatrix2D AStatic = new SparseDComplexMatrix2D(G.rows(), G.columns());
			CCommonOps.elementDivide(C, stepSize, 0, COverH);
			CCommonOps.add(G, COverH, AStatic);
			
			//initialize XPreviousTimePoint with zeros
			CDenseMatrix64F XPreviousTimePoint = X.copy();
			
			LinearSolver<CDenseMatrix64F> solver = CLinearSolverFactory.lu(numVoltages + numCurrents);
			solver.setA(AStatic);
			
			//initialize these matrices to the correct dimensions
			CDenseMatrix64F BDynamic = new CDenseMatrix64F(B.getNumRows(), B.getNumCols());
			CDenseMatrix64F BCurrentTimePoint = new CDenseMatrix64F(B.getNumRows(), B.getNumCols());
			CDenseMatrix64F XCurrent = new CDenseMatrix64F(X.getNumRows(), X.getNumCols());
			
			for(int i = 0; i < numSteps; i++){
				//Small signal analysis sets all DC sources to ground, so this is commented out
				BCurrentTimePoint = B.copy();
				//clearMatrix(BCurrentTimePoint);
				
				//calculate VPulse value
				if(0 <= currentTime && currentTime < riseTime){
					vPulseValue = ((amplitude/riseTime)*currentTime);
				}
				else if(riseTime  <= currentTime && currentTime <= riseTime+pulseWidth){
					vPulseValue = amplitude;
				}
				else if(riseTime+pulseWidth < currentTime && currentTime < 2*riseTime+pulseWidth){
					vPulseValue = (amplitude-((amplitude/riseTime)*(currentTime - (riseTime + pulseWidth))));
				}
				else{
					vPulseValue = 0;
				}
				//set BWithSourceVals with the correct voltage value for the pulse input
				BCurrentTimePoint.set(VNewIndex, 0, vPulseValue, 0);
				CCommonOps.mult(COverH, XPreviousTimePoint, BDynamic);
				CCommonOps.add(BCurrentTimePoint, BDynamic, BCurrentTimePoint);
				// LU decomposition, Solve for this time point
				solver.solve(BCurrentTimePoint, XCurrent);
				magnitude = XCurrent.getReal(nodeToTrack, 0);
				writer.println(currentTime + "\t" + magnitude);
				//set XPreviousTimePoint to the solution we just calculated in XCurrent
				XPreviousTimePoint = XCurrent.copy();
				XCurrent = X.copy();
				currentTime += stepSize;
			}
			System.setOut(orig);
			writer.close();
		}
		catch (Exception e){
			System.out.println("Couldn't write to file.");
			System.out.println(e);
		}
	}
	
	//not accounting for non-linear elements
	public void solveTimeTrapezoidalRule(String fileName){
		int VNewIndex = 0;
		double stepSize = 0;
		double currentTime = 0;
		double numSteps = 0;
		double amplitude = 0;
		double riseTime = 0;
		double pulseWidth = 0;
		double magnitude = 0;
		double vPulseValue = 0;
		
		// can only sweep one VPulse source at the moment
		for(Component c : circuitElements){
			if(c instanceof VPulse){
				VPulse temp = (VPulse)c;
				VNewIndex = temp.getNewIndex();
				stepSize = (temp.maxTime - temp.minTime) / temp.numSteps;
				currentTime = temp.minTime;
				numSteps = temp.numSteps;
				amplitude = temp.amplitude;
				riseTime = temp.riseTime;
				pulseWidth = temp.pulseWidth;
				break;
			}
			else if(c instanceof VStep){
				VStep temp = (VStep)c;
				VNewIndex = temp.newIndex;
				stepSize = (temp.maxTime - temp.minTime) / temp.numSteps;
				currentTime = temp.minTime;
				numSteps = temp.numSteps;
				amplitude = temp.amplitude;
				riseTime = temp.riseTime;
				pulseWidth = temp.maxTime;
				break;
			}
		}
				
		try{
			PrintStream writer = new PrintStream(fileName);
			PrintStream orig = System.out;
			
			//initialize these matrices to the correct dimensions
			CDenseMatrix64F BDynamic = new CDenseMatrix64F(B.getNumRows(), B.getNumCols());
			CDenseMatrix64F BCurrentTimePoint = new CDenseMatrix64F(B.getNumRows(), B.getNumCols());
			CDenseMatrix64F BPreviousTimePoint = new CDenseMatrix64F(B.getNumRows(), B.getNumCols());
			CDenseMatrix64F XCurrentTimePoint = new CDenseMatrix64F(X.getNumRows(), X.getNumCols());
			
			CDenseMatrix64F COverH = new CDenseMatrix64F(C.getNumRows(), C.getNumCols());
			CDenseMatrix64F GOverTwo = new CDenseMatrix64F(G.getNumRows(), G.getNumCols());
			CCommonOps.elementDivide(C, stepSize, 0, COverH);
			CCommonOps.elementDivide(G, 2, 0, GOverTwo);
			
			// StaticA = (G/2)+(C/h), set below (doesn't change)
			CDenseMatrix64F AStatic = new CDenseMatrix64F(G.getNumRows(), G.getNumCols());
			CCommonOps.add(GOverTwo, COverH, AStatic);
			
			//StaticB = (C/h)-(G/2), set below (doesn't change)
			CDenseMatrix64F BStatic = new CDenseMatrix64F(G.getNumRows(), G.getNumCols());
			CCommonOps.subtract(COverH, GOverTwo, BStatic);
			
			//initialize XPreviousTimePoint with zeros
			CDenseMatrix64F XPreviousTimePoint = X.copy();
			//BPreviousTimePoint = B.copy();
			
			LinearSolver<CDenseMatrix64F> solver = CLinearSolverFactory.lu(numVoltages + numCurrents);
			solver.setA(AStatic);
			
			for(int i = 0; i < numSteps; i++){
				//Small signal analysis sets all DC sources to ground, so this is commented out
				BCurrentTimePoint = B.copy();
				
				//calculate VPulse value
				if(0 <= currentTime && currentTime < riseTime){
					vPulseValue = ((amplitude/riseTime)*currentTime);
				}
				else if(riseTime  <= currentTime && currentTime <= riseTime+pulseWidth){
					vPulseValue = amplitude;
				}
				else if(riseTime+pulseWidth < currentTime && currentTime < 2*riseTime+pulseWidth){
					vPulseValue = (amplitude-((amplitude/riseTime)*(currentTime - (riseTime + pulseWidth))));
				}
				else{
					vPulseValue = 0;
				}
				//set BCurrentTimePoint with the correct voltage value for the pulse input
				BCurrentTimePoint.set(VNewIndex, 0, vPulseValue, 0);
				//BDynamic = (B(tk)+B(tk-1))/2
				CCommonOps.add(BCurrentTimePoint, BPreviousTimePoint, BDynamic);
				CCommonOps.elementDivide(BDynamic, 2, 0, BDynamic);
				//BDynamic = (B(tk)+B(tk-1))/2 + ((C/h)-(G/2))*X(tk-1)
				CCommonOps.multAdd(BStatic, XPreviousTimePoint, BDynamic);
				// LU decomposition, Solve for this time point
				
				solver.solve(BDynamic, XCurrentTimePoint);
				magnitude = XCurrentTimePoint.getReal(nodeToTrack, 0);
				writer.println(currentTime + "\t" + magnitude);
				//set XPreviousTimePoint to the solution we just calculated in XCurrent
				XPreviousTimePoint = XCurrentTimePoint.copy();
				BPreviousTimePoint = BCurrentTimePoint.copy();
				currentTime += stepSize;
			}
			System.setOut(orig);
			writer.close();
		}
		catch (Exception e){
			System.out.println("Couldn't write to file.");
			System.out.println(e);
		}
	}
	
	public double calcMagnitude(int row, SparseDComplexMatrix1D matrix){
		double[] value = matrix.get(row);
		double real = value[0];
		double imaginary = value[1];
		return Math.sqrt((real * real)+(imaginary * imaginary));
	}
	
	public double calcPhase(int row, SparseDComplexMatrix1D matrix){
		double[] value = matrix.get(row);
		double real = value[0];
		double imaginary = value[1];
		return Math.atan(imaginary/real);
	}
	
	public void prettyPrintNetlist(){
		System.out.println("voltages: " + numVoltages + "\t currents: " + numCurrents);
		for(Component c : circuitElements){
			System.out.println(c.toString());
		}
		System.out.println();
	}
	
	public void printConvert(CDenseMatrix64F matrix){
		int numRows = matrix.numRows;
		int numCols = matrix.numCols;
		DenseMatrix64F temp = new DenseMatrix64F(numRows, numCols);
		CCommonOps.stripReal(matrix, temp);
		DecimalFormat df = new DecimalFormat("#.#############");
		for(int i = 0; i < numRows; i++){
			for(int j = 0; j < numCols; j++){
				System.out.print(String.format("%-20s", df.format(temp.get(i, j))));
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printMatrixToWriter(CDenseMatrix64F matrix, PrintStream writer){
		for(int rowIndex = 0, numRows = matrix.numRows; rowIndex < numRows; rowIndex++){
			for(int colIndex = 0, numCols = matrix.numCols; colIndex < numCols; colIndex++){
				writer.print(matrix.getReal(rowIndex, colIndex) + " ");
			}
			writer.print("\n");
		}
	}
	
}
