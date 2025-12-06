import MyClasses.Conditions.*;
import MyClasses.*;
import MyClasses.Consumables.*;
import MyClasses.Keyboard.*;
import MyClasses.Persons.*;
import MyClasses.Security.*;




//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//void main() {
//
//
//    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//    // to see how IntelliJ IDEA suggests fixing it.
//    IO.println(String.format("Hello and welcome!"));
//
//    for (int i = 1; i <= 5; i++) {
//        //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//        // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//        IO.println("i = " + i);
//    }
//}



import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Starting Food/Drink Formulation System (CLI Mode)...");
        runCLI();
    }

    private static void runCLI() {
        int choice;
        do {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Add New Formulation (Simulated)");
            System.out.println("2. Consult Formulations");
            System.out.println("3. Report/Comment Issue (Feedback)");
            System.out.println("4. Set Veto");
            System.out.println("5. List All Formulations");
            System.out.println("0. Exit and Save");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1; // Invalid input
            }

            switch (choice) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 0:
                    System.out.println("Exiting application.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }
}