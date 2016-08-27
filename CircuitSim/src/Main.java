
public class Main {
	
	public static void main(String[] args) {
		if(0 >= args.length || args.length > 1){
			Utilities.usage(1);
		}
		
		Netlist netlist = new Netlist();
		netlist.readNetlist(args[0]);
		netlist.prettyPrint();
		netlist.populateMatricies();
	}
}
