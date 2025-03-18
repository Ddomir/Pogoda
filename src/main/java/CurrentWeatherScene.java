import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;
import java.util.Objects;

public class CurrentWeatherScene {
    private static Scene scene;
    private final Stage primaryStage; // Store primaryStage for access

    public CurrentWeatherScene(Stage primaryStage, String zipcode) {
        this.primaryStage = primaryStage;

        // Fetch weather data
        WeatherData weatherData = DataFetcher.fetchWeatherData(zipcode);
        if (weatherData == null) {
            System.out.println("Failed to fetch weather data");
            return;
        }
        WeatherData.LocationData locationData = weatherData.getLocationData();
        WeatherData.CurrentData currentData = weatherData.getCurrentData();
        List<WeatherData.DailyData> dailyData = weatherData.getDailyData();
        List<WeatherData.HourlyData> hourlyData = weatherData.getHourlyData();

        VBox topInfo = createTopInfo(locationData.getCity(), locationData.getState(), currentData.getTemperature(), currentData.getShortForecast(), currentData.getIsDaytime());

        HBox timeSelector = createTimeSelector();

       VBox todayInfoCards = createTodayInfoCards(dailyData.get(0).getDetailedForecast(), hourlyData);

        // Root container
        VBox root = new VBox(topInfo, timeSelector, todayInfoCards);
        root.setSpacing(20);
        root.setAlignment(Pos.TOP_CENTER);
        setBackground(root, currentData.getIsDaytime());

        // Scene
        scene = new Scene(root, 402, 874);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/CurrentWeatherScene.css")).toExternalForm());
    }

    private VBox createTopInfo(String city, String state, double temperature, String shortForecast, boolean isDaytime) {
        // Location Label
        Label location = new Label(city + ", " + state);
        location.getStyleClass().add("location");
        location.setAlignment(Pos.CENTER_RIGHT);

        // Back button
        ImageView backButton = Helpers.getIcon("chevron-left", Color.WHITE, 36);
        backButton.setOnMouseClicked(e -> primaryStage.setScene(ListScene.getScene()));
        HBox back = new HBox(backButton);
        HBox.setHgrow(back, Priority.ALWAYS);

        // Combine back button and location
        HBox locationBack = new HBox(back, location);
        HBox.setHgrow(locationBack, Priority.ALWAYS);
        locationBack.setAlignment(Pos.TOP_LEFT);

        // Temp Label
        Label temp = new Label(Double.toString(temperature) + " F°");
        temp.getStyleClass().add("temperature-main");

        // Weather Icon
        ImageView tempIcon = getWeatherIcon(shortForecast, 64, isDaytime);
        VBox icon = new VBox(tempIcon);
        icon.setAlignment(Pos.CENTER);

        HBox tempWeathIcon = new HBox(icon, temp);
        tempWeathIcon.setAlignment(Pos.BOTTOM_RIGHT);
        tempWeathIcon.setSpacing(10);

        VBox topInfo = new VBox(locationBack, tempWeathIcon);

        return new VBox(topInfo);
    }

    private HBox createTimeSelector() {
        Label todayLabel = new Label("Today");
        todayLabel.getStyleClass().add("time-select-label");
        todayLabel.setAlignment(Pos.CENTER);
        HBox today = new HBox(todayLabel);
        today.getStyleClass().add("time-select-active");
        today.setAlignment(Pos.CENTER);
        today.setMinWidth(167);
        today.setMinHeight(35);
        HBox.setHgrow(today, Priority.ALWAYS);

        Label weekLabel = new Label("7 Days");
        weekLabel.getStyleClass().add("time-select-label");
        weekLabel.setAlignment(Pos.CENTER);
        HBox week = new HBox(weekLabel);
        week.getStyleClass().add("time-select-inactive");
        week.setAlignment(Pos.CENTER);
        week.setMinWidth(167);
        week.setMinHeight(35);
        HBox.setHgrow(week, Priority.ALWAYS);

        HBox timeSelector = new HBox(today, week);
        timeSelector.setSpacing(12);
        HBox.setHgrow(timeSelector, Priority.ALWAYS);
        timeSelector.setMaxHeight(35);

        return timeSelector;
    }

    private VBox createBox(VBox content) {
        VBox box = new VBox(content);
        box.getStyleClass().add("box");
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    private VBox createTodayInfoCards(String detailedForecast, List<WeatherData.HourlyData> hourlyData) {
        VBox todayDescription = createTodayDescription(detailedForecast);
        VBox hourlyTemperature = createHourlyTemperature(hourlyData);
        VBox hourlyPrecipitation = createHourlyPrecipitation(hourlyData);

        VBox main = new VBox(todayDescription, hourlyTemperature, hourlyPrecipitation);
        main.setSpacing(10);
        return main;
    }

    private VBox createTodayDescription(String detailedForecast) {
        Label description = new Label(detailedForecast);
        description.setWrapText(true);
        description.getStyleClass().add("forecast-description");

        VBox todayDescription = new VBox(description);
        todayDescription.setFillWidth(true);
        todayDescription.setAlignment(Pos.TOP_LEFT);

        return createBox(todayDescription);
    }

    private VBox createHourlyTemperature(List<WeatherData.HourlyData> hourlyData) {
        ImageView clockIcon = Helpers.getIcon("clock", Color.WHITE, 20);
        Label boxTitle = new Label("Hourly Temperature");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(clockIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        // Create the container for hour cards
        HBox hourCards = new HBox();
        hourCards.setAlignment(Pos.TOP_LEFT);
        hourCards.setSpacing(14);

        // Add hour cards to the HBox
        for (WeatherData.HourlyData h : hourlyData) {
            Label time = new Label(h.getTime());
            time.setMinWidth(52);
            time.getStyleClass().add("hours-text");
            HBox.setHgrow(time, Priority.ALWAYS);
            ImageView tempIcon = getWeatherIcon(h.getShortForecast(), 38, h.getIsDaytime());
            Label temp = new Label(h.getTemperature() + " F°");
            temp.setMinWidth(52);
            temp.getStyleClass().add("hours-text");
            VBox hourCard = new VBox(time, tempIcon, temp);
            hourCard.setMinWidth(52);
            hourCard.setAlignment(Pos.CENTER);
            hourCards.getChildren().add(hourCard);
        }

        // Create a ScrollPane and configure it
        ScrollPane hourCardsContainer = new ScrollPane(hourCards);
        hourCardsContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourCardsContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourCardsContainer.setFitToHeight(true);
        hourCardsContainer.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        hourCardsContainer.setPadding(new Insets(0));

        hourCards.setMinWidth(Region.USE_PREF_SIZE);

        // Wrap the ScrollPane in a VBox with the title
        VBox main = new VBox(titleBox, hourCardsContainer);
        main.setSpacing(10);

        return createBox(main);
    }

    private VBox createHourlyPrecipitation(List<WeatherData.HourlyData> hourlyData) {
        ImageView rainIcon = Helpers.getIcon("cloud-rain", Color.WHITE, 20);
        Label boxTitle = new Label("Hourly Precipitation");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(rainIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        // Custom X-axis labels (every other hour)
        Label hr1 = new Label(hourlyData.get(0).getTime());
        Label hr2 = new Label(hourlyData.get(2).getTime());
        Label hr3 = new Label(hourlyData.get(4).getTime());
        Label hr4 = new Label(hourlyData.get(6).getTime());
        hr1.getStyleClass().add("xaxis-text");
        hr2.getStyleClass().add("xaxis-text");
        hr3.getStyleClass().add("xaxis-text");
        hr4.getStyleClass().add("xaxis-text");

        Region spc1 = new Region();
        Region spc2 = new Region();
        Region spc3 = new Region();
        HBox.setHgrow(spc1, Priority.ALWAYS);
        HBox.setHgrow(spc2, Priority.ALWAYS);
        HBox.setHgrow(spc3, Priority.ALWAYS);
        hr1.setMaxWidth(40);
        hr2.setMaxWidth(40);
        hr3.setMaxWidth(40);
        hr4.setMaxWidth(40);
        hr1.setMinWidth(40);
        hr2.setMinWidth(40);
        hr3.setMinWidth(40);
        hr4.setMinWidth(40);
        hr1.setAlignment(Pos.CENTER);
        hr2.setAlignment(Pos.CENTER);
        hr3.setAlignment(Pos.CENTER);
        hr4.setAlignment(Pos.CENTER);
        HBox xaxis = new HBox(hr1, spc1, hr2, spc2, hr3, spc3, hr4);
        HBox.setHgrow(xaxis, Priority.ALWAYS);
        xaxis.setMaxWidth(Double.MAX_VALUE);


        Pane chartPane = new Pane();
        chartPane.setMinHeight(125);
        chartPane.setPrefHeight(125);
        double[] xData = new double[7];
        double[] yData = new double[7];

        // Find the highest precipitation chance
        double highestChance = 0;
        int highestIndex = 0;

        for (int i = 0; i < 7; i++) {
            xData[i] = i * 50;
            yData[i] = hourlyData.get(i).getPrecipitationChance();

            if (yData[i] > highestChance) {
                highestChance = yData[i];
                highestIndex = i;
            }
        }

        // Skip showing the graph if all values are too low
        if (highestChance < 5) {
            Label noRainLabel = new Label("No significant precipitation expected");
            noRainLabel.getStyleClass().add("forecast-description");
            VBox main = new VBox(titleBox, noRainLabel);
            main.setSpacing(12);
            return createBox(main);
        }
        double[] scaledY = new double[7];
        for (int i = 0; i < yData.length; i++) {
            scaledY[i] = 150 - (yData[i] / 100.0 * 125);
        }

        // Create a Path for curve
        Path curvePath = new Path();

        MoveTo moveTo = new MoveTo(xData[0], scaledY[0]);
        curvePath.getElements().add(moveTo);

        // Create a smooth curve through all points
        for (int i = 0; i < xData.length - 1; i++) {
            double x1 = xData[i];
            double y1 = scaledY[i];
            double x2 = xData[i + 1];
            double y2 = scaledY[i + 1];

            // Calculate control points for smoother curve
            double controlX1 = x1 + (x2 - x1) / 2;
            double controlY1 = y1;
            double controlX2 = x1 + (x2 - x1) / 2;
            double controlY2 = y2;

            CubicCurveTo cubicCurveTo = new CubicCurveTo(
                    controlX1, controlY1,
                    controlX2, controlY2,
                    x2, y2
            );
            curvePath.getElements().add(cubicCurveTo);
        }

        curvePath.setStroke(Color.WHITE);
        curvePath.setFill(Color.TRANSPARENT);
        curvePath.setOpacity(0.75);
        curvePath.setStrokeWidth(2);

        // Create a path for the gradient fill
        Path fillPath = new Path();
        fillPath.getElements().add(new MoveTo(xData[0], scaledY[0]));

        for (int i = 0; i < xData.length - 1; i++) {
            double x1 = xData[i];
            double y1 = scaledY[i];
            double x2 = xData[i + 1];
            double y2 = scaledY[i + 1];

            double controlX1 = x1 + (x2 - x1) / 2;
            double controlY1 = y1;
            double controlX2 = x1 + (x2 - x1) / 2;
            double controlY2 = y2;

            CubicCurveTo cubicCurveTo = new CubicCurveTo(
                    controlX1, controlY1,
                    controlX2, controlY2,
                    x2, y2
            );
            fillPath.getElements().add(cubicCurveTo);
        }

        fillPath.getElements().add(new LineTo(xData[xData.length - 1], 125));
        fillPath.getElements().add(new LineTo(xData[0], 150));
        fillPath.getElements().add(new LineTo(xData[0], scaledY[0]));

        // Create gradient fill
        Stop[] stops = new Stop[] {
                new Stop(0.25, Color.rgb(255, 255, 255, 0.5)),
                new Stop(1, Color.rgb(255, 255, 255, 0))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 125, false, CycleMethod.NO_CYCLE, stops);
        fillPath.setFill(gradient);
        fillPath.setStroke(Color.TRANSPARENT);
        chartPane.getChildren().add(fillPath);
        chartPane.getChildren().add(curvePath);
        if (highestChance >= 5) {
            Text text = new Text(xData[highestIndex], scaledY[highestIndex] - 15,
                    String.format("%.0f%%", highestChance));
            text.setFill(Color.WHITE);
            text.setTextOrigin(VPos.CENTER);
            chartPane.getChildren().add(text);
            Circle highPoint = new Circle(xData[highestIndex], scaledY[highestIndex], 3, Color.WHITE);
            chartPane.getChildren().add(highPoint);
        }

        chartPane.setMaxWidth(Double.MAX_VALUE);
        chartPane.prefWidthProperty().bind(xaxis.widthProperty());
        HBox chartContainer = new HBox(chartPane);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(0, 0, 0, 8));

        VBox graphGroup = new VBox(chartContainer, xaxis);
        VBox.setVgrow(graphGroup, Priority.ALWAYS);
        graphGroup.setAlignment(Pos.CENTER);

        VBox main = new VBox(titleBox, graphGroup);
        main.setSpacing(10);

        return createBox(main);
    }


    private Color getColorForForecast(String shortForecast) {
        if (shortForecast.toLowerCase().contains("sunny")) {
            return Color.web("#FFE32C");
        } else if (shortForecast.toLowerCase().contains("clear")) {
            return Color.web("#FFE32C");
        } else {
            return Color.web("#FFFFFF");
        }
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
        colorOverlay.setColor(getColorForForecast(shortForecast));
        colorOverlay.setRadius(100);
        colorOverlay.setChoke(1);
        Blend blend = new Blend(BlendMode.SRC_ATOP);
        blend.setTopInput(colorOverlay);
        blend.setBottomInput(colorAdjust);
        iconView.setEffect(blend);

        return iconView;
    }

    private void setBackground(VBox root, boolean isDaytime) {
        if (isDaytime) {
            root.setStyle("-fx-background-image: url('/images/SunnyDay.png');");
        } else {
            root.setStyle("-fx-background-image: url('/images/SunnyNight.png');");
        }
    }

    public static Scene getScene() {
        return scene;
    }
}