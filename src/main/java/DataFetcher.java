import java.util.List;
import java.util.Map;

public class DataFetcher {

    // Convert Zipcode to Lat, Lng
    private static ZipcodeLocator zipcodeLocator = new ZipcodeLocator();

    /**
     * Fetches all weather data (location, hourly, daily, current, etc.) and populates a WeatherData object.
     *
     * @param zipcode The ZIP code to fetch weather data for.
     * @return A populated WeatherData object, or null if the data could not be fetched.
     */
    public static WeatherData fetchWeatherData(String zipcode) {
        WeatherData weatherData = new WeatherData();

        // Step 1: Fetch location data (point data)
        WeatherData.LocationData locationData = getLocationData(zipcode);
        if (locationData == null) {
            System.err.println("Failed to fetch location data for ZIP code: " + zipcode);
            return null;
        }
        weatherData.setLocationData(locationData);

        // Step 2: Fetch hourly data
        List<WeatherData.HourlyData> hourlyData = getHourlyData(locationData.getHourlyForecastURL());
        if (hourlyData == null) {
            System.err.println("Failed to fetch hourly data for ZIP code: " + zipcode);
            return null;
        }
        weatherData.setHourlyData(hourlyData);

        // Step 3: Fetch daily data
        List<WeatherData.DailyData> dailyData = getDailyData(locationData.getForecastURL());
        if (dailyData == null) {
            System.err.println("Failed to fetch daily data for ZIP code: " + zipcode);
            return null;
        }
        weatherData.setDailyData(dailyData);

        // Step 4: Fetch current data (temperature, UV index, etc.)
        WeatherData.CurrentData currentData = getCurrentData(zipcode, hourlyData);
        if (currentData == null) {
            System.err.println("Failed to fetch current data for ZIP code: " + zipcode);
            return null;
        }
        weatherData.setCurrentData(currentData);

        return weatherData;
    }

    // Fetch location data (point data)
    private static WeatherData.LocationData getLocationData(String zipcode) {
        ZipcodeLocator.Location location = zipcodeLocator.getLocationData(zipcode);
        if (location == null) {
            return null;
        }

        // Create and populate LocationData
        WeatherData.LocationData locationData = new WeatherData.LocationData();
        locationData.setZip(zipcode);
        locationData.setLat(location.getLat());
        locationData.setLng(location.getLng());
        locationData.setCity(location.getCity());
        locationData.setState(location.getStateId());
        locationData.setCounty(location.getCountyName());

        // Fetch point data (gridX, gridY, region, etc.) using MyWeatherAPI
        Map<String, String> pointData = MyWeatherAPI.getPointData(location.getLat(), location.getLng());
        if (pointData != null) {
            locationData.setRegion(pointData.get("region"));
            locationData.setGridX(pointData.get("gridX"));
            locationData.setGridY(pointData.get("gridY"));
            locationData.setForecastURL(pointData.get("forecastURL"));
            locationData.setHourlyForecastURL(pointData.get("forecastHourlyURL"));
        } else {
            System.err.println("Failed to fetch point data for ZIP code: " + zipcode);
        }

        return locationData;
    }

    // Fetch hourly data
    private static List<WeatherData.HourlyData> getHourlyData(String hourlyURL) {
        return MyWeatherAPI.getHourlyData(hourlyURL);
    }

    // Fetch daily data
    private static List<WeatherData.DailyData> getDailyData(String dailyURL) {
        return MyWeatherAPI.getDailyData(dailyURL);
    }

    // Fetch current data (temperature, UV index, etc.)
    private static WeatherData.CurrentData getCurrentData(String zipcode, List<WeatherData.HourlyData> hourlyData) {
        WeatherData.CurrentData currentData = new WeatherData.CurrentData();

        // Use the first hour's data for current temperature and forecast
        if (hourlyData != null && !hourlyData.isEmpty()) {
            WeatherData.HourlyData firstHour = hourlyData.get(0);
            currentData.setTemperature(firstHour.getTemperature());
            currentData.setShortForecast(firstHour.getShortForecast());
        }

        // Fetch UV index from EPA API
        int uvIndex = MyWeatherAPI.getUVIndex(zipcode);
        if (uvIndex != -1) {
            currentData.setUvIndex(uvIndex);
        } else {
            System.err.println("Failed to fetch UV index for ZIP code: " + zipcode);
        }

        // Fetch additional current data (humidity, dew point, wind direction, wind speed) from hourly forecast
        WeatherData.LocationData locationData = getLocationData(zipcode);
        if (locationData != null) {
            WeatherData.CurrentData additionalData = MyWeatherAPI.getCurrentHourlyData(locationData.getHourlyForecastURL());
            if (additionalData != null) {
                currentData.setHumidity(additionalData.getHumidity());
                currentData.setDewPoint(additionalData.getDewPoint());
                currentData.setWindDirection(additionalData.getWindDirection());
                currentData.setWindSpeed(additionalData.getWindSpeed());
            }
        }

        return currentData;
    }
}