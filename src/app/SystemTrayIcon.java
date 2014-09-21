package app;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SystemTrayIcon extends Main {
    static PopupMenu popup;
    static SystemTray sysTray;
    static ActionListener listener;
    static String command;
    Label trayDigits;
    Scene trayScene;
    BufferedImage buffTrayIcon;
    WritableImage wim = new WritableImage(16, 16);
    TrayIcon trayIcon = null;


    public SystemTrayIcon() {

        if (SystemTray.isSupported() && sysTray == null) {
            sysTray = SystemTray.getSystemTray();

            /* fx thread: set up tray digits */
            StackPane trayPane = new StackPane();
            trayPane.setMinWidth(16.0);
            trayPane.setMinHeight(16.0);
            trayPane.setStyle("-fx-background-color: #000000;");
            trayPane.setOpacity(0.8);

            trayDigits = new javafx.scene.control.Label();
            trayDigits.setStyle("-fx-text-fill: #FFFFFF");
            trayDigits.setOpacity(1);

            trayPane.getChildren().addAll(trayDigits);
            trayScene = new Scene(trayPane, null);

            /* awt thread: make tray icon */
            buffTrayIcon = new BufferedImage(16, 16, 2);
            SwingFXUtils.fromFXImage(trayScene.snapshot(wim), buffTrayIcon);

            /* tray popup events */
            listener = e ->
            {
                command = e.getActionCommand().toLowerCase();
                System.out.println(command);
                if (command.equals("pause")) {
                    Platform.runLater(() -> pauseApp());/*fx thread */
                }
                if (command.equals("restart")) {
                    Platform.runLater(() -> restartApp());
                }
                if (command.equals("reset")) {
                    Platform.runLater(() -> {
                        app.setMinWidth(400);
                        app.setMinHeight(200);
                        app.setOpacity(1.0);
                        app.requestFocus();
                    });
                }
                if (command.equals("exit")) {
                    sysTray.remove(trayIcon);/* awt thread */

                    Platform.runLater(() -> {/* fx thread */
                        timeoutStages.forEach(s -> s.getStage().close());
                        app.close();
                    });
                }
            };

            MenuItem pause = new MenuItem("Pause");
            pause.addActionListener(listener);

            MenuItem restart = new MenuItem("Restart");
            restart.addActionListener(listener);

            MenuItem reset = new MenuItem("Reset");
            reset.addActionListener(listener);

            MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(listener);

            /* add tray menu options */
            popup = new PopupMenu();
            popup.add(pause);
            popup.addSeparator();
            popup.add(restart);
            popup.addSeparator();
            popup.add(reset);
            popup.addSeparator();
            popup.add(exit);

            trayIcon = new TrayIcon(buffTrayIcon, "Pomodoro Timer", popup);
            trayIcon.addActionListener(listener);

            try {
                sysTray.add(trayIcon);
            } catch (AWTException except) {
                app.close();
            }
        }
    }
}
