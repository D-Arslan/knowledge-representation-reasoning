package rcr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintStream;

public class MainApp extends Application {

    static {
        // Tweety prints "No default FOL reasoner" warnings to stderr during class
        // loading — one per module. Suppress them so the console stays clean.
        System.setErr(new PrintStream(System.err) {
            @Override public void println(String x) {
                if (x != null && x.startsWith("No default FOL reasoner")) return;
                super.println(x);
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/rcr/dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 900, 650);
        scene.getStylesheets().add(
                getClass().getResource("/rcr/css/style.css").toExternalForm());
        stage.setTitle("RCR1 — Logiques Formelles");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
