package utils;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FieldValidator {
	
	public Boolean verifyEmpty (String[] textfields) {
		for (String t: textfields) { 
			if (t.isEmpty()) return false; 
		}
		return true;
	}
	
	public Boolean emailValidation(String email) {
		Pattern p = Pattern.compile("^(.+)@(.+)$");
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
