import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private boolean isEditing = false;
    private VBox locationCards;
    private Stage primaryStage;

    public ListScene(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Load zip codes from file
        zipCodes = ZipcodeManager.loadZipCodes();

        Label title = new Label("Locations");
        title.getStyleClass().add("root-title");
        title.setAlignment(Pos.CENTER_LEFT);

        locationCards = new VBox();
        locationCards.setSpacing(16);

        // Create location cards for all zip codes
        refreshLocationCards();

        // Add field
        HBox addFieldContainer = createAddFieldContainer();

        // Edit button
        VBox editButton = createEditButton(addFieldContainer);

        // Layout
        VBox root = new VBox(locationCards, editButton);
        VBox.setVgrow(title, Priority.ALWAYS);
        root.setSpacing(12);
        root.setAlignment(Pos.TOP_CENTER);

        VBox main = new VBox(title, root);
        VBox.setVgrow(title, Priority.ALWAYS);
        main.setSpacing(12);
        main.setAlignment(Pos.TOP_LEFT);

        // Scene
        scene = new Scene(main, 402, 874);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/ListScene.css")).toExternalForm());
    }

    private HBox createLocationCard(String zipCode, WeatherData weatherData) {
        WeatherData.LocationData locationData = weatherData.getLocationData();
        WeatherData.CurrentData currentData = weatherData.getCurrentData();

        HBox locationCard = new HBox();
        locationCard.getStyleClass().add("location-card");
        locationCard.setPrefHeight(80);

        String backgroundColor = getBackgroundColorForForecast(currentData.getShortForecast(), currentData.getIsDaytime());
        String borderColor = getBorderColorForForecast(currentData.getShortForecast(), currentData.getIsDaytime());
        locationCard.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 15;"
        );

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

        ImageView weatherIcon = getWeatherIcon(currentData.getShortForecast(), 38, currentData.getIsDaytime());
        VBox rightIcon = new VBox(weatherIcon);
        rightIcon.setAlignment(Pos.CENTER_RIGHT);

        locationCard.getChildren().addAll(leftText, rightIcon);
        HBox.setHgrow(locationCard, Priority.ALWAYS);
        HBox.setHgrow(leftText, Priority.ALWAYS);

        // Minus Button (hidden by default)
        ImageView minusIcon = Helpers.getIcon("minus", Color.WHITE, 34);
        HBox removeButton = new HBox(minusIcon);
        removeButton.getStyleClass().add("remove-button");
        removeButton.setAlignment(Pos.CENTER);
        removeButton.setPrefSize(34, 34);
        removeButton.setMaxHeight(34);
        removeButton.setVisible(isEditing);

        HBox locationCardContainer = new HBox(locationCard);
        locationCardContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(locationCardContainer, Priority.ALWAYS);
        locationCardContainer.setSpacing(10);

        // Store the zip code in the userData property
        locationCardContainer.setUserData(zipCode);

        // Change to location weather scene
        locationCard.setOnMouseClicked(e -> {
            if (zipCode != null) {
                primaryStage.setScene(new CurrentWeatherScene(primaryStage, zipCode).getScene());
            }
        });

        // Remove button action
        removeButton.setOnMouseClicked(e -> {
            String zipCodeToRemove = (String) locationCardContainer.getUserData(); // Retrieve zipCode from userData
            zipCodes.remove(zipCodeToRemove);
            ZipcodeManager.saveZipCodes(zipCodes);
            refreshLocationCards();
        });

        return locationCardContainer;
    }

    private HBox createAddFieldContainer() {
        HBox addFieldContainer = new HBox();
        addFieldContainer.getStyleClass().add("add-field-container");

        TextField zipCodeField = new TextField();
        zipCodeField.setPromptText("Enter zip code ...");
        HBox.setHgrow(zipCodeField, Priority.ALWAYS);
        zipCodeField.getStyleClass().add("zip-code-field");

        ImageView searchIcon = Helpers.getIcon("search", Color.WHITE, 30);
        VBox searchButton = new VBox(searchIcon);
        searchButton.getStyleClass().add("edit-button");
        searchButton.setAlignment(Pos.CENTER_RIGHT);
        searchButton.setPrefSize(50, 50);
        searchButton.setMaxSize(50, 50);

        HBox searchButtonContainer = new HBox(searchButton);
        addFieldContainer.getChildren().addAll(zipCodeField, searchButtonContainer);
        HBox.setHgrow(addFieldContainer, Priority.ALWAYS);
        addFieldContainer.setSpacing(16);

        // Search button action
        searchButton.setOnMouseClicked(e -> {
            String newZipCode = zipCodeField.getText().trim();

            // Validate the zip code
            if (newZipCode.isEmpty()) {
                showAlert("Invalid Input", "Please enter a zip code.");
                return;
            }

            if (zipCodes.contains(newZipCode)) {
                showAlert("Duplicate Zip Code", "This zip code is already in the list.");
                return;
            }

            // Check if the zip code exists in the ZipcodeLocator
            ZipcodeLocator.Location location = ZipcodeLocator.getInstance().getLocationData(newZipCode); // Use singleton
            if (location == null) {
                showAlert("Invalid Zip Code", "The entered zip code does not exist.");
                return;
            }

            // Add the zip code to the list
            zipCodes.add(newZipCode);
            ZipcodeManager.saveZipCodes(zipCodes);
            zipCodeField.clear();

            // Refresh the UI to show the new location card
            refreshLocationCards();
        });

        return addFieldContainer;
    }

    private VBox createEditButton(HBox addFieldContainer) {
        ImageView editIcon = Helpers.getIcon("pencil", Color.WHITE, 30);
        VBox editButton = new VBox(editIcon);
        editButton.getStyleClass().add("edit-button");
        editButton.setAlignment(Pos.CENTER);
        editButton.setPrefSize(50, 50);
        editButton.setMaxSize(50, 50);

        editButton.setOnMouseClicked(e -> {
            isEditing = !isEditing;
            toggleEditMode(isEditing, addFieldContainer);
        });

        return editButton;
    }

    private void toggleEditMode(boolean isEditing, HBox addFieldContainer) {
        // Show or hide the addFieldContainer based on edit mode
        if (isEditing) {
            if (!locationCards.getChildren().contains(addFieldContainer)) {
                locationCards.getChildren().add(addFieldContainer);
            }
        } else {
            locationCards.getChildren().remove(addFieldContainer);
        }

        // Show or hide minus buttons
        for (javafx.scene.Node node : locationCards.getChildren()) {
            if (node instanceof HBox) {
                HBox locationCardContainer = (HBox) node;

                if (locationCardContainer.getStyleClass().contains("add-field-container")) {
                    continue;
                }

                if (isEditing) {
                    // Add the minus button if it doesn't already exist
                    boolean hasRemoveButton = locationCardContainer.getChildren().stream()
                            .anyMatch(child -> child instanceof HBox && child.getStyleClass().contains("remove-button"));

                    if (!hasRemoveButton) {
                        ImageView minusIcon = Helpers.getIcon("minus", Color.WHITE, 34);
                        HBox removeButton = new HBox(minusIcon);
                        removeButton.getStyleClass().add("remove-button");
                        removeButton.setAlignment(Pos.CENTER);
                        removeButton.setPrefSize(34, 34);
                        removeButton.setMaxHeight(34);

                        // Add action to remove the location card
                        removeButton.setOnMouseClicked(e -> {
                            String zipCode = (String) locationCardContainer.getUserData(); // Store zipCode in userData
                            zipCodes.remove(zipCode);
                            ZipcodeManager.saveZipCodes(zipCodes);
                            refreshLocationCards();
                        });

                        locationCardContainer.getChildren().add(removeButton);
                    }
                } else {
                    // Remove the minus button if it exists
                    locationCardContainer.getChildren().removeIf(child ->
                            child instanceof HBox && child.getStyleClass().contains("remove-button")
                    );
                }
            }
        }
    }

    private void refreshLocationCards() {
        locationCards.getChildren().clear();

        for (String zipCode : zipCodes) {
            WeatherData weatherData = DataFetcher.fetchWeatherData(zipCode);
            if (weatherData == null) continue;

            HBox locationCard = createLocationCard(zipCode, weatherData);
            locationCards.getChildren().add(locationCard);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ImageView getWeatherIcon(String shortForecast, int size, boolean isDaytime) {
        String iconPath = "/icons/";
        boolean isNightTime = !(isDaytime);

        if (shortForecast.toLowerCase().contains("sunny")) {
            iconPath += isDaytime ? "sun.png" : "moon.png";
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            iconPath += isDaytime ? "cloudy.png" : "cloud-moon.png";
        } else if (shortForecast.toLowerCase().contains("rain")) {
            iconPath += "cloud-rain.png";
        } else if (shortForecast.toLowerCase().contains("snow")) {
            iconPath += "cloud-snow.png";
        } else {
            iconPath += isNightTime ? "moon.png" : "sun.png";
        }

        ImageView iconView = new ImageView(new Image(iconPath));
        iconView.setFitWidth(size);
        iconView.setFitHeight(size);
        iconView.setPreserveRatio(true);

        // Adjust icon color based on forecast
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(1);
        InnerShadow colorOverlay = new InnerShadow();
        colorOverlay.setColor(getColorForForecast(shortForecast, isDaytime));
        colorOverlay.setRadius(100);
        colorOverlay.setChoke(1);
        Blend blend = new Blend(BlendMode.SRC_ATOP);
        blend.setTopInput(colorOverlay);
        blend.setBottomInput(colorAdjust);
        iconView.setEffect(blend);

        return iconView;
    }

    private String getBackgroundColorForForecast(String shortForecast, boolean isDaytime) {
        if (shortForecast.toLowerCase().contains("sunny") || shortForecast.toLowerCase().contains("clear")) {
            return isDaytime ? "linear-gradient(to right, #FB8500 0%, #FFCA50 80%)" : "linear-gradient(to right, #683B99 0%, #C766FF 80%)";
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            return "linear-gradient(to right, #998E9B 0%, #D0C3D0 80%)";
        } else if (shortForecast.toLowerCase().contains("rain")) {
            return "linear-gradient(to right, #1E90FF 0%, #87CEEB 80%)";
        } else {
            return "linear-gradient(to right, #FB8500 0%, #FFCA50 80%)";
        }
    }

    private String getBorderColorForForecast(String shortForecast, boolean isDaytime) {
        if (shortForecast.toLowerCase().contains("sunny") || shortForecast.toLowerCase().contains("clear")) {
            return isDaytime ? "linear-gradient(to right, #FFCA50, #FB8500)" : "linear-gradient(to right, #C766FF, #683B99)";
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            return "linear-gradient(to right, #D0C3D0, #998E9B)";
        } else if (shortForecast.toLowerCase().contains("rain")) {
            return "linear-gradient(to right, #87CEEB, #1E90FF)";
        } else {
            return "linear-gradient(to right, #FFCA50, #FB8500)";
        }
    }

    private Color getColorForForecast(String shortForecast, boolean isDaytime) {
        if (shortForecast.toLowerCase().contains("sunny") || shortForecast.toLowerCase().contains("clear")) {
            return isDaytime ? Color.web("#FFE32C") : Color.web("#683B99");
        } else {
            return Color.web("#FFFFFF");
        }
    }

    public static Scene getScene() {
        return scene;
    }
}