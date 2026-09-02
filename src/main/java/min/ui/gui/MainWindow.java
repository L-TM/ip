package min.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import min.Min;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Min min;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image minImage = new Image(
            this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /** Keeps the conversation pane scrolled to the latest dialog. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Provides the Min instance used to process commands.
     *
     * @param min The Min instance.
     */
    public void setMin(Min min) {
        this.min = min;
        this.dialogContainer.getChildren().add(
                DialogBox.getMinDialog(
                        this.min.getWelcomeMessage(), this.minImage));
    }

    /**
     * Displays the user's input and Min's response in dialog boxes.
     * Clears the input field after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = this.min.getResponse(input);
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, this.userImage),
                DialogBox.getMinDialog(response, this.minImage)
        );
        this.userInput.clear();
    }
}
