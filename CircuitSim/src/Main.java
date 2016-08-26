//imports
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {
		if(0 >= args.length || args.length > 1){
			usage(1);
		}
		else{
			readNetlist(args[0]);
		}
		
		
		
		
		
	}

	private static ArrayList<Object> readNetlist(String fileName) {
		BufferedReader fileReader;
		ArrayList<Object> circuitElements = new ArrayList<Object>();
		
		try{
			fileReader = new BufferedReader(new FileReader(new File(fileName)));
			String nextLine = fileReader.readLine();
			while(!nextLine.toLowerCase().equals(".end")){
				System.out.println(nextLine);
				circuitElements.add(parseLine(nextLine));
				nextLine = fileReader.readLine();
			}
		}
		catch(IOException e){
			System.out.println(e);
			usage(2);
		}
		return circuitElements;
	}
	
	private static Object parseLine(String nextLine){
/*  		
 			Letter codes for different circuit elements
-->			Components expected to implement
			
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
			
-->			Components not expected to implement
			
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
		String[] tokens = nextLine.split("\\s+");
		int tempId = Integer.parseInt(tokens[0].substring(1));
		switch(tokens[0].toLowerCase().charAt(0)){
			case 'r':
				return new Resistor(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Double.parseDouble(tokens[3]), tempId);
			case 'c':
				return new Capacitor(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Double.parseDouble(tokens[3]), tempId);
			case 'l':
				return new Inductor(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Double.parseDouble(tokens[3]), tempId);
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
				usage(3);
				System.exit(-1);
				break;
		}
		return null;
	}
	
	
	// error codes for the user
	private static void usage(int error){
		switch (error){
		case 1:
			System.out.println("Correct usage is \"java runSimulation netList.cir\"");
			break;
		case 2:
			System.out.println("Could not open or read file.");
			break;
		case 3:
			System.out.println("Invalid circuit component");
			break;
		default:
			System.out.println("Unknown error.");
			break;
		}
	}
}
