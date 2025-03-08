import weather.Period;
import weather.WeatherAPI;

import java.util.ArrayList;

public class DataFetcher {
    // Convert Zipcode to Lat, Long
    private static ZipcodeLocator zipcodeLocator = new ZipcodeLocator();
    public static ZipcodeLocator.Location GetZipcodeData(String zipcode) {
        return zipcodeLocator.getLocationData(zipcode);
    }



    // Get Day/Night Forecast
    public static ArrayList<Period> getDailyForecast(String region, int gridX, int gridY) {
        return WeatherAPI.getForecast(region, gridX, gridY);
    }
}
