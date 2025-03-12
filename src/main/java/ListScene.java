import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class ListScene {
    private static Scene scene;
    private List<String> zipCodes;

    public ListScene(Stage primaryStage) {
        // Load zip codes from file
        zipCodes = ZipcodeManager.loadZipCodes();

        Label title = new Label("Locations");
        title.getStyleClass().add("root-title");
        title.setAlignment(Pos.CENTER_LEFT);

        VBox locationCards = new VBox();
        locationCards.setSpacing(16);

        // Each Location Card
        for (String zipCode : zipCodes) {
            WeatherData weatherData = DataFetcher.fetchWeatherData(zipCode);
            if (weatherData == null) continue;

            WeatherData.LocationData locationData = weatherData.getLocationData();
            WeatherData.CurrentData currentData = weatherData.getCurrentData();

            HBox locationCard = new HBox();
            locationCard.getStyleClass().add("location-card");
            locationCard.setPrefHeight(80);

            // Set dynamic background color based on shortForecast
            String backgroundColor = getBackgroundColorForForecast(currentData.getShortForecast());
            locationCard.setStyle("-fx-background-color: " + backgroundColor + ";");

            // Left Text
            VBox leftText = new VBox();
            leftText.getStyleClass().add("left-text");
            leftText.setAlignment(Pos.CENTER_LEFT);
            leftText.setMinWidth(54);

            Label locationLabel = new Label(locationData.getCity() + ", " + locationData.getState());
            locationLabel.getStyleClass().add("location-label");
            locationLabel.setAlignment(Pos.TOP_LEFT);

            Label temperatureLabel = new Label(currentData.getTemperature() + " °F");
            temperatureLabel.getStyleClass().add("temperature-label");
            temperatureLabel.setAlignment(Pos.BOTTOM_LEFT);

            leftText.getChildren().addAll(locationLabel, temperatureLabel);

            // Weather Icon
            ImageView weatherIcon = getWeatherIcon(currentData.getShortForecast());
            VBox rightIcon = new VBox(weatherIcon);
            rightIcon.setAlignment(Pos.CENTER_RIGHT);

            // Add content to the card
            locationCard.getChildren().addAll(leftText, rightIcon);
            HBox.setHgrow(leftText, Priority.ALWAYS);

            // Add the card to the container
            locationCards.getChildren().add(locationCard);

            // Change to location weather scene
            locationCard.setOnMouseClicked(e -> {
                if (zipCode != null) {
                    primaryStage.setScene(new CurrentWeatherScene(primaryStage, zipCode).getScene());
                }
            });
        }

        // Layout
        VBox root = new VBox(title, locationCards);
        VBox.setVgrow(title, Priority.ALWAYS);
        root.setSpacing(12);

        // Scene
        scene = new Scene(root, 402, 874);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/ListScene.css")).toExternalForm());
    }

    private ImageView getWeatherIcon(String shortForecast) {
        String iconPath = "/icons/";
        if (shortForecast.toLowerCase().contains("sunny")) {
            iconPath += "sun.png";
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            iconPath += "cloudy.png";
        } else if (shortForecast.toLowerCase().contains("rain")) {
            iconPath += "cloud-rain.png";
        } else if (shortForecast.toLowerCase().contains("partly cloudy")) {
            iconPath += "cloud-sun.png";
        } else {
            iconPath += "sun.png";
        }

        ImageView iconView = new ImageView(new Image(iconPath));
        iconView.setFitWidth(54);
        iconView.setFitHeight(54);
        iconView.setPreserveRatio(true);

        // Adjust icon color based on forecast
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(1);
        InnerShadow colorOverlay = new InnerShadow();
        colorOverlay.setColor(getColorForForecast(shortForecast));
        colorOverlay.setRadius(100);
        colorOverlay.setChoke(1);
        Blend blend = new Blend(BlendMode.SRC_ATOP);
        blend.setTopInput(colorOverlay);
        blend.setBottomInput(colorAdjust);
        iconView.setEffect(blend);

        return iconView;
    }

    private String getBackgroundColorForForecast(String shortForecast) {
        if (shortForecast.toLowerCase().contains("sunny")) {
            return "linear-gradient(to right, #FB8500 0%, #FFCA50 80%)";
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            return "linear-gradient(to right, #998E9B 0%, #D0C3D0 80%)";
        } else if (shortForecast.toLowerCase().contains("rain")) {
            return "linear-gradient(to right, #1E90FF 0%, #87CEEB 80%)";
        } else {
            return "linear-gradient(to right, #FB8500 0%, #FFCA50 80%)";
        }
    }

    private Color getColorForForecast(String shortForecast) {
        if (shortForecast.toLowerCase().contains("sunny")) {
            return Color.web("#FB8500");
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            return Color.web("#998E9B");
        } else if (shortForecast.toLowerCase().contains("rain")) {
            return Color.web("#1E90FF");
        } else {
            return Color.web("#FB8500");
        }
    }



    public static Scene getScene() {
        return scene;
    }
}