import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.*;

public class Netlist {
	private List<Component> circuitElements;
	private List<Integer> nodes;
	// using EJML for matrix manipulation
	// Modified Nodal Analysis equation we wish to solve:
	// [G][X] + [C] d[X]/dt = [B]
	private SimpleMatrix G;
	private SimpleMatrix X;
	private SimpleMatrix C;
	private SimpleMatrix B;
	
	// used to calculate matrix sizes before hand so we don't have to resize them repeatedly
	private int numVoltages;
	private int numCurrents;
	
	public Netlist(){
		// our list of circuit elements and matrices to be populated
		circuitElements = new ArrayList<Component>();
		// list of nodes used to calculate number of voltages required in the matrix
		nodes = new ArrayList<Integer>();
		// have to initialize the list of nodes with the ground node numbered 0
		nodes.add(0);
		// numVoltages and numCurrents used to calculate size of matrices later
		numVoltages = 0;
		numCurrents = 0;
		// for now initialize to zero
		G = new SimpleMatrix(0, 0);
		X = new SimpleMatrix(0, 0);
		C = new SimpleMatrix(0, 0);
		B = new SimpleMatrix(0, 0);
	}
	
	protected void incrVoltages(int amount){
		this.numVoltages += amount;
	}
	
	protected void incrCurrents(int amount){
		this.numCurrents += amount;
	}
	
	// resizes all the relevant matrices after reading in netlist
	private void resizeMatrices(){
		int size = numVoltages + numCurrents;
		G.reshape(size, size);
		X.reshape(size, 1);
		C.reshape(size, size);
		B.reshape(size, 1);
	}
	
	protected void readNetlist(String fileName) {
		try{
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(fileName)));
			String nextLine = fileReader.readLine();
			while(!nextLine.toLowerCase().equals(".end")){
				//System.out.println(nextLine);
				circuitElements.add(this.parseLine(nextLine));
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
	private Component parseLine(String nextLine){
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
				/*
				 * TODO
				 * 
			case 'i':
				
			case 'v':
				
			case 'd':
				
			case 'q':
				
			case 'm':
			*/
			default:
				Utilities.usage(3);
				System.exit(-1);
				break;
		}
		
		this.incrVoltages(newComponent.numVoltagesToAdd(nodes));
		this.incrCurrents(newComponent.numCurrentsToAdd());
		return newComponent;
	}

	private static double convert(String token) {
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
				((Inductor) c).newIndex = newIndex++;
			}
			else if(c instanceof IndVoltageSource){
				((IndVoltageSource) c).newIndex = newIndex++;
			}
			else if(c instanceof VCVS){
				((VCVS) c).newIndex = newIndex++;
			}
			else if(c instanceof OpAmp){
				((OpAmp) c).newIndex = newIndex++;
			}
			else if(c instanceof MutualInductance){
				((MutualInductance) c).newIndexOne = newIndex++;
				((MutualInductance) c).newIndexTwo = newIndex++;
			}
		}
	}

	public void populateMatricies() {
		this.calculateNewIndicies();
		for(Component c : circuitElements){
			c.insertStamp(G, X, C, B);
		}
	}
	
	public void solve(String fileName){
		// will need to take in a filename and the type of solution
		// e.g. frequency, time, transient etc.
		// once a solution is found print out the current state of all matrices
	}
	
	public void prettyPrint(){
		System.out.println("voltages: " + numVoltages + "\t currents: " + numCurrents);
		for(Component c : circuitElements){
			System.out.println(c.toString());
		}
		System.out.println();
	}
}
