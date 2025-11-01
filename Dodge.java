package cs333;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.net.URL;

public class Dodge extends Application {
	@Override
	public void start(Stage stage) {
		try {
			URL fxml = Dodge.class.getResource("Dodge.fxml");
			if (fxml == null)
				throw new IllegalStateException("Could not load Dodge.fxml. Make sure it's next to Dodge.java.");

			Parent root = FXMLLoader.load(fxml);
			Scene scene = new Scene(root, 600, 800);
			stage.setTitle("Dodge!");
			stage.setResizable(false);
			stage.setScene(scene);
			stage.show();
			root.requestFocus();
		} catch (Throwable t) {
			t.printStackTrace();
			Alert a = new Alert(Alert.AlertType.ERROR, "Startup Error: " + t.getMessage());
			a.setHeaderText("Startup Error");
			a.showAndWait();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
