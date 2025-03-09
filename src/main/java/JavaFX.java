import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFX extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Weather App");

		// Initialize scenes
		ListScene listScene = new ListScene(primaryStage);
		CurrentWeatherScene currentWeatherScene = new CurrentWeatherScene(primaryStage, "60018");

		// Set the initial scene
		primaryStage.setScene(listScene.getScene());
		primaryStage.show();
	}
}