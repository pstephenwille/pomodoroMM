package app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    Double opacity = 0.8;
    Long breakForMinutes = 10L;
    Long workForMinuts = 25L;
    List<Screen> allScreens;
    Stage appContainter = new Stage();
    Long timerText = 100L;
    Label breakTimerLbl = new Label();
    Timeline displayTimer;
    List<BreakPeriodStage> timeoutStages = new ArrayList<>();
    Timeline breakPeriodTimeline;
    Timeline workPeriodTimeLine;
    Label willWorkFor = new Label();
    TextField workMinutesText = new TextField();
    Label minutes01Lbl = new Label();
    Label breakfor = new Label();
    TextField breakMinutesText = new TextField();
    Label minutes02Lbl = new Label();
    Label setOpacityTo = new Label();
    TextField opacityText = new TextField();
    Label percentLbl = new Label();
    SystemTray sysTray;
    TrayIcon trayIcon = null;
    Label instructionTxt;
    Stage app;
    String minutesText;
    String secondsText;
    String millisText;

    @Override
    public void start(Stage app) throws Exception {
        this.app = app;

        makeFormFields();

        GridPane userInputs = new GridPane();
//        userInputs.setGridLinesVisible(true);

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

        scene.addEventHandler(KeyEvent.KEY_RELEASED, key ->
        {
            String code = key.getCode().toString().toLowerCase();

            if (code.equals("escape") || code.equals("esc")) {
                app.close();
            }
            if (code.equals("enter")) {
                /* user has submitted the form,
                * set the values and,
                * start the app */
                workForMinuts *= 60L * 1000L;
                breakForMinutes *= 60L * 1000L;
                timerText = breakForMinutes;

                /* leave app container running, to give the stages someting to run in. */
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
        /* i will work for */
        willWorkFor.setText("I will work for ");

        /* work minutes, input */
        workMinutesText.setText("25");
        workMinutesText.textProperty().addListener((observable, oldValue, newValue) -> {
            String onlyDigits = newValue.replaceAll("\\D+", "");

            onlyDigits = (onlyDigits.length() > 3) ? onlyDigits.substring(0, 3) : onlyDigits;

            int _length = onlyDigits.length();

            if (_length > 0 && _length < 4) {
                workForMinuts = Long.parseLong(onlyDigits);
            } else {
                onlyDigits = "0";
            }

            workMinutesText.setText(onlyDigits);
        });

        /* minutes */
        minutes01Lbl.setText(" minutes");

        /*i will break for */
        breakfor.setText("I will break for ");

        /* break minutes, input */
        breakMinutesText.setText("10");
        breakMinutesText.textProperty().addListener((observable, oldValue, newValue) -> {
            String onlyDigits = newValue.replaceAll("\\D+", "");

            onlyDigits = (onlyDigits.length() > 3) ? onlyDigits.substring(0, 3) : onlyDigits;

            int _length = onlyDigits.length();

            if (_length > 0 && _length < 3) {
                breakForMinutes = Long.parseLong(onlyDigits);
            } else {
                onlyDigits = "0";
            }

            breakMinutesText.setText(onlyDigits);
        });
        /* minutes */
        minutes02Lbl.setText(" minutes");

        /* set opacity to */
        setOpacityTo.setText("Set opacity to ");

        /* opacity input */
        opacityText.setText("80");
        opacityText.textProperty().addListener((observable, oldValue, newValue) -> {
            String _digits = newValue.replaceAll("\\D+", "");

            int _length = _digits.length();

            if (_length > 0 && _length < 4) {
                opacity = Double.parseDouble(_digits) / 100.00;
            } else {
                _digits = "0";
            }
            opacityText.setText(_digits);
        });

        /* % */
        percentLbl.setText(" %");

        instructionTxt = new Label("Pres ESCAPE to exit, press ENTER to start.\nDuring the " +
                "break period, ESCAPE will restart the cycle.");
        instructionTxt.setId("instructionTxt");
    }

    public void makeSysTrayIcon() {
        if (SystemTray.isSupported()) {
            sysTray = SystemTray.getSystemTray();
            URL imageUrl = Main.class.getResource("javaIcon.jpg");
            Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);


            ActionListener listener = e ->
            {
                String command = e.getActionCommand().toLowerCase();

                if (command.equals("restart")) {
                    Platform.runLater(() -> hideBreakPeriodStages());/*fx thread */
                }
                if (command.equals("exit")) {
                    sysTray.remove(trayIcon);/* awt thread */

                    Platform.runLater(() -> {/* fx thread */
                        timeoutStages.forEach(s -> s.getStage().close());
                        app.close();
                    });
                }
            };

            MenuItem restart = new MenuItem("Restart");
                restart.addActionListener(listener);

            MenuItem exit = new MenuItem("Exit");
                exit.addActionListener(listener);

            PopupMenu popup = new PopupMenu();
                popup.add(restart);
                popup.addSeparator();
                popup.add(exit);

            trayIcon = new TrayIcon(image, "Pomodoro Timer", popup);
            trayIcon.addActionListener(listener);

            try {
                sysTray.add(trayIcon);
            } catch (AWTException except) {
            }
        }
    }

    public void makeBreakScreens() {
        allScreens = Screen.getScreens();
        allScreens.forEach(s -> timeoutStages.add(
                new BreakPeriodStage(s, opacity, breakTimerLbl)) );

        timeoutStages.forEach(s->{
            s.getStage().getScene().addEventHandler(KeyEvent.KEY_RELEASED,  escape -> {
                String key = escape.getCode().toString().toLowerCase();
                if (key.equals("escape") || key.equals("esc")) {
                    hideBreakPeriodStages();
                }
            });
        });
    }

    public void makeTimers() {

        /* show break stages */
        breakPeriodTimeline = new Timeline(new KeyFrame(Duration.millis(breakForMinutes),
                even -> hideBreakPeriodStages()));

        /* hide break stages */
        workPeriodTimeLine = new Timeline(new KeyFrame(Duration.millis(workForMinuts),
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
                        timerText = workForMinuts;
                    }
                }));
        displayTimer.setCycleCount(Timeline.INDEFINITE);
    }

    /* break period */
    public void showBreakPeriodStages() {
        /* reset timer */
        timerText = breakForMinutes;
        timeoutStages.forEach( s ->s.getStage().show());

        /* gets focus to accept 'escape' key presses */
        Platform.runLater( ()->timeoutStages.get(0).getStage().requestFocus() );

        displayTimer.playFromStart();

        breakPeriodTimeline.playFromStart();

        appContainter.toFront();
    }

    /* work period */
    public void hideBreakPeriodStages() {
        timeoutStages.forEach(s -> s.getStage().hide());
        displayTimer.pause();

        workPeriodTimeLine.play();
    }

}
