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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CurrentWeatherScene {
    private static Scene scene;
    private final Stage primaryStage;
    private ScrollPane scrollableCards;
    private VBox todayInfoCards;
    private VBox weeklyInfoCards;

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

        this.todayInfoCards = createTodayInfoCards(dailyData.get(0).getDetailedForecast(), hourlyData, currentData);

        this.weeklyInfoCards = createWeeklyInfoCards(dailyData);

        this.scrollableCards = new ScrollPane(todayInfoCards);
        scrollableCards.setMinHeight(648);
        scrollableCards.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableCards.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableCards.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollableCards.setFitToWidth(true);

        HBox timeSelector = createTimeSelector();

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

        today.setOnMouseClicked(e -> {
            scrollableCards.setContent(todayInfoCards);
            setActive(today, week);
        });

        week.setOnMouseClicked(e -> {
            scrollableCards.setContent(weeklyInfoCards);
            setActive(week, today);
        });

        return timeSelector;
    }

    private void setActive(HBox active, HBox inactive) {
        active.getStyleClass().clear();
        active.getStyleClass().add("time-select-active");

        inactive.getStyleClass().clear();
        inactive.getStyleClass().add("time-select-inactive");
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

        HBox realFeelAndWind = new HBox(createRealFeel(currentData), createWind(currentData));
        realFeelAndWind.setSpacing(22);

        VBox main = new VBox(todayDescription, hourlyTemperature, hourlyPrecipitation, humidityAndUV, realFeelAndWind, makeSpacing());
        main.setSpacing(10);
        return main;
    }

    private VBox createTodayDescription(String detailedForecast) {
        Label description = new Label(detailedForecast);
        description.setWrapText(true);
        description.getStyleClass().add("forecast-description");

        ScrollPane scrollPane = new ScrollPane(description);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("transparent-scroll-pane");
        scrollPane.setMinHeight(40);
        scrollPane.setMaxHeight(117);
        description.setMaxWidth(Double.MAX_VALUE);

        description.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            double newScrollPaneHeight = Math.min(newHeight.doubleValue(), 117);
            scrollPane.setPrefHeight(newScrollPaneHeight);
        });

        VBox todayDescription = new VBox(scrollPane);
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

        HBox hourCards = new HBox();
        hourCards.setAlignment(Pos.TOP_LEFT);
        hourCards.setSpacing(14);

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

        ScrollPane hourCardsContainer = new ScrollPane(hourCards);
        hourCardsContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourCardsContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hourCardsContainer.setFitToHeight(true);
        hourCardsContainer.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        hourCardsContainer.setPadding(new Insets(0));

        hourCards.setMinWidth(Region.USE_PREF_SIZE);

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

        double[] xData = new double[7];
        double[] yData = new double[7];

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
            Label noRainLabel = new Label("No significant precipitation expected.");
            noRainLabel.getStyleClass().add("forecast-description");
            VBox main = new VBox(titleBox, noRainLabel);
            main.setSpacing(12);
            return createBox(main);
        }

        int chartHeight;
        if (highestChance < 15) {
            chartHeight = 60;
        } else if (highestChance < 30) {
            chartHeight = 70;
        } else if (highestChance < 50) {
            chartHeight = 80;
        } else {
            chartHeight = 90;
        }

        Pane chartPane = new Pane();
        chartPane.setMinHeight(chartHeight);
        chartPane.setPrefHeight(chartHeight);
        chartPane.setMaxHeight(chartHeight);
        chartPane.setClip(new Rectangle(0, 0, 300, chartHeight));

        double[] scaledY = new double[7];
        double verticalMargin = 10;
        double usableHeight = chartHeight - verticalMargin - 5;

        for (int i = 0; i < yData.length; i++) {
            scaledY[i] = verticalMargin + (1 - (yData[i] / 100.0)) * usableHeight;
        }

        Path curvePath = new Path();

        MoveTo moveTo = new MoveTo(xData[0], scaledY[0]);
        curvePath.getElements().add(moveTo);

        // Create a smooth curve through all points
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

        fillPath.getElements().add(new LineTo(xData[xData.length - 1], chartHeight));
        fillPath.getElements().add(new LineTo(xData[0], chartHeight));
        fillPath.getElements().add(new LineTo(xData[0], scaledY[0]));

        Stop[] stops = new Stop[] {
                new Stop(0.25, Color.rgb(255, 255, 255, 0.5)),
                new Stop(1, Color.rgb(255, 255, 255, 0))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, chartHeight, false, CycleMethod.NO_CYCLE, stops);
        fillPath.setFill(gradient);
        fillPath.setStroke(Color.TRANSPARENT);
        chartPane.getChildren().add(fillPath);
        chartPane.getChildren().add(curvePath);

        if (highestChance >= 5) {
            Text text = new Text(xData[highestIndex], Math.max(scaledY[highestIndex] - 10, 15),
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
        chartContainer.setPadding(new Insets(5, 8, 0, 8));
        chartContainer.setMinHeight(chartHeight + 5);
        chartContainer.setMaxHeight(chartHeight + 5);

        VBox graphGroup = new VBox(chartContainer, xaxis);
        VBox.setVgrow(graphGroup, Priority.ALWAYS);
        graphGroup.setAlignment(Pos.CENTER);
        graphGroup.setSpacing(3);

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
        } else {
            UVClassname = "Extreme";
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
        uvIndicator.setTranslateX(circleX + 2);
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

        Label humidityLabel = new Label("" + realFeel + " F°");
        humidityLabel.getStyleClass().add("big-text");

        VBox topContent = new VBox(titleBox, humidityLabel);
        topContent.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(topContent, Priority.NEVER);

        Label realFeelDesc = new Label(Math.abs(realFeel - currentData.getTemperature()) + "° " + ((realFeel-currentData.getTemperature() < 0) ? "below" : "above") + " the real temperature.");
        realFeelDesc.getStyleClass().add("subtext");
        realFeelDesc.setWrapText(true);

        VBox bottomContent = new VBox(realFeelDesc);
        bottomContent.setAlignment(Pos.BOTTOM_LEFT);
        bottomContent.setMinHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(bottomContent, Priority.ALWAYS);

        VBox main = new VBox(topContent, bottomContent);
        main.setSpacing(10);
        main.setMinHeight(137);
        main.setMinWidth(137);
        return createBox(main);
    }

    private VBox createWind(WeatherData.CurrentData currentData) {
        ImageView tempIcon = Helpers.getIcon("wind", Color.WHITE, 20);
        Label boxTitle = new Label("Wind");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(tempIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        ImageView direction = Helpers.getIcon("circle-arrow-out-up-right", Color.WHITE, 50);
        direction.setRotate(getIconWindRot(currentData.getWindDirection()));

        Label direcLabel = new Label(getFullWindDirection(currentData.getWindDirection()));
        direcLabel.getStyleClass().add("forecast-description");
        direcLabel.setWrapText(true);

        HBox middleContent = new HBox(direction, direcLabel);
        middleContent.setAlignment(Pos.CENTER_LEFT);
        middleContent.setSpacing(10);

        VBox topContent = new VBox(titleBox, middleContent);
        topContent.setSpacing(8);
        topContent.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(topContent, Priority.NEVER);

        String[] parts = currentData.getWindSpeed().split(" ");
        String speed = parts[0];
        Label speedLabel = new Label(speed + " ");
        speedLabel.getStyleClass().add("speed-text");
        Label mphLabel = new Label(parts[1] + " winds.");
        mphLabel.getStyleClass().add("subtext");

        HBox bottomContent = new HBox(speedLabel, mphLabel);
        bottomContent.setAlignment(Pos.BOTTOM_LEFT);
        bottomContent.setMinHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(bottomContent, Priority.ALWAYS);

        VBox main = new VBox(topContent, bottomContent);
        main.setSpacing(10);
        main.setMinHeight(137);
        main.setMinWidth(137);

        return createBox(main);
    }

    private Color getColorForForecast(String shortForecast, boolean isDaytime) {
        String lowerCaseForecast = shortForecast.toLowerCase();

        if (lowerCaseForecast.contains("sunny") || lowerCaseForecast.contains("clear")) {
            return isDaytime ? Color.web("#FFE32C") : Color.web("#FFFFFF");
        } else {
            return Color.web("#FFFFFF");
        }
    }

    private ImageView getWeatherIcon(String shortForecast, int size, boolean isDaytime) {
        String iconPath = "";
        String lowerCaseForecast = shortForecast.toLowerCase();

        if (lowerCaseForecast.contains("sunny") || lowerCaseForecast.contains("clear")) {
            iconPath += isDaytime ? "sun" : "moon";
        } else if (lowerCaseForecast.contains("cloudy")) {
            iconPath += isDaytime ? "cloudy" : "cloud-moon";
        } else if (lowerCaseForecast.contains("rain") || lowerCaseForecast.contains("shower")) {
            iconPath += "cloud-rain";
        } else if (lowerCaseForecast.contains("snow")) {
            iconPath += "cloud-snow";
        } else {
            iconPath += isDaytime ? "sun" : "moon";
        }

        Color iconColor = getColorForForecast(shortForecast, isDaytime);
        return Helpers.getIcon(iconPath, iconColor, size);
    }

    private void setBackground(VBox root, boolean isDaytime) {
        System.out.println(isDaytime);
        if (isDaytime) {
            root.setStyle("-fx-background-image: url('/images/SunnyDay.png');");
        } else {
            root.setStyle("-fx-background-image: url('/images/SunnyNight.png');");
        }
    }

    private int getIconWindRot(String windDirection) {
        switch (windDirection) {
            case "N":    return -45;
            case "NNE":  return -22;
            case "NE":   return 0;
            case "ENE":  return 22;
            case "E":    return 45;
            case "ESE":  return 67;
            case "SE":   return 90;
            case "SSE":  return 112;
            case "S":    return 135;
            case "SSW":  return 157;
            case "SW":   return 180;
            case "WSW":  return 202;
            case "W":    return 225;
            case "WNW":  return 247;
            case "NW":   return 270;
            case "NNW":  return 292;
            default:     return -45;
        }
    }

    private String getFullWindDirection(String windDirection) {
        switch (windDirection) {
            case "N":    return "North";
            case "NNE":  return "North Northeast";
            case "NE":   return "Northeast";
            case "ENE":  return "East Northeast";
            case "E":    return "East";
            case "ESE":  return "East Southeast";
            case "SE":   return "Southeast";
            case "SSE":  return "South Southeast";
            case "S":    return "South";
            case "SSW":  return "South Southwest";
            case "SW":   return "Southwest";
            case "WSW":  return "West Southwest";
            case "W":    return "West";
            case "WNW":  return "West Northwest";
            case "NW":   return "Northwest";
            case "NNW":  return "North Northwest";
            default:     return "Unknown";
        }
    }

    private VBox createWeeklyInfoCards(List<WeatherData.DailyData> dailyData) {
        VBox forecast = create7DayForecast(dailyData);
        VBox precipitation = create7DayPrecipitation(dailyData);
        VBox main = new VBox(forecast, precipitation);
        main.setSpacing(12);
        return main;
    }

    private VBox create7DayForecast(List<WeatherData.DailyData> dailyData) {
        ImageView calIcon = Helpers.getIcon("calendar-days", Color.WHITE, 20);
        Label boxTitle = new Label("7 Day Forecast");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(calIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        VBox dayRows = new VBox();
        dayRows.setSpacing(4);

        // for bar boundaries
        int[] minMax = getMinMax(dailyData);
        int min = minMax[0];
        int max = minMax[1];
        int range = max - min;

        for (WeatherData.DailyData day : dailyData) {
            int rawLow = (int) Math.round(day.getNightTemperature());
            int rawHigh = (int) Math.round(day.getDayTemperature());

            int low = Math.min(rawLow, rawHigh);
            int high = Math.max(rawLow, rawHigh);

            Label dayLabel = new Label(Helpers.getDayName(day.getTime()));
            dayLabel.getStyleClass().add("normal-text");
            dayLabel.setMinWidth(55);
            dayLabel.setAlignment(Pos.CENTER_LEFT);
            HBox dayIcon = new HBox(getWeatherIcon(day.getShortForecast(), 20, true), dayLabel);
            dayIcon.setAlignment(Pos.CENTER_LEFT);
            dayIcon.setSpacing(5);
            HBox.setHgrow(dayIcon, Priority.ALWAYS);

            // Weather bar and temps
            Label lowLabel = new Label(String.valueOf(low));
            Label highLabel = new Label(String.valueOf(high));
            lowLabel.getStyleClass().add("normal-text");
            lowLabel.setOpacity(0.75);
            highLabel.getStyleClass().add("normal-text");
            lowLabel.setMinWidth(40);
            highLabel.setMinWidth(40);
            lowLabel.setAlignment(Pos.CENTER);
            highLabel.setAlignment(Pos.CENTER);

            double lowPos = range > 0 ? ((double) (low - min) / range) * 100 : 0;
            double highPos = range > 0 ? ((double) (high - min) / range) * 100 : 100;
            double barWidth = highPos - lowPos;

            if (barWidth <= 0) {
                barWidth = 10;
            }

            System.out.println("low: " + low + ", high: " + high);
            System.out.println("lowPos: " + lowPos + ", highPos: " + highPos);
            System.out.println("Clip X: " + (lowPos * 1.56) + ", Width: " + (barWidth * 1.56));

            Region grayBar = new Region();
            grayBar.setMinWidth(156);
            grayBar.setMaxWidth(156);
            grayBar.setMinHeight(6);
            grayBar.setMaxHeight(6);
            grayBar.getStyleClass().add("gray-bar");

            Region tempBar = new Region();
            tempBar.setMinWidth(156);
            tempBar.setMaxWidth(156);
            tempBar.setMinHeight(6);
            tempBar.setMaxHeight(6);
            tempBar.getStyleClass().add("temp-bar");

            Rectangle clip = new Rectangle();
            clip.setWidth(barWidth * 1.56);
            clip.setHeight(6);
            clip.setX(lowPos * 1.56);
            clip.setArcHeight(10);
            clip.setArcWidth(10);
            tempBar.setClip(clip);

            StackPane barContainer = new StackPane();
            barContainer.setMinWidth(156);
            barContainer.setMaxWidth(156);
            barContainer.setMinHeight(6);
            barContainer.setMaxHeight(6);
            barContainer.getChildren().addAll(grayBar, tempBar);
            tempBar.toFront();

            HBox tempBarComponent = new HBox(lowLabel, barContainer, highLabel);
            tempBarComponent.setAlignment(Pos.CENTER_RIGHT);

            // Row layout
            HBox row = new HBox(dayIcon, tempBarComponent);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setSpacing(4);
            dayRows.getChildren().add(row);
        }
        VBox main = new VBox(titleBox, dayRows);
        main.setSpacing(5);
        return createBox(main);
    }

    private VBox create7DayPrecipitation(List<WeatherData.DailyData> dailyData) {
        ImageView calIcon = Helpers.getIcon("calendar-days", Color.WHITE, 20);
        Label boxTitle = new Label("Upcoming Precipitation");
        boxTitle.getStyleClass().add("card-title");
        HBox titleBox = new HBox(calIcon, boxTitle);
        titleBox.setSpacing(5);
        titleBox.setOpacity(0.75);

        HBox weekCols = new HBox();
        weekCols.setSpacing(10);
        weekCols.setAlignment(Pos.CENTER);

        for (WeatherData.DailyData day : dailyData) {
            VBox dayColumn = new VBox();
            dayColumn.setAlignment(Pos.CENTER);
            HBox.setHgrow(dayColumn, Priority.ALWAYS);

            double precipitation = day.getPrecipitationChance();

            // Precipitation percentage
            Label precipLabel = new Label((precipitation > 10) ? (int) precipitation + "%" : "");
            precipLabel.getStyleClass().add("precip-label");
            precipLabel.setMinWidth(Region.USE_PREF_SIZE);

            // Precipitation bar container
            StackPane precipBar = new StackPane();
            precipBar.setMinWidth(12);
            precipBar.setMaxWidth(12);
            precipBar.setMinHeight(100);
            precipBar.getStyleClass().add("precip-bar");

            Region precipFill = new Region();
            precipFill.setMinWidth(12);
            precipFill.setMaxWidth(12);
            precipFill.setMinHeight(precipitation);
            precipFill.getStyleClass().add("precip-fill");
            Rectangle clip = new Rectangle(12, precipitation);
            precipFill.setClip(clip);

            VBox barContainer = new VBox(precipLabel, precipFill);
            barContainer.setAlignment(Pos.BOTTOM_CENTER);
            barContainer.setSpacing(5);

            precipBar.getChildren().add(barContainer);
            VBox.setVgrow(precipBar, Priority.ALWAYS);

            dayColumn.getChildren().addAll(precipBar);
            weekCols.getChildren().add(dayColumn);
        }

        Rectangle line = new Rectangle();
        line.setHeight(1);
        line.widthProperty().bind(weekCols.widthProperty());
        line.getStyleClass().add("line");

        HBox daysAxis = new HBox();
        HBox.setHgrow(daysAxis, Priority.ALWAYS);
        daysAxis.setSpacing(39);
        daysAxis.setAlignment(Pos.CENTER);

        for (WeatherData.DailyData day : dailyData) {
            Label dayLetter = new Label(Helpers.getDayLetter(day.getTime()));
            dayLetter.getStyleClass().add("day-letter");
            dayLetter.setMinWidth(Region.USE_PREF_SIZE);

            VBox dayLetterBox = new VBox(dayLetter);
            dayLetterBox.setAlignment(Pos.CENTER);
            dayLetterBox.setMinWidth(Region.USE_PREF_SIZE);
            dayLetterBox.setMaxWidth(Region.USE_PREF_SIZE);
            HBox.setHgrow(dayLetterBox, Priority.ALWAYS);

            daysAxis.getChildren().add(dayLetterBox);
        }

        VBox barAndLine = new VBox(new VBox(weekCols, line), daysAxis);
        barAndLine.setAlignment(Pos.CENTER);
        barAndLine.setSpacing(5);

        VBox main = new VBox(titleBox, barAndLine);
        main.setSpacing(10);
        return createBox(main);
    }

    private Region makeSpacing() {
        Region region = new Region();
        region.setMinHeight(200);
        region.setStyle("-fx-background-color: rgba(255, 255, 255, 0);");
        return region;
    }

    private int[] getMinMax(List<WeatherData.DailyData> dailyData) {
        List<Double> temps = new ArrayList<>();
        for (WeatherData.DailyData day : dailyData) {
            temps.add(day.getDayTemperature());
            temps.add(day.getNightTemperature());
        }

        int[] tempArray = temps.stream().mapToInt(Double::intValue).toArray();
        int min = Arrays.stream(tempArray).min().orElse(Integer.MAX_VALUE);
        int max = Arrays.stream(tempArray).max().orElse(Integer.MIN_VALUE);

        return new int[]{min, max};
    }

    public static Scene getScene() {
        return scene;
    }
}