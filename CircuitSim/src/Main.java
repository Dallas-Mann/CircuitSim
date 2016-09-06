
public class Main {
	
	public static void main(String[] args) {
		if(args.length != 2){
			Utilities.usage(1);
		}
		
		Netlist netlist = new Netlist();
		netlist.readNetlist(args[0]);
		netlist.prettyPrint();
		netlist.populateMatricies();
		netlist.solve(args[1]);
	}
}
