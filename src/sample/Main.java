package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public Double opacity = 0.8;
    public Long breakForMinutes = 10L;
    public Long workForMinuts = 25L;
    public List<Screen> allScreens;
    public Integer screenCount;

    public Long timerText = 100L;
    public Label breakTimerLbl = new Label();

    public Timeline displayTimer;

//    public List<Stage> timeoutStages = new ArrayList<>();
    public List<BreakPeriodStage> timeoutStages = new ArrayList<>();
    public Timeline breakPeriodTimeline;
    public Timeline workPeriodTimeLine;

    public Label willWorkFor = new Label();
    public TextField workMinutesText = new TextField();
    public Label minutes01Lbl = new Label();

    public Label breakfor = new Label();
    public TextField breakMinutesText = new TextField();
    public Label minutes02Lbl = new Label();
    public Label setOpacityTo = new Label();
    public TextField opacityText = new TextField();
    public Label percentLbl = new Label();

    @Override
    public void start(Stage formStage) throws Exception {

        GridPane userInputs = new GridPane();
        userInputs.setGridLinesVisible(false);

        makeFormFields();

        /* position form fields */
        /* row 1*/
        userInputs.add(willWorkFor, 1, 1);
        userInputs.add(workMinutesText, 5, 1);
        userInputs.add(minutes01Lbl, 6, 1);

        /* row 2*/
        userInputs.add(breakfor, 1, 2);
        userInputs.add(breakMinutesText, 5, 2);
        userInputs.add(minutes02Lbl, 6, 2);

        /* row 3 */
        userInputs.add(setOpacityTo, 1, 3);
        userInputs.add(opacityText, 5, 3);
        userInputs.add(percentLbl, 6, 3);


        Scene scene = new Scene(userInputs, 500, 500);
        scene.getStylesheets().add
                (Main.class.getResource("main.css").toExternalForm());


        formStage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, event ->
        {
            System.out.println(event);
            if (event.getCode().toString() == "ESCAPE" || event.getCode().toString() == "ENTER") {
                workForMinuts *= 60L * 1000L;
                breakForMinutes *= 60L * 1000L;
                timerText = breakForMinutes;

                formStage.close();

                makeBreakScreens();
                makeAppContainer();
                makeTimers();
            }
        });
        formStage.initStyle(StageStyle.UTILITY);
        formStage.show();
    }

    public void makeFormFields() {
        /* i will work for */
        willWorkFor.setText("I will work for ");

        /* this many minutes, input */
        workMinutesText.setText("25");
        workMinutesText.setMaxWidth(80);
        workMinutesText.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("work minutes01Lbl text changed");

            String onlyDigits = newValue.replaceAll("\\D+", "");

            onlyDigits = (onlyDigits.length() > 3) ? onlyDigits.substring(0, 3) : onlyDigits;
            System.out.println(onlyDigits);

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
        breakMinutesText.setMaxWidth(80);
        breakMinutesText.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("break minutes text changed");

            String onlyDigits = newValue.replaceAll("\\D+", "");
            onlyDigits = (onlyDigits.length() > 3) ? onlyDigits.substring(0, 3) : onlyDigits;
            int _length = onlyDigits.length();

            if (_length > 0 && _length < 3) {
                breakForMinutes = Long.parseLong(onlyDigits);
                System.out.println(breakForMinutes);
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
        opacityText.setMaxWidth(80);
        opacityText.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("opacityText field");

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
    }

    public void makeBreakScreens() {
        System.out.println("makeBreakScreens()");
        allScreens = Screen.getScreens();
        screenCount = allScreens.size();

        allScreens.forEach(s -> {
            System.out.println("foreach screen");
            timeoutStages.add(new BreakPeriodStage("id-" + screenCount--, s, opacity, breakTimerLbl));
        });
        }

    public void makeAppContainer() {
        System.out.println("makeAppContainer()");
        Stage stage = new Stage();

        stage.setMaxWidth(0.0);
        stage.setMaxHeight(0.0);
        stage.setOpacity(0.0);
        stage.initModality(Modality.APPLICATION_MODAL);/* required to allow stage.toFront() */

        stage.show();

        /* start app */
        new Timeline(new KeyFrame(
                Duration.millis(250),
                e -> hideBreakPeriodStages())).play();
    }



    /* break period */
    public void showBreakPeriodStages() {
        System.out.println("showBreakPeriodStages");

        /* reset timer */
        timerText = breakForMinutes;
        timeoutStages.forEach(s -> s.getStage().show());
        displayTimer.playFromStart();

        breakPeriodTimeline.playFromStart();
    }

    /* work period */
    public void hideBreakPeriodStages() {
        System.out.println("hideBreakPeriodStages()");

        timeoutStages.forEach(s -> s.getStage().hide());
        displayTimer.pause();

        workPeriodTimeLine.play();
    }



    public void makeTimers() {

        /* show break stages */
        breakPeriodTimeline = new Timeline(new KeyFrame(Duration.millis(breakForMinutes),
            even-> {
                System.out.println("calling hide");
                hideBreakPeriodStages();
            }));

        /* hide break stages */
        workPeriodTimeLine =  new Timeline(new KeyFrame(Duration.millis(workForMinuts),
            event -> {
                System.out.println("calling show");
                showBreakPeriodStages();
            }));

        /* update count down clock */
        displayTimer = new Timeline(new KeyFrame(
                Duration.millis(250),
                event -> {
                    timerText -= 250L;
                    Long _minutes = TimeUnit.MILLISECONDS.toMinutes(timerText);
                        _minutes %= 60;
                    Long _seconds = TimeUnit.MILLISECONDS.toSeconds(timerText);
                        _seconds %= 60;
                    Long _millis = TimeUnit.MILLISECONDS.toMillis(timerText);
                        _millis %= 1000;

                    String minutesText = _minutes.toString();
                    String secondsText = _seconds.toString();
                    String millisText = _millis.toString();

                    if (minutesText.length() == 1) {minutesText = "0" + minutesText;}
                    if (secondsText.length() == 1) {secondsText = "0" + secondsText;}
                    if (millisText.length() == 1) {millisText = "0" + millisText;}

                    breakTimerLbl.setText(minutesText + ":" +
                            secondsText + ":" +
                            millisText.substring(0, 2));
                    if (timerText <= 0) {timerText = workForMinuts;}
                }));
        displayTimer.setCycleCount(Timeline.INDEFINITE);
    }
}
