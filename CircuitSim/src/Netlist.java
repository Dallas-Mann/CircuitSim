import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.*;



public class Netlist {
	private List<Component> circuitElements;
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
		// numVoltages and numCurrents used to calculate size of matrices later
		numVoltages = 0;
		numCurrents = 0;
		// for now initialize to zero
		G = new SimpleMatrix(0, 0);
		X = new SimpleMatrix(0, 0);
		C = new SimpleMatrix(0, 0);
		B = new SimpleMatrix(0, 0);
	}
	
	protected void incrVoltages(){
		this.numVoltages++;
	}
	
	protected void incrCurrents(){
		this.numCurrents++;
	}
	
	protected boolean newNode(int nodeOne, int nodeTwo){
		if(nodeOne > numVoltages || nodeTwo > numVoltages){
			return true;
		}
		else
			return false;
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
-->	Components expected to implement
	
 	Resistor R
	Capacitor C
	Inductor L
    Voltage-controlled voltage source Voltage-controlled current source E
    Voltage-controlled voltage source Voltage-controlled current source G
    Current-controlled current source Current-controlled voltage source F
    Current-controlled current source Current-controlled voltage source H
    Independent current source & stimulus I
    Independent voltage source & stimulus V
    Diode D
    Bipolar transistor Q
    MOSFET M
	
-->	Components not currently expected to implement
	
    Coupling K
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
		switch(tokens[0].toLowerCase().charAt(0)){
			case 'r':
				newComponent = new Resistor(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), convert(tokens[3]));
				break;
			case 'c':
				newComponent = new Capacitor(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), convert(tokens[3]));
				break;
			case 'l':
				newComponent = new Inductor(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), convert(tokens[3]));
				break;
			/*
			 * TODO
			 * 
			case 'e':
				
			case 'g':
				
			case 'f':
				
			case 'h':
				
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
		
		if(newNode(newComponent.getNodeOne(), newComponent.getNodeTwo())){
			this.incrVoltages();
		}
		if(newComponent instanceof Inductor){
			this.incrCurrents();
		}
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
	
	public void prettyPrint(){
		System.out.println("voltages: " + numVoltages + "\t currents: " + numCurrents);
		for(Component c : circuitElements){
			System.out.println(c.toString());
		}
		System.out.println();
	}

	public void populateMatricies() {
		// index is really numVoltages + 1 to start at new rows/columns augmented on matrices
		// then we subtract 1 to offset the matrix indices starting at 0
		int newIndex = numVoltages;
		for(Component c : circuitElements){
			if(c instanceof Inductor){
				((Inductor) c).newIndex = newIndex;
				newIndex++;
			}
		}
		for(Component c : circuitElements){
			c.insertStamp(G, X, C, B);
		}
	}
}
