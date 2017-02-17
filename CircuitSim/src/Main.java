
public class Main {
	
	public static void main(String[] args){
		if(args.length != 2){
			Utilities.usage(1);
		}
		
		Netlist netlist = new Netlist();
		netlist.readNetlist(args[0]);
		
		//verify the netlist was read in and show all the components.
		//netlist.prettyPrintNetlist();
		netlist.populateMatricies();
		//netlist.prettyPrintMatrices();
		//netlist.printAll();
		netlist.simulate(args[1]);
	}
}
