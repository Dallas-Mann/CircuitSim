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
	
	public Netlist(){
		//initialize arraylist of circuit elements and SimpleMatrices
		circuitElements = new ArrayList<Component>();
		G = new SimpleMatrix(2, 2);
		X = new SimpleMatrix(2, 2);
		C = new SimpleMatrix(2, 2);
		B = new SimpleMatrix(2, 2);
	}
	
	protected void readNetlist(String fileName) {
		try{
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(fileName)));
			String nextLine = fileReader.readLine();
			while(!nextLine.toLowerCase().equals(".end")){
				//System.out.println(nextLine);
				circuitElements.add(parseLine(nextLine));
				nextLine = fileReader.readLine();
			}
			fileReader.close();
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
	
-->	Components not expected to implement
	
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
	
	private static Component parseLine(String nextLine){
		String[] tokens = nextLine.split("\\s+");
		switch(tokens[0].toLowerCase().charAt(0)){
			case 'r':
				return new Resistor(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), convert(tokens[3]));
			case 'c':
				return new Capacitor(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), convert(tokens[3]));
			case 'l':
				return new Inductor(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), convert(tokens[3]));
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
		return null;
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
		for(Component c : circuitElements){
			System.out.println(c.toString());
		}
	}

	public void populateMatricies() {
		for(Component c : circuitElements){
			c.insertStamp(G, X, C, B);
		}
	}
}
