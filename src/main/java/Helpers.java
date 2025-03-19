import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Helpers {
    public static ImageView getIcon(String filename, Color color, int size) {
        String iconPath = "/icons/" + filename + ".png";

        ImageView iconView = new ImageView(new Image(iconPath));
        iconView.setFitWidth(size);
        iconView.setFitHeight(size);
        iconView.setPreserveRatio(true);

        // Adjust icon color based on forecast
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(1);
        InnerShadow colorOverlay = new InnerShadow();
        colorOverlay.setColor(color);
        colorOverlay.setRadius(100);
        colorOverlay.setChoke(1);
        Blend blend = new Blend(BlendMode.SRC_ATOP);
        blend.setTopInput(colorOverlay);
        blend.setBottomInput(colorAdjust);
        iconView.setEffect(blend);

        return iconView;
    }

    public static int calculateRealFeel(double temperature, int humidity, double dewPoint, String windSpeed) {
        double windSpeedMph = Double.parseDouble(windSpeed.replaceAll("[^0-9.]", ""));

        if (temperature >= 50) {
            // Heat Index (for warm temperatures)
            double heatIndex = calculateHeatIndex(temperature, humidity);
            return (int) Math.round(heatIndex);
        } else {
            // Wind Chill (for cold temperatures)
            double windChill = calculateWindChill(temperature, windSpeedMph);
            return (int) Math.round(windChill);
        }
    }

    private static double calculateHeatIndex(double temperature, int humidity) {
        double T = temperature;
        double R = humidity;

        double heatIndex = -42.379 +
                2.04901523 * T +
                10.14333127 * R +
                -0.22475541 * T * R +
                -0.00683783 * T * T +
                -0.05481717 * R * R +
                0.00122874 * T * T * R +
                0.00085282 * T * R * R +
                -0.00000199 * T * T * R * R;

        return heatIndex;
    }

    private static double calculateWindChill(double temperature, double windSpeedMph) {
        double T = temperature;
        double V = windSpeedMph;

        double windChill = 35.74 +
                0.6215 * T +
                -35.75 * Math.pow(V, 0.16) +
                0.4275 * T * Math.pow(V, 0.16);

        return windChill;
    }
}
