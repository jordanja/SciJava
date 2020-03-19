package thesis.Helpers;

public class TypeCheckers {

	// https://stackoverflow.com/questions/29437223/take-input-and-then-cast-into-appropriate-data-type-java
	public static boolean isDouble(String string) {
	    try {
	        double d = Double.parseDouble(string);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	public static boolean isInteger(String string) {
	    try {
	        double d = Integer.parseInt(string);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public static boolean isNumeric(String str) {
		  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
