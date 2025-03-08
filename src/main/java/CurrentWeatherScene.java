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

        String zipcode = "60018";

        WeatherData.LocationData locationData = DataFetcher.getLocationData(zipcode);
        if (locationData != null) {
            System.out.println("City: " + locationData.getCity());
            System.out.println("State: " + locationData.getState());
            System.out.println("County: " + locationData.getCounty())   ;
            System.out.println("Region: " + locationData.getRegion());
            System.out.println("GridX: " + locationData.getGridX());
            System.out.println("GridY: " + locationData.getGridY());
            System.out.println("Forecast URL: " + locationData.getForecastURL());
            System.out.println("Hourly Forecast URL: " + locationData.getHourlyForecastURL());
        } else {
            System.out.println("Invalid ZIP code");
        }


        // Layout
        VBox root = new VBox(switchToListSceneButton);

        // Scene
        scene = new Scene(root, 700, 700);

        // Event handling
        switchToListSceneButton.setOnAction(e -> primaryStage.setScene(ListWeatherScene.getScene()));
    }

    public static Scene getScene() {
        return scene;
    }
}