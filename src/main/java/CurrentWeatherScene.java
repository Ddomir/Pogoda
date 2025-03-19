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

        VBox todayInfoCards = createTodayInfoCards(dailyData.get(0).getDetailedForecast(), hourlyData, currentData);
        ScrollPane scrollableCards = new ScrollPane(todayInfoCards);
        scrollableCards.setMinHeight(648);
        scrollableCards.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableCards.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableCards.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0 0 0 0;");
        scrollableCards.setFitToWidth(true);

        // Root container
        VBox root = new VBox(topInfo, timeSelector, scrollableCards);
        VBox.setVgrow(scrollableCards, Priority.ALWAYS);
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

    private VBox createTodayInfoCards(String detailedForecast, List<WeatherData.HourlyData> hourlyData, WeatherData.CurrentData currentData) {
        VBox todayDescription = createTodayDescription(detailedForecast);

        VBox hourlyTemperature = createHourlyTemperature(hourlyData);

        VBox hourlyPrecipitation = createHourlyPrecipitation(hourlyData);

        HBox humidityAndUV = new HBox(createHumidity(currentData.getHumidity(), currentData.getDewPoint()), createUV(currentData.getUvIndex()));
        humidityAndUV.setSpacing(22);

        HBox realFeelAndWind = new HBox(createRealFeel(currentData));

        VBox main = new VBox(todayDescription, hourlyTemperature, hourlyPrecipitation, humidityAndUV, realFeelAndWind);
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
        hr1.getStyleClass().add("subtext");
        hr2.getStyleClass().add("subtext");
        hr3.getStyleClass().add("subtext");
        hr4.getStyleClass().add("subtext");

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

    private VBox createHumidity(int humidity, double dewpoint) {
        ImageView rainIcon = Helpers.getIcon("waves", Color.WHITE, 20);
        Label boxTitle = new Label("Humidity");
        boxTitle.getStyleClass().add("card-title");

        HBox titleBox = new HBox(rainIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        Label humidityLabel = new Label(humidity + "%");
        humidityLabel.getStyleClass().add("big-text");

        VBox topContent = new VBox(titleBox, humidityLabel);
        topContent.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(topContent, Priority.NEVER);

        Label dew = new Label("The dewpoint is " + dewpoint + "°.");
        dew.getStyleClass().add("subtext");
        dew.setWrapText(true);

        VBox bottomContent = new VBox(dew);
        bottomContent.setAlignment(Pos.BOTTOM_LEFT);
        bottomContent.setMinHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(bottomContent, Priority.ALWAYS);

        VBox main = new VBox(topContent, bottomContent);
        main.setSpacing(10);
        main.setMinHeight(137);
        main.setMinWidth(137);

        return createBox(main);
    }


    private VBox createUV(int UV) {
        ImageView rainIcon = Helpers.getIcon("sun", Color.WHITE, 20);
        Label boxTitle = new Label("UV Index");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(rainIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        Label UVnum = new Label("" + UV);
        UVnum.getStyleClass().add("big-text");

        String UVClassname = "";
        if (UV < 3) {
            UVClassname = "Low";
        } else if (UV < 6) {
            UVClassname = "Moderate";
        } else if (UV < 8) {
            UVClassname = "High";
        } else if (UV < 10) {
            UVClassname = "Very High";
        }

        Label UVclass = new Label(UVClassname);
        UVclass.getStyleClass().add("UVClass-text");
        HBox UVData = new HBox(UVnum, UVclass);
        UVData.setSpacing(5);
        UVData.setAlignment(Pos.BOTTOM_LEFT);

        VBox topContent = new VBox(titleBox, UVData);
        VBox.setVgrow(topContent, Priority.ALWAYS);


        HBox uvBar = new HBox();
        uvBar.setMinHeight(4);
        uvBar.setMaxHeight(4);
        uvBar.setMinWidth(137);
        uvBar.setMaxWidth(137);
        uvBar.getStyleClass().add("uvBar");
        uvBar.setAlignment(Pos.CENTER);

        Circle uvIndicator = new Circle(6);
        uvIndicator.setFill(Color.WHITE);
        uvIndicator.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 15, 0, 0, 4);");
        double barWidth = uvBar.getMinWidth();
        double normalizedUV = Math.max(0, Math.min(UV, 10)) / 10.0;
        double circleX = normalizedUV * (barWidth - (uvIndicator.getRadius() * 2));

        Pane overlay = new Pane(uvIndicator);
        overlay.setMinWidth(uvBar.getMinWidth());
        overlay.setMinHeight(uvBar.getMinHeight());
        uvIndicator.setTranslateX(circleX);
        uvIndicator.setTranslateY(6);

        // Stack both together
        StackPane uvBarGroup = new StackPane(uvBar, overlay);
        uvBarGroup.setAlignment(Pos.CENTER);

        HBox bottomContent = new HBox(uvBarGroup);
        bottomContent.setAlignment(Pos.BOTTOM_LEFT);

        VBox main = new VBox(topContent, bottomContent);
        main.setMinHeight(137);
        main.setMinWidth(137);
        return createBox(main);
    }

    private VBox createRealFeel(WeatherData.CurrentData currentData) {
        ImageView tempIcon = Helpers.getIcon("thermometer", Color.WHITE, 20);
        Label boxTitle = new Label("Feels Like");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(tempIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        int realFeel = Helpers.calculateRealFeel(currentData.getTemperature(), currentData.getHumidity(), currentData.getDewPoint(), currentData.getWindSpeed());

        Label humidityLabel = new Label("" + realFeel);
        humidityLabel.getStyleClass().add("big-text");

        VBox topContent = new VBox(titleBox, humidityLabel);
        VBox.setVgrow(topContent, Priority.ALWAYS);

        Label realFeelDesc = new Label(Math.abs(realFeel - currentData.getTemperature()) + "° " + ((realFeel-currentData.getTemperature() < 0) ? "below" : "above") + " the real temperature.");
        realFeelDesc.getStyleClass().add("subtext");

        VBox bottomContent = new VBox(realFeelDesc);

        VBox main = new VBox(topContent, bottomContent);

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
        String iconPath = "";

        if (shortForecast.toLowerCase().contains("sunny")) {
            iconPath += isDaytime ? "sun" : "moon";
        } else if (shortForecast.toLowerCase().contains("cloudy")) {
            iconPath += isDaytime ? "cloudy" : "cloud-moon";
        } else if (shortForecast.toLowerCase().contains("rain")) {
            iconPath += "cloud-rain";
        } else if (shortForecast.toLowerCase().contains("snow")) {
            iconPath += "cloud-snow";
        } else {
            iconPath += isDaytime ? "sun" : "moon";
        }

        return Helpers.getIcon(iconPath, getColorForForecast(shortForecast), size);
    }

    private void setBackground(VBox root, boolean isDaytime) {
        System.out.println(isDaytime);
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