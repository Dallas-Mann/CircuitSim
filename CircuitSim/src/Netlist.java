import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.ejml.data.CDenseMatrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.CLinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CCommonOps;

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
	private CDenseMatrix64F G;
	private CDenseMatrix64F X;
	private CDenseMatrix64F C;
	private CDenseMatrix64F B;
	
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
		G = new CDenseMatrix64F(0, 0);
		X = new CDenseMatrix64F(0, 0);
		C = new CDenseMatrix64F(0, 0);
		B = new CDenseMatrix64F(0, 0);
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
		G.reshape(size, size);
		X.reshape(size, 1);
		C.reshape(size, size);
		B.reshape(size, 1);
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
				newComponent = new OpAmp(tokens[0], nodeOne, nodeTwo, Integer.parseInt(tokens[3]), 
						convert(tokens[4]));
				break;
			case 'a':
				newComponent = new VAC(tokens[0], nodeOne, nodeTwo, convert(tokens[3]), 
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
				case DC:
					break;
				case TIME:
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
			
			CDenseMatrix64F BNew = B.copy();
			CDenseMatrix64F XNew = X.copy();
			
			// have to assign stdout stream to file
			System.setOut(writer);
			
			for(int i = 0; i < numSteps; i++){
				// initialize new matrices to store G+SC, B, and X, so original matrices are not modified
				CDenseMatrix64F GPlusSC = new CDenseMatrix64F(C.numRows, C.numCols);
				// initialize BNew and XNew
				BNew = B.copy();
				XNew = X.copy();
				double wSweep = 2 * Math.PI * currentFreq;

				// create S for the current frequency
				Complex s = new Complex(0, wSweep);
				
				// remove dc value from b if f != 0
				for(int index = 0; index < BNew.getNumRows(); index++){
					//keep dc component at f=0Hz
					if(currentFreq != 0){
						if(index != VACNewIndex){
							BNew.set(index, 0, 0, 0);
						}
					}
				}
				
				// set GPlusSC to S*C
				CCommonOps.elementMultiply(C, s.getReal(), s.getImaginary(), GPlusSC);
				// set GPlusSC to (G + SC)
				CCommonOps.add(G, GPlusSC, GPlusSC);
				
				// LU decomposition
				LinearSolver<CDenseMatrix64F> solver = CLinearSolverFactory.lu(numVoltages + numCurrents);
				// Solve for this frequency
				solver.setA(GPlusSC);
				solver.solve(BNew, XNew);
				
				magnitude = calcMagnitude(nodeToTrack, 0, XNew);
				phase = calcPhase(nodeToTrack, 0, XNew);
				
				writer.println(currentFreq + "\t" + magnitude + "\t" + phase);
				// next step, increase frequency
				if(i < numSteps / 10){
					currentFreq += 0.1 * stepSize;
				}
				else{
					currentFreq += stepSize;
				}
			}
			System.setOut(orig);
			writer.close();
		}
		catch (Exception e){
			System.out.println("Couldn't write to file.");
			System.out.println(e);
		}
	}
	
	public double calcMagnitude(int row, int col, CDenseMatrix64F matrix){
		double real = matrix.getReal(row, col);
		double imaginary = matrix.getImaginary(row, col);
		return Math.sqrt((real * real)+(imaginary * imaginary));
	}
	
	public double calcPhase(int row, int col, CDenseMatrix64F matrix){
		double real = matrix.getReal(row, col);
		double imaginary = matrix.getImaginary(row, col);
		return Math.atan(imaginary/real);
	}
	
	public void prettyPrintNetlist(){
		System.out.println("voltages: " + numVoltages + "\t currents: " + numCurrents);
		for(Component c : circuitElements){
			System.out.println(c.toString());
		}
		System.out.println();
	}
	
	public void prettyPrintMatrices(){
		System.out.println("G Matrix");
		G.print();
		System.out.println();
		System.out.println("X Matrix");
		X.print();
		System.out.println();
		System.out.println("C Matrix");
		C.print();
		System.out.println();
		System.out.println("B Matrix");
		B.print();
	}
	
	void printConvert(CDenseMatrix64F matrix){
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
	
	void printAll(){
			printConvert(G);
			printConvert(C);
			printConvert(X);
			printConvert(B);
	}
}
