//imports
//import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {
		if(0 >= args.length || args.length > 1){
			usage(1);
		}
		else{
			Netlist netlist = new Netlist();
			netlist.readNetlist(args[0]);
		}
	}
	
	// error codes for the user
	public static void usage(int error){
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
