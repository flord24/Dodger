package cs333;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloFX extends Application {
	@Override
	public void start(Stage stage) {
		stage.setTitle("HelloFX Test");
		stage.setScene(new Scene(new Label("JavaFX ✅"), 320, 120));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}