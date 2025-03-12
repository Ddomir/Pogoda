import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class JavaFX extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		Font latoThinItalic = Font.loadFont(getClass().getResource("/fonts/Lato-ThinItalic.ttf").toExternalForm(), 24);
		Font latoThin = Font.loadFont(getClass().getResource("/fonts/Lato-Thin.ttf").toExternalForm(), 24);
		Font latoLight = Font.loadFont(getClass().getResource("/fonts/Lato-Light.ttf").toExternalForm(), 24);
		Font latoLightItalic = Font.loadFont(getClass().getResource("/fonts/Lato-LightItalic.ttf").toExternalForm(), 24);
		Font latoRegular = Font.loadFont(getClass().getResource("/fonts/Lato-Regular.ttf").toExternalForm(), 24);
		Font latoItalic = Font.loadFont(getClass().getResource("/fonts/Lato-Italic.ttf").toExternalForm(), 24);
		Font latoBold = Font.loadFont(getClass().getResource("/fonts/Lato-Bold.ttf").toExternalForm(), 24);
		Font latoBoldItalic = Font.loadFont(getClass().getResource("/fonts/Lato-BoldItalic.ttf").toExternalForm(), 24);
		Font latoBlack = Font.loadFont(getClass().getResource("/fonts/Lato-Black.ttf").toExternalForm(), 24);
		Font latoBlackItalic = Font.loadFont(getClass().getResource("/fonts/Lato-BlackItalic.ttf").toExternalForm(), 24);

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
