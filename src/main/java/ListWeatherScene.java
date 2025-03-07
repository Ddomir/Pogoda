import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListWeatherScene {
    private static Scene scene;
    private Button switchToCurrentSceneButton;

    public ListWeatherScene(Stage primaryStage) {
        switchToCurrentSceneButton = new Button("Switch to Current View");

        // Layout
        VBox root = new VBox(switchToCurrentSceneButton);

        // Scene
        scene = new Scene(root, 700, 700);

        // Event handling
        switchToCurrentSceneButton.setOnAction(e -> primaryStage.setScene(CurrentWeatherScene.getScene()));
    }

    public static Scene getScene() {
        return scene;
    }
}