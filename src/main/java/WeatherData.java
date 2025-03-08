import java.util.List;

public class WeatherData {
    public static class LocationData {
        private String zip;
        private double lat;
        private double lng;
        private String city;
        private String state;
        private String county;
        private String region;
        private String gridX;
        private String gridY;
        private String forecastURL;
        private String hourlyForecastURL;

        // Getters and Setters
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getCounty() { return county; }
        public void setCounty(String county) { this.county = county; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getGridX() { return gridX; }
        public void setGridX(String gridX) { this.gridX = gridX; }

        public String getGridY() { return gridY; }
        public void setGridY(String gridY) { this.gridY = gridY; }

        public String getForecastURL() { return forecastURL; }
        public void setForecastURL(String forecastURL) { this.forecastURL = forecastURL; }

        public String getHourlyForecastURL() { return hourlyForecastURL; }
        public void setHourlyForecastURL(String hourlyForecastURL) { this.hourlyForecastURL = hourlyForecastURL; }
    }

    public static class CurrentData {
        private double temperature;
        private String shortForecast;
        private int uvIndex;
        private int humidity;
        private double dewPoint;
        private String windDirection;
        private String windSpeed;

        // Getters and Setters
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public String getShortForecast() { return shortForecast; }
        public void setShortForecast(String shortForecast) { this.shortForecast = shortForecast; }

        public int getUvIndex() { return uvIndex; }
        public void setUvIndex(int uvIndex) { this.uvIndex = uvIndex; }

        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }

        public double getDewPoint() { return dewPoint; }
        public void setDewPoint(double dewPoint) { this.dewPoint = dewPoint; }

        public String getWindDirection() { return windDirection; }
        public void setWindDirection(String windDirection) { this.windDirection = windDirection; }

        public String getWindSpeed() { return windSpeed; }
        public void setWindSpeed(String windSpeed) { this.windSpeed = windSpeed; }
    }

    public static class HourlyData {
        private String time; // In 12hr am/pm format
        private double temperature;
        private String shortForecast;
        private int precipitationChance;

        // Getters and Setters
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public String getShortForecast() { return shortForecast; }
        public void setShortForecast(String shortForecast) { this.shortForecast = shortForecast; }

        public int getPrecipitationChance() { return precipitationChance; }
        public void setPrecipitationChance(int precipitationChance) { this.precipitationChance = precipitationChance; }
    }

    public static class DailyData {
        private String date; // In 12hr am/pm format
        private double dayTemperature;
        private double nightTemperature;
        private int precipitationChance;
        private String shortForecast;

        // Getters and Setters
        public String getTime() { return date; }
        public void setTime(String time) { this.date = time; }

        public double getDayTemperature() { return dayTemperature; }
        public void setDayTemperature(double temperature) { this.dayTemperature = temperature; }

        public double getNightTemperature() { return nightTemperature; }
        public void setNightTemperature(double temperature) { this.nightTemperature = temperature; }

        public int getPrecipitationChance() { return precipitationChance; }
        public void setPrecipitationChance(int precipitationChance) { this.precipitationChance = precipitationChance; }

        public String getShortForecast() { return shortForecast; }
        public void setShortForecast(String shortForecast) { this.shortForecast = shortForecast; }
    }

    private LocationData locationData;
    private CurrentData currentData;
    private List<HourlyData> hourlyData;
    private List<DailyData> dailyData;

    // Getters and Setters
    public LocationData getLocationData() { return locationData; }
    public void setLocationData(LocationData locationData) { this.locationData = locationData; }

    public CurrentData getCurrentData() { return currentData; }
    public void setCurrentData(CurrentData currentData) { this.currentData = currentData; }

    public List<HourlyData> getHourlyData() { return hourlyData; }
    public void setHourlyData(List<HourlyData> hourlyData) { this.hourlyData = hourlyData; }

    public List<DailyData> getDailyData() { return dailyData; }
    public void setDailyData(List<DailyData> dailyData) { this.dailyData = dailyData; }
}