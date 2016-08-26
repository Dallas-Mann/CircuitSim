//imports
//import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {
		if(0 >= args.length || args.length > 1){
			Utilities.usage(1);
		}
		else{
			Netlist netlist = new Netlist();
			netlist.readNetlist(args[0]);
		}
	}
}
