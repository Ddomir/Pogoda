import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import weather.Period;

import java.util.ArrayList;
import java.util.Map;

public class CurrentWeatherScene {
    private static Scene scene;
    private Button switchToListSceneButton;

    public CurrentWeatherScene(Stage primaryStage) {
        Button switchToListSceneButton = new Button("List");

        // Get Data
        ArrayList<Period> DayNightForecast = DataFetcher.getDailyForecast("LOT", 77, 70);
        TextField temperatureField = new TextField("Today's temperature is: " + DayNightForecast.get(0).temperature + "°F");

        // Layout
        VBox root = new VBox(temperatureField, switchToListSceneButton);

        // Scene
        scene = new Scene(root, 700, 700);

        // Event handling
        switchToListSceneButton.setOnAction(e -> primaryStage.setScene(ListWeatherScene.getScene()));
    }

    public static Scene getScene() {
        return scene;
    }
}