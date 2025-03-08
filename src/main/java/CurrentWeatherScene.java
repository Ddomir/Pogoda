import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import weather.Period;

import java.util.ArrayList;
import java.util.Map;

public class CurrentWeatherScene {
    private static Scene scene;
    private Button switchToListSceneButton;

    public CurrentWeatherScene(Stage primaryStage) {
        Button switchToListSceneButton = new Button("List");

        String Zipcode = "60018";

        // Get Data
        ZipcodeLocator.Location location = DataFetcher.GetZipcodeData(Zipcode);
        ArrayList<Period> DayNightForecast = DataFetcher.getDailyForecast("LOT", 77, 70);

        TextField coordinatesField = new TextField("Coordinates: " + location.getLat() + " " + location.getLng());
        TextField locationField = new TextField("Location: " + location.getCity() + " " + location.getStateId());
        TextField temperatureField = new TextField("Today's temperature is: " + DayNightForecast.get(0).temperature + "°F");

        // Layout
        VBox root = new VBox(locationField, coordinatesField, temperatureField, switchToListSceneButton);

        // Scene
        scene = new Scene(root, 700, 700);

        // Event handling
        switchToListSceneButton.setOnAction(e -> primaryStage.setScene(ListWeatherScene.getScene()));
    }

    public static Scene getScene() {
        return scene;
    }
}