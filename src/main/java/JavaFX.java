import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class JavaFX extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Pogoda - By Dominic Irla");

		// Initialize scenes
		ListScene listScene = new ListScene(primaryStage);
		CurrentWeatherScene currentWeatherScene = new CurrentWeatherScene(primaryStage, "60018");

		// Set the initial scene
		primaryStage.setScene(listScene.getScene());
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
