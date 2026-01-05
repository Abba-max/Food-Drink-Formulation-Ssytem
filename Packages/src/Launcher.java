public class Launcher {
    public static void main(String[] args) {
        // Launch the JavaFX application through this non-JavaFX class
        // This avoids "JavaFX runtime components are missing" error
        Main_GUI.main(args);
    }
}