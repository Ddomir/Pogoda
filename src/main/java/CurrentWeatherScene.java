import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class CurrentWeatherScene {
    private static Scene scene;
    private Button switchToListSceneButton;

    public CurrentWeatherScene(Stage primaryStage, String zipcode) {
        // Button to switch back to the list scene
        switchToListSceneButton = new Button("Back to List");

        // Fetch weather data
        WeatherData weatherData = DataFetcher.fetchWeatherData(zipcode);
        if (weatherData == null) {
            System.out.println("Failed to fetch weather data");
            return;
        }

        // Location Data
        WeatherData.LocationData locationData = weatherData.getLocationData();
        Label locationLabel = new Label(locationData.getCity() + ", " + locationData.getState());
        locationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Current Data
        WeatherData.CurrentData currentData = weatherData.getCurrentData();
        Label temperatureLabel = new Label("Temperature: " + currentData.getTemperature() + " °F");
        Label forecastLabel = new Label("Forecast: " + currentData.getShortForecast());
        Label humidityLabel = new Label("Humidity: " + currentData.getHumidity() + "%");
        Label dewPointLabel = new Label("Dew Point: " + currentData.getDewPoint() + " °F");
        Label windLabel = new Label("Wind: " + currentData.getWindSpeed() + " " + currentData.getWindDirection());

        // Hourly Data
        Label hourlyHeader = new Label("Hourly Forecast");
        hourlyHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        VBox hourlyDataBox = new VBox(5);
        hourlyDataBox.setPadding(new Insets(10));
        List<WeatherData.HourlyData> hourlyData = weatherData.getHourlyData();
        for (WeatherData.HourlyData hour : hourlyData) {
            Label hourLabel = new Label(hour.getTime() + ": " + hour.getTemperature() + " °F, " +
                    hour.getShortForecast() + ", " + hour.getPrecipitationChance() + "%");
            hourlyDataBox.getChildren().add(hourLabel);
        }

        // Daily Data
        Label dailyHeader = new Label("7-Day Forecast");
        dailyHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        VBox dailyDataBox = new VBox(5);
        dailyDataBox.setPadding(new Insets(10));
        List<WeatherData.DailyData> dailyData = weatherData.getDailyData();
        for (WeatherData.DailyData day : dailyData) {
            Label dayLabel = new Label(day.getTime() + ": Day - " + day.getDayTemperature() + " °F, Night - " +
                    day.getNightTemperature() + " °F, " + day.getShortForecast() + ", " + day.getPrecipitationChance() + "%");
            dailyDataBox.getChildren().add(dayLabel);
        }

        // Layout
        VBox currentDataBox = new VBox(10, locationLabel, temperatureLabel, forecastLabel, humidityLabel, dewPointLabel, windLabel);
        currentDataBox.setPadding(new Insets(20));
        currentDataBox.setAlignment(Pos.TOP_LEFT);

        VBox contentBox = new VBox(10, switchToListSceneButton, currentDataBox, hourlyHeader, hourlyDataBox, dailyHeader, dailyDataBox);
        contentBox.setPadding(new Insets(20));

        // Wrap the content in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true); // Ensure the content fits the width of the ScrollPane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrolling

        // Scene
        scene = new Scene(scrollPane, 700, 700);

        // Event handling
        switchToListSceneButton.setOnAction(e -> primaryStage.setScene(ListScene.getScene()));
    }

    public static Scene getScene() {
        return scene;
    }
}