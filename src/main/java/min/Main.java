package min;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import min.exception.MinException;
import min.ui.Messages;
import min.ui.gui.MainWindow;

/** Starts Min's JavaFX user interface. */
public class Main extends Application {
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
            stage.setTitle(Messages.APP_NAME);
            stage.setResizable(false);
            stage.show();
        } catch (MinException e) {
            showStartupError(e.getMessage());
        } catch (IOException e) {
            showStartupError(Messages.STARTUP_ERROR);
        } catch (RuntimeException e) {
            e.printStackTrace();
            showStartupError(Messages.UNEXPECTED_STARTUP_ERROR);
        }
    }

    /**
     * Shows an error dialog when Min cannot start.
     *
     * @param message The error message to display.
     */
    private void showStartupError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Messages.APP_NAME);
        alert.setHeaderText(Messages.STARTUP_ERROR_HEADER);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
