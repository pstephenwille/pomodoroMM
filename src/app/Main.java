package app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main extends Application {
    Double opacity = 0.8;
    Label minutes02Lbl = new Label();
    Label setOpacityTo = new Label();
    Label breakTimerLbl = new Label();
    Label willWorkFor = new Label();
    Label instructionTxt;
    Label percentLbl = new Label();
    Label breakfor = new Label();
    Label minutes01Lbl = new Label();
    List<Screen> allScreens;
    List<BreakPeriodStage> timeoutStages = new ArrayList<>();
    Long breakForMinutes = 10L;
    Long workForMinutes = 25L;
    Long timerText;
    TextField breakMinutesText = new TextField();
    TextField opacityText = new TextField();
    TextField workMinutesText = new TextField();
    Timeline displayTimer;
    Timeline breakPeriodTimeline;
    Timeline workPeriodTimeLine;
    TrayIcon trayIcon = null;
    Stage app;
    Stage appContainter = new Stage();
    String minutesText;
    String secondsText;
    String millisText;
    SystemTray sysTray;
    BufferedImage buffTrayIcon;
    Label trayDigits;
    PopupMenu popup;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage app) throws Exception {
        this.app = app;

        makeFormFields();

        GridPane userInputs = new GridPane();

        /* position form fields */
        /* row 1*/
        userInputs.add(willWorkFor, 1, 0);
        userInputs.add(workMinutesText, 5, 0);
        userInputs.add(minutes01Lbl, 6, 0);

        /* row 2*/
        userInputs.add(breakfor, 1, 1);
        userInputs.add(breakMinutesText, 5, 1);
        userInputs.add(minutes02Lbl, 6, 1);

        /* row 3 */
        userInputs.add(setOpacityTo, 1, 2);
        userInputs.add(opacityText, 5, 2);
        userInputs.add(percentLbl, 6, 2);

        /* row 4, instruction text */
        userInputs.add(instructionTxt, 1, 4, 6, 1);


        Scene scene = new Scene(userInputs, 400, 200);
        scene.getStylesheets().add
                (Main.class.getResource("main.css").toExternalForm());

        app.addEventHandler(KeyEvent.KEY_RELEASED, key ->
        {
            String code = key.getCode().toString().toLowerCase();

            if (code.equals("escape") || code.equals("esc")) {
                app.setMaxWidth(0.0);
                app.setMaxHeight(0.0);
                app.setOpacity(0.0);
            }


            if (code.equals("enter")) {
                /* user has submitted the form,
                * set the values and,
                * start the app */
                if (timeoutStages.size() > 0) pauseApp();

                /* if not already milliseconds */
                if (workForMinutes.toString().length() < 4) {
                    workForMinutes *= 60L * 1000L;
                }
                if (breakForMinutes.toString().length() < 4) {
                    breakForMinutes *= 60L * 1000L;
                }
                timerText = workForMinutes;

                if (timeoutStages.size() > 0) {
                    /* reset countdown clock opacity */
                    timeoutStages.forEach(s -> {
                        s.getLayout()
                                .setStyle("-fx-background-color: rgba(0, 0, 0," + opacity + ")");
                    });
                }
                /* leave app container running, to give the stages something to run in. */
                app.setMaxWidth(0.0);
                app.setMaxHeight(0.0);
                app.setOpacity(0.0);

                makeSysTrayIcon();
                makeBreakScreens();
                makeTimers();
                hideBreakPeriodStages();
            }
        });


        app.setTitle("Pomodoro - multi monitor");
        app.setScene(scene);
        app.initStyle(StageStyle.UTILITY);
        app.show();
    }

    public void makeFormFields() {
        ChangeListener parseField = (observable, oldValue, newValue) -> {
            String fieldID = observable.toString().split(" ")[2].split("=")[1].replace(",", "");

            String _digits = newValue.toString().replaceAll("\\D+", "");

            /* limit to 3 digits */
            _digits = (_digits.length() > 3) ? _digits.substring(0, 3) : _digits;

            if (_digits.length() > 0) {
                if (fieldID.equals("workMinutes")) {
                    workForMinutes = Long.parseLong(_digits);
                }
                if (fieldID.equals("breakMinutes")) {
                    breakForMinutes = Long.parseLong(_digits);
                }
                if (fieldID.equals("opacity")) {
                    opacity = Double.parseDouble(_digits) / 100.00;
                }
            } else {
                _digits = "";
            }

            if (fieldID.equals("workMinutes")) {
                workMinutesText.setText(_digits);
            }
            if (fieldID.equals("breakMinutes")) {
                breakMinutesText.setText(_digits);
            }
            if (fieldID.equals("opacity")) {
                opacityText.setText(_digits);
            }
        };

        /* i will work for */
        willWorkFor.setText("I will work for ");

        /* work minutes, input */
        workMinutesText.setText("25");
        workMinutesText.setId("workMinutes");
        workMinutesText.textProperty().addListener(parseField);

        /* minutes */
        minutes01Lbl.setText(" minutes");

        /*i will break for */
        breakfor.setText("I will break for ");

        /* break minutes, input */
        breakMinutesText.setText("10");
        breakMinutesText.setId("breakMinutes");
        breakMinutesText.textProperty().addListener(parseField);

        /* minutes */
        minutes02Lbl.setText(" minutes");

        /* set opacity to */
        setOpacityTo.setText("Set opacity to ");

        /* opacity input */
        opacityText.setText("80");
        opacityText.setId("opacity");
        opacityText.textProperty().addListener(parseField);


        /* % */
        percentLbl.setText(" %");

        instructionTxt = new Label("Pres ESCAPE to exit, press ENTER to start.\nDuring the " +
                "break period, ESCAPE will restart the cycle.");
        instructionTxt.setId("instructionTxt");
    }

    public void updateTrayDigits() {
        /* fs get trayDigits */
        /* update label */
//        SwingFXUtils.fromFXImage(trayScene.snapshot( new WritableImage(16, 16) ), buffTrayIcon);

        /* awt update trayIcon */
//        trayIcon = new TrayIcon(buffTrayIcon, "Pomodoro Timer", popup);

    }
    public void makeSysTrayIcon() {
        if (SystemTray.isSupported() && sysTray == null) {
            sysTray = SystemTray.getSystemTray();

            /* fx thread */
            StackPane trayPane = new StackPane();
            trayPane.setMinWidth(16.0);
            trayPane.setMinHeight(16.0);
            trayPane.setStyle("-fx-background-color: #333333;");

            trayDigits = new Label("00");
            trayDigits.setStyle("-fx-text-fill: #FFFFFF");
            trayDigits.setAlignment(Pos.CENTER);

            trayPane.getChildren().addAll(trayDigits);
            Scene trayScene = new Scene(trayPane, null);

            /* awt thread */
            buffTrayIcon = new BufferedImage(16, 16, 2);
            SwingFXUtils.fromFXImage(trayScene.snapshot( new WritableImage(16, 16) ), buffTrayIcon);


            ActionListener listener = e ->
            {
                String command = e.getActionCommand().toLowerCase();

                if (command.equals("pause")) {
                    Platform.runLater(() -> pauseApp());
                }
                if (command.equals("restart")) {
                    Platform.runLater(() -> hideBreakPeriodStages());/*fx thread */
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
            }
        }
    }

    public void makeBreakScreens() {
        if (timeoutStages.size() == 0) {

            allScreens = Screen.getScreens();
            allScreens.forEach(s -> timeoutStages.add(
                    new BreakPeriodStage(s, opacity, breakTimerLbl)));

            timeoutStages.forEach(s -> {
                s.getStage().getScene().addEventHandler(KeyEvent.KEY_RELEASED, escape -> {
                    String key = escape.getCode().toString().toLowerCase();
                    if (key.equals("escape") || key.equals("esc")) {
                        hideBreakPeriodStages();
                    }
                });
            });
        }
    }

    public void makeTimers() {
        /* show break stages */
        breakPeriodTimeline = new Timeline(new KeyFrame(Duration.millis(breakForMinutes),
                even -> hideBreakPeriodStages()));

        /* hide break stages */
        workPeriodTimeLine = new Timeline(new KeyFrame(Duration.millis(workForMinutes),
                event -> showBreakPeriodStages()));

        /* update count down clock */
        displayTimer = new Timeline(new KeyFrame(
                Duration.millis(250),
                event -> {
                    timerText -= 250L;
                    Long _minutes = TimeUnit.MILLISECONDS.toMinutes(timerText) % 60;
                    Long _seconds = TimeUnit.MILLISECONDS.toSeconds(timerText) % 60;
                    Long _millis = TimeUnit.MILLISECONDS.toMillis(timerText) % 1000;

                    minutesText = _minutes.toString();
                    secondsText = _seconds.toString();
                    millisText = _millis.toString();

                    if (minutesText.length() == 1) {
                        minutesText = "0" + minutesText;
                    }
                    if (secondsText.length() == 1) {
                        secondsText = "0" + secondsText;
                    }
                    if (millisText.length() == 1) {
                        millisText = "0" + millisText;
                    }

                    breakTimerLbl.setText(minutesText + ":" +
                            secondsText + ":" +
                            millisText.substring(0, 2));
                    if (timerText <= 0) {
                        timerText = workForMinutes;
                    }
                }));
        displayTimer.setCycleCount(Timeline.INDEFINITE);
    }

    /* work period */
    public void hideBreakPeriodStages() {
        timeoutStages.forEach(s -> s.getStage().hide());
        displayTimer.pause();

        workPeriodTimeLine.playFromStart();
    }

    /* break period */
    public void showBreakPeriodStages() {
        /* reset timer */
        timerText = breakForMinutes;
        timeoutStages.forEach(s -> s.getStage().show());

        /* gets focus to accept 'escape' key presses */
        Platform.runLater(() -> timeoutStages.get(0).getStage().requestFocus());

        displayTimer.playFromStart();

        breakPeriodTimeline.playFromStart();

        appContainter.toFront();
    }

    public void pauseApp() {
        timeoutStages.forEach(s -> s.getStage().hide());

        breakPeriodTimeline.pause();
        workPeriodTimeLine.pause();
        displayTimer.pause();
    }
}
