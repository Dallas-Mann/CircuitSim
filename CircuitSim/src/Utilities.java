
public class Utilities {
	public static boolean isNumeric(String token){
		try  
		{  
			Double.parseDouble(token);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;
	}
	
	public static String[] splitString(String token){
		int index = 0;
		for(char c : token.toCharArray()){
			if(Character.isAlphabetic(c)){
				break;
			}else
				index++;
		}
		//split token into two string and return an array containing them.
		String[] result = new String[2];
		result[0] = token.substring(0, index);
		result[1] = token.substring(index);
		return result;
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
		System.exit(-1);
	}
}
