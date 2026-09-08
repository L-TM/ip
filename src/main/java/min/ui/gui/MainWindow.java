package min.ui.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import min.Min;

/**
 * Controller for the main GUI.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

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
                DialogBox.createMinDialog(
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
                DialogBox.createUserDialog(input, this.userImage),
                DialogBox.createMinDialog(response, this.minImage)
        );
        this.userInput.clear();

        if (this.min.isExitCommand(input)) {
            exitAfterDelay();
        }
    }

    /** Disables further input and closes the application after a short delay. */
    private void exitAfterDelay() {
        this.userInput.setDisable(true);
        this.sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
