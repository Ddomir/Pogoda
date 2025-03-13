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
}
