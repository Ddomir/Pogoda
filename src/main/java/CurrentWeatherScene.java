import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import weather.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrentWeatherScene {
    private static Scene scene;
    private Button switchToListSceneButton;

    public CurrentWeatherScene(Stage primaryStage) {
        Button switchToListSceneButton = new Button("List");

        String zipcode = "60018";

        WeatherData weatherData = DataFetcher.fetchWeatherData(zipcode);
        if (weatherData != null) {
            // Print location data
            WeatherData.LocationData locationData = weatherData.getLocationData();
            System.out.println("Location Data:");
            System.out.println("Region: " + locationData.getRegion());
            System.out.println("GridX: " + locationData.getGridX());
            System.out.println("GridY: " + locationData.getGridY());
            System.out.println("Forecast URL: " + locationData.getForecastURL());
            System.out.println("Hourly Forecast URL: " + locationData.getHourlyForecastURL());

            // Print hourly data
            System.out.println("\nHourly Data:");
            List<WeatherData.HourlyData> hourlyData = weatherData.getHourlyData();
            for (WeatherData.HourlyData hour : hourlyData) {
                System.out.println(hour.getTime() + ": " + hour.getTemperature() + " F°, " +
                        hour.getShortForecast() + ", " + hour.getPrecipitationChance() + "%");
            }

            // Print daily data
            System.out.println("\nHourly Data:");
            List<WeatherData.DailyData> dailyData = weatherData.getDailyData();
            for (WeatherData.DailyData day : dailyData) {
                System.out.println(day.getTime() + ": Day - " + day.getDayTemperature() + " F°, Night - " + day.getNightTemperature()
                        + " F°, " + day.getShortForecast() + ", " + day.getPrecipitationChance() + "%");
            }

        } else {
            System.out.println("Failed to fetch weather data");
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