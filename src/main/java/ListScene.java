import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ListScene {
    private static Scene scene;
    private ListView<String> zipCodeListView;
    private List<String> zipCodes;

    public ListScene(Stage primaryStage) {
        // Load zip codes from file
        zipCodes = ZipcodeManager.loadZipCodes();

        // Create ListView to display zip codes
        zipCodeListView = new ListView<>();
        zipCodeListView.getItems().addAll(zipCodes);

        // TextField for adding new zip codes
        TextField zipCodeInput = new TextField();
        zipCodeInput.setPromptText("Enter ZIP Code");

        // Buttons
        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        Button switchToCurrentSceneButton = new Button("Switch to Current View");

        // Add button action
        addButton.setOnAction(e -> {
            String newZipCode = zipCodeInput.getText().trim();
            if (!newZipCode.isEmpty() && !zipCodes.contains(newZipCode)) {
                zipCodes.add(newZipCode);
                zipCodeListView.getItems().add(newZipCode);
                ZipcodeManager.saveZipCodes(zipCodes); // Save to file
                zipCodeInput.clear();
            }
        });

        // Remove button action
        removeButton.setOnAction(e -> {
            String selectedZipCode = zipCodeListView.getSelectionModel().getSelectedItem();
            if (selectedZipCode != null) {
                zipCodes.remove(selectedZipCode);
                zipCodeListView.getItems().remove(selectedZipCode);
                ZipcodeManager.saveZipCodes(zipCodes); // Save to file
            }
        });

        // Switch to CurrentWeatherScene with selected zip code
        zipCodeListView.setOnMouseClicked(e -> {
            String selectedZipCode = zipCodeListView.getSelectionModel().getSelectedItem();
            if (selectedZipCode != null) {
                primaryStage.setScene(new CurrentWeatherScene(primaryStage, selectedZipCode).getScene());
            }
        });

        // Layout
        HBox buttonBox = new HBox(addButton, removeButton);
        VBox root = new VBox(zipCodeListView, zipCodeInput, buttonBox, switchToCurrentSceneButton);

        // Scene
        scene = new Scene(root, 402, 874);

        // Event handling for switching scenes
        switchToCurrentSceneButton.setOnAction(e -> primaryStage.setScene(CurrentWeatherScene.getScene()));
    }

    public static Scene getScene() {
        return scene;
    }
}