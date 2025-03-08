import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class ZipcodeLocator {

    private Map<String, Location> zipCodeMap;

    public ZipcodeLocator() {
        zipCodeMap = new HashMap<>();
        loadZipCodes();
    }

    // Location class to hold the details of each ZIP code
    public static class Location {
        private String zip;
        private double lat;
        private double lng;
        private String city;
        private String stateId;
        private String countyName;
        private String region;
        private String gridx;
        private String gridy;
        private String forecastURL;
        private String forecastHourlyURL;

        public Location(String zip, double lat, double lng, String city, String stateId, String countyName) {
            this.zip = zip;
            this.lat = lat;
            this.lng = lng;
            this.city = city;
            this.stateId = stateId;
            this.countyName = countyName;
        }

        public void setPointData(String region, String gridx, String gridy, String forecastURL, String forecastHourlyURL) {
            this.region = region;
            this.gridx = gridx;
            this.gridy = gridy;
            this.forecastURL = forecastURL;
            this.forecastHourlyURL = forecastHourlyURL;
        }

        public double getLat() { return lat; }
        public double getLng() { return lng; }
        public String getCity() { return city; }
        public String getStateId() { return stateId; }
        public String getCountyName() { return countyName; }
        public String getRegion() { return region; }
        public String getGridx() { return gridx; }
        public String getGridy() { return gridy; }
        public String getForecastURL() { return forecastURL; }
        public String getForecastHourlyURL() { return forecastHourlyURL; }
    }

    // Method to load ZIP codes from the CSV file
    private void loadZipCodes() {
        String csvFile = "/uszips.csv";
        try (InputStream inputStream = getClass().getResourceAsStream(csvFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            reader.readLine(); // Skip the header line
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 6) {
                    String zip = fields[0].replace("\"", "");
                    double lat = Double.parseDouble(fields[1].replace("\"", ""));
                    double lng = Double.parseDouble(fields[2].replace("\"", ""));
                    String city = fields[3].replace("\"", "");
                    String stateId = fields[4].replace("\"", "");
                    String countyName = fields[5].replace("\"", "");
                    Location location = new Location(zip, lat, lng, city, stateId, countyName);
                    zipCodeMap.put(zip, location);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get latitude and longitude by ZIP code
    public Location getLocationData(String zipCode) {
        Location location = zipCodeMap.get(zipCode);
        if (location != null) {
            return location;
        } else {
            return null;
        }
    }

}
