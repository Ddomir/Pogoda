import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MyWeatherAPI {

    // Fetch point data (gridX, gridY, region, forecast URL, forecastHourly URL) from latitude and longitude
    public static Map<String, String> getPointData(double lat, double lng) {
        DecimalFormat df = new DecimalFormat("#.####");
        String formattedLat = df.format(lat);
        String formattedLng = df.format(lng);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.gov/points/" + formattedLat + "," + formattedLng))
                .header("Accept", "application/geo+json")
                .header("User-Agent", "JavaWeatherApp/1.0 (your-email@example.com)")  // Important for NWS API
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String jsonResponse = response.body();
        return parsePointData(jsonResponse);
    }

    // Parse point data
    private static Map<String, String> parsePointData(String json) {
        Map<String, String> pointData = new HashMap<>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            JSONObject properties = (JSONObject) jsonObject.get("properties");

            pointData.put("region", (String) properties.get("cwa"));
            pointData.put("gridX", properties.get("gridX").toString());
            pointData.put("gridY", properties.get("gridY").toString());
            pointData.put("forecastURL", (String) properties.get("forecast"));
            pointData.put("forecastHourlyURL", (String) properties.get("forecastHourly"));
        } catch (ParseException e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
            e.printStackTrace();
        }

        return pointData;
    }

    // Fetch hourly data
    public static List<WeatherData.HourlyData> getHourlyData(String hourlyURL) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(hourlyURL))
                .header("User-Agent", "JavaWeatherApp/1.0 (your-email@example.com)")  // Important for NWS API
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (response == null || response.statusCode() != 200) {
            System.err.println("Failed to fetch hourly data: " +
                    (response != null ? "Status: " + response.statusCode() + ", Body: " + response.body() : "Response was null"));
            return null;
        }

        String jsonResponse = response.body();
        return parseHourlyData(jsonResponse);
    }

    // Parse hourly data
    private static List<WeatherData.HourlyData> parseHourlyData(String json) {
        List<WeatherData.HourlyData> hourlyDataList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            JSONObject properties = (JSONObject) jsonObject.get("properties");
            JSONArray periods = (JSONArray) properties.get("periods");

            for (Object periodObj : periods) {
                JSONObject period = (JSONObject) periodObj;

                // Extract data for each period
                String startTime = (String) period.get("startTime");
                double temperature = ((Number) period.get("temperature")).doubleValue();
                String shortForecast = (String) period.get("shortForecast");
                boolean isDaytime = (boolean) period.get("isDaytime");

                // Handle nested "probabilityOfPrecipitation"
                JSONObject probabilityOfPrecipitation = (JSONObject) period.get("probabilityOfPrecipitation");
                int precipitationChance = probabilityOfPrecipitation != null ?
                        ((Number) probabilityOfPrecipitation.get("value")).intValue() : 0;

                // Format the time in 12-hour am/pm format
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(startTime);
                LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                String formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("ha")).toLowerCase();

                // Create and populate HourlyData object
                WeatherData.HourlyData hourlyData = new WeatherData.HourlyData();
                hourlyData.setTime(formattedTime);
                hourlyData.setTemperature(temperature);
                hourlyData.setShortForecast(shortForecast);
                hourlyData.setPrecipitationChance(precipitationChance);
                hourlyData.setIsDaytime(isDaytime);

                hourlyDataList.add(hourlyData);

                // Stop after 12 hours
                if (hourlyDataList.size() >= 12) {
                    break;
                }
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
            e.printStackTrace();
        }

        return hourlyDataList;
    }

    // Fetch daily data
    public static List<WeatherData.DailyData> getDailyData(String dailyURL) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dailyURL))
                .header("User-Agent", "JavaWeatherApp/1.0 (your-email@example.com)")  // Important for NWS API
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (response == null || response.statusCode() != 200) {
            System.err.println("Failed to fetch daily data: " +
                    (response != null ? "Status: " + response.statusCode() + ", Body: " + response.body() : "Response was null"));
            return null;
        }

        String jsonResponse = response.body();
        return parseDailyData(jsonResponse);
    }

    // Parse daily data
    private static List<WeatherData.DailyData> parseDailyData(String json) {
        List<WeatherData.DailyData> dailyDataList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            JSONObject properties = (JSONObject) jsonObject.get("properties");
            JSONArray periods = (JSONArray) properties.get("periods");

            // Iterate through periods and group by day
            for (int i = 0; i < periods.size(); i += 2) { // Each day has 2 periods (day and night)
                JSONObject dayPeriod = (JSONObject) periods.get(i);
                JSONObject nightPeriod = (JSONObject) periods.get(i + 1);

                // Extract day data
                String date = ((String) dayPeriod.get("startTime")).split("T")[0]; // Extract date (YYYY-MM-DD)
                double dayTemperature = ((Number) dayPeriod.get("temperature")).doubleValue();
                String dayForecast = (String) dayPeriod.get("shortForecast");
                String dayDetailedForecast = (String) dayPeriod.get("detailedForecast");

                // Extract night data
                double nightTemperature = ((Number) nightPeriod.get("temperature")).doubleValue();
                String nightForecast = (String) nightPeriod.get("shortForecast");

                // Extract precipitation chance
                JSONObject probabilityOfPrecipitation = (JSONObject) dayPeriod.get("probabilityOfPrecipitation");
                int precipitationChance = 0; // Default to 0 if precipitation data is missing
                if (probabilityOfPrecipitation != null) {
                    Number precipitationValue = (Number) probabilityOfPrecipitation.get("value");
                    if (precipitationValue != null) {
                        precipitationChance = precipitationValue.intValue();
                    }
                }

                // Create and populate DailyData object
                WeatherData.DailyData dailyData = new WeatherData.DailyData();
                dailyData.setTime(date);
                dailyData.setDayTemperature(dayTemperature);
                dailyData.setNightTemperature(nightTemperature);
                dailyData.setPrecipitationChance(precipitationChance);
                dailyData.setShortForecast(dayForecast + " / " + nightForecast);
                dailyData.setDetailedForecast(dayDetailedForecast);

                dailyDataList.add(dailyData);

                // Stop after 7 days
                if (dailyDataList.size() >= 7) {
                    break;
                }
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
            e.printStackTrace();
        }

        return dailyDataList;
    }

    // Fetch UV index data from EPA API
    public static int getUVIndex(String zipcode) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://data.epa.gov/efservice/getEnvirofactsUVDAILY/ZIP/" + zipcode + "/JSON"))
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 to indicate an error
        }

        String jsonResponse = response.body();
        return parseUVIndex(jsonResponse);
    }

    // Parse UV index data from EPA API response
    private static int parseUVIndex(String json) {
        JSONParser parser = new JSONParser();

        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            if (jsonArray.isEmpty()) {
                System.err.println("No UV index data found for the provided ZIP code");
                return -1;
            }

            // Get the first entry (most recent UV index)
            JSONObject uvData = (JSONObject) jsonArray.get(0);
            String uvIndexStr = (String) uvData.get("UV_INDEX");

            // Parse the UV index as an integer
            return Integer.parseInt(uvIndexStr);
        } catch (ParseException e) {
            System.err.println("Failed to parse UV index JSON response: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse UV index value: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // Fetch current hour's data
    public static WeatherData.CurrentData getCurrentHourlyData(String hourlyURL) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(hourlyURL))
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String jsonResponse = response.body();
        return parseCurrentHourlyData(jsonResponse);
    }

    // Parse current hour's data
    private static WeatherData.CurrentData parseCurrentHourlyData(String json) {
        JSONParser parser = new JSONParser();
        WeatherData.CurrentData currentData = new WeatherData.CurrentData();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            JSONObject properties = (JSONObject) jsonObject.get("properties");
            JSONArray periods = (JSONArray) properties.get("periods");

            if (periods != null && !periods.isEmpty()) {
                JSONObject firstHour = (JSONObject) periods.get(0);

                // Extract temperature and short forecast
                currentData.setTemperature(((Number) firstHour.get("temperature")).doubleValue());
                currentData.setShortForecast((String) firstHour.get("shortForecast"));
                currentData.setIsDaytime((boolean) firstHour.get("isDaytime"));

                // Extract humidity
                JSONObject relativeHumidity = (JSONObject) firstHour.get("relativeHumidity");
                if (relativeHumidity != null) {
                    currentData.setHumidity(((Number) relativeHumidity.get("value")).intValue());
                }

                // Extract dew point
                JSONObject dewpoint = (JSONObject) firstHour.get("dewpoint");
                if (dewpoint != null) {
                    double dewPointCelsius = ((Number) dewpoint.get("value")).doubleValue();
                    double dewPointFahrenheit = convertCelsiusToFahrenheit(dewPointCelsius);
                    dewPointFahrenheit = Math.round(dewPointFahrenheit * 10) / 10.0;
                    currentData.setDewPoint(dewPointFahrenheit);
                }

                // Extract wind direction and speed
                currentData.setWindDirection((String) firstHour.get("windDirection"));
                currentData.setWindSpeed((String) firstHour.get("windSpeed"));
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse hourly JSON response: " + e.getMessage());
            e.printStackTrace();
        }

        return currentData;
    }

    // C to F (for dewpoint)
    private static double convertCelsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }
}