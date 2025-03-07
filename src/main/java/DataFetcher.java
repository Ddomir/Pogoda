import weather.Period;
import weather.WeatherAPI;

import java.util.ArrayList;

public class DataFetcher {

    // Get Day/Night Forecast
    public static ArrayList<Period> getDailyForecast(String region, int gridX, int gridY) {
        return WeatherAPI.getForecast(region, gridX, gridY);
    }
}
