package min.ui.cli;

import java.io.IOException;

import min.Min;
import min.exception.MinException;

/** Starts the command-line version of Min. */
public class CliLauncher {
    private static final String LOAD_DATA_ERROR_MESSAGE = "Unable to load saved data.";

    /**
     * Starts Min using standard input and output.
     *
     * @param args Command-line arguments, which Min does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Min min;

        try {
            min = new Min();
        } catch (MinException e) {
            ui.showError(e.getMessage());
            return;
        } catch (IOException e) {
            ui.showError(LOAD_DATA_ERROR_MESSAGE);
            return;
        }

        ui.showWelcome(min.getWelcomeMessage());

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            String response = min.getResponse(command);
            ui.showResponse(response);

            if (min.isExitCommand(command)) {
                break;
            }
        }
    }
}
