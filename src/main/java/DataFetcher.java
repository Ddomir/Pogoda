import java.util.List;
import java.util.Map;

public class DataFetcher {

    // Convert Zipcode to Lat, Lng
    private static ZipcodeLocator zipcodeLocator = new ZipcodeLocator();

    /**
     * Fetches all weather data (location, hourly, etc.) and populates a WeatherData object.
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
}