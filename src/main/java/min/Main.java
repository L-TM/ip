package min;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import min.exception.MinException;
import min.ui.gui.MainWindow;

/** Starts Min's JavaFX user interface. */
public class Main extends Application {
    private static final String UNEXPECTED_STARTUP_ERROR_MESSAGE =
            "Min could not start because of an unexpected error.";

    @Override
    public void start(Stage stage) {
        try {
            Min min = new Min();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();

            MainWindow controller = fxmlLoader.getController();
            controller.setMin(min);

            Scene scene = new Scene(mainLayout);
            stage.setScene(scene);
            stage.setTitle("Min");
            stage.setResizable(false);
            stage.show();
        } catch (MinException e) {
            showStartupError(e.getMessage());
        } catch (IOException e) {
            showStartupError("Unable to start Min.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            showStartupError(UNEXPECTED_STARTUP_ERROR_MESSAGE);
        }
    }

    /**
     * Shows an error dialog when Min cannot start.
     *
     * @param message The error message to display.
     */
    private void showStartupError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Min");
        alert.setHeaderText("Unable to start Min");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
