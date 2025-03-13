import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ZipcodeManager {
    private static final String FILE_PATH = "zipcodes.txt";
    private static List<String> zipCodesCache; // Cache for zip codes

    // Load zip codes from file (cached)
    public static List<String> loadZipCodes() {
        if (zipCodesCache != null) {
            return zipCodesCache; // Return cached data
        }

        zipCodesCache = new ArrayList<>();
        try (InputStream inputStream = ZipcodeManager.class.getClassLoader().getResourceAsStream(FILE_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.err.println("File not found: " + FILE_PATH);
                return zipCodesCache;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                zipCodesCache.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Failed to load zip codes: " + e.getMessage());
        }
        return zipCodesCache;
    }

    // Save zip codes to file
    public static void saveZipCodes(List<String> zipCodes) {
        zipCodesCache = new ArrayList<>(zipCodes); // Update cache
        try (OutputStream outputStream = new FileOutputStream("src/main/resources/" + FILE_PATH);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            for (String zipCode : zipCodes) {
                writer.write(zipCode);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save zip codes: " + e.getMessage());
        }
    }
}