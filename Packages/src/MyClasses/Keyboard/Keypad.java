package MyClasses.Keyboard;

import java.util.Scanner;
public class Keypad {
	
public Keypad() {
		
	}
	Scanner scan = new Scanner(System.in);
	
	
	
	public int  getInt() {
		return Integer.parseInt(scan.nextLine());
		
	}
	
	public double getDouble() {
		return Double.parseDouble(scan.nextLine());
	}
	
	public String getString() {
		return scan.nextLine();
	}
	
//scan.close();

}
