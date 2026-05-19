import ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        // CivicCare GUI launch karne ke liye
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainWindow();
            }
        });
    }
}