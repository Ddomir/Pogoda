import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;

public class DataFetcher {

    // Convert Zipcode to Lat, Lng
    private static ZipcodeLocator zipcodeLocator = new ZipcodeLocator();

    public static WeatherData.LocationData getLocationData(String zipcode) {
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

        // Fetch point data (gridX, gridY, region, etc.)
        fetchPointData(locationData);

        return locationData;
    }

    // Fetch point data from Lat, Lng and update LocationData object
    private static void fetchPointData(WeatherData.LocationData locationData) {
        // Format latitude and longitude to 4 decimal places
        DecimalFormat df = new DecimalFormat("#.####");
        String lat = df.format(locationData.getLat());
        String lng = df.format(locationData.getLng());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.gov/points/" + lat + "," + lng))
                .header("Accept", "application/geo+json")
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (response == null || response.statusCode() != 200) {
            System.err.println("Failed to fetch point data: " +
                    (response != null ? "Status: " + response.statusCode() : "Response was null"));
            if (response != null) {
                System.err.println("Response body: " + response.body());
            }
            return;
        }

        String jsonResponse = response.body();

        try {
            // Extract the properties object from the JSON
            String propertiesJson = extractJsonObject(jsonResponse, "properties");
            if (propertiesJson != null) {
                locationData.setRegion(extractJsonString(propertiesJson, "cwa"));
                locationData.setGridX(extractJsonNumber(propertiesJson, "gridX"));
                locationData.setGridY(extractJsonNumber(propertiesJson, "gridY"));
                locationData.setForecastURL(extractJsonString(propertiesJson, "forecast"));
                locationData.setHourlyForecastURL(extractJsonString(propertiesJson, "forecastHourly"));
            } else {
                System.err.println("Could not extract properties object from JSON");
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String extractJsonObject(String json, String key) {
        String fullKey = "\"" + key + "\":";
        int startIndex = json.indexOf(fullKey);
        if (startIndex == -1) return null;

        startIndex += fullKey.length();
        // Skip any whitespace
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }

        // Check if we have an object
        if (startIndex >= json.length() || json.charAt(startIndex) != '{') return null;

        // Find the matching closing brace
        int braceCount = 1;
        int endIndex = startIndex + 1;
        while (braceCount > 0 && endIndex < json.length()) {
            char c = json.charAt(endIndex);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;
            endIndex++;
        }

        if (braceCount != 0) return null;
        return json.substring(startIndex, endIndex);
    }

    private static String extractJsonString(String json, String key) {
        String fullKey = "\"" + key + "\":";
        int startIndex = json.indexOf(fullKey);
        if (startIndex == -1) return null;

        startIndex += fullKey.length();
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }

        // Check if we have a string value with quotes
        if (startIndex >= json.length() || json.charAt(startIndex) != '"') return null;

        startIndex++; // Skip the opening quote
        int endIndex = json.indexOf('"', startIndex);
        if (endIndex == -1) return null;

        return json.substring(startIndex, endIndex);
    }

    private static String extractJsonNumber(String json, String key) {
        // Look for the key with quotation marks
        String fullKey = "\"" + key + "\":";
        int startIndex = json.indexOf(fullKey);
        if (startIndex == -1) return null;

        startIndex += fullKey.length();
        // Skip any whitespace
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }

        // Find the end of the number (comma, closing brace, or whitespace)
        int endIndex = startIndex;
        while (endIndex < json.length()) {
            char c = json.charAt(endIndex);
            if (c == ',' || c == '}' || Character.isWhitespace(c)) break;
            endIndex++;
        }

        if (startIndex == endIndex) return null;

        // Trim any whitespace from the extracted value
        return json.substring(startIndex, endIndex).trim();
    }


}