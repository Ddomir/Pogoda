import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFX extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Weather App");

		// Initialize scenes
		CurrentWeatherScene currentWeatherScene = new CurrentWeatherScene(primaryStage);
		ListWeatherScene listWeatherScene = new ListWeatherScene(primaryStage);

		// Set the initial scene
		primaryStage.setScene(currentWeatherScene.getScene());
		primaryStage.show();
	}
}