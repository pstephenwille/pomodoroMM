package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
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

    public List<Stage> timeoutStages = new ArrayList<Stage>();

    @Override
    public void start(Stage formStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));


        GridPane userInputs = new GridPane();
        userInputs.setGridLinesVisible(false);

        /* i will work for */
        Label workfor = new Label("I will work for ");

        /* this many minutes*/
        TextField workMinutesText = new TextField("25");
        workMinutesText.setMaxWidth(80);
        workMinutesText.textProperty().addListener((obervable, oldValue, newValue) -> {
            System.out.println("work minutes text changed");

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
        Label minutes = new Label(" minutes");





        /*i will break for */
        Label breakfor = new Label("I will break for ");

        /* this many */
        final TextField breakMinutesText = new TextField("10");
        breakMinutesText.setMaxWidth(80);
        breakMinutesText.textProperty().addListener((obervable, oldValue, newValue) -> {
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
        Label minutes2 = new Label(" minutes");





        /* set opacity to */
        Label setOpacityTo = new Label("Set opacity to ");

        TextField opacityText = new TextField("80");
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
        Label percent = new Label(" %");




        /* row 1*/
        userInputs.add(workfor, 1, 1);
        userInputs.add(workMinutesText, 5, 1);
        userInputs.add(minutes, 6, 1);

        /* row 2*/
        userInputs.add(breakfor, 1, 2);
        userInputs.add(breakMinutesText, 5, 2);
        userInputs.add(minutes2, 6, 2);

        /* row 3 */
        userInputs.add(setOpacityTo, 1, 3);
        userInputs.add(opacityText, 5, 3);
        userInputs.add(percent, 6, 3);


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
            }
        });
        formStage.initStyle(StageStyle.UTILITY);
        formStage.show();
    }


    public void makeBreakScreens() {
        System.out.println("makeBreakScreens()");
        allScreens = Screen.getScreens();
        screenCount = allScreens.size();

        allScreens.forEach(s -> {
            System.out.println("foreach screen");
            timeoutStages.add(setUpBreakStages("id-" + screenCount--, s));
        });

//        makeAppContainer();
    }

    public void makeAppContainer() {
        System.out.println("makeAppContainer()");
        Stage stage = new Stage();

        stage.setMaxWidth(0.0);
        stage.setMaxHeight(0.0);
        stage.setOpacity(0.0);
        stage.initModality(Modality.APPLICATION_MODAL);/* required to allow stage.toFront() */

        stage.show();

//        hideBreakPeriodStages();

        new Timeline(new KeyFrame(
                Duration.millis(250),
                e -> hideBreakPeriodStages())).play();

    }



    /* break period */
    public void showBreakPeriodStages() {
        System.out.println("showBreakPeriodStages");

        /* reset timer */
        timerText = breakForMinutes;

        timeoutStages.forEach(s -> s.show());
        displayTimer.playFromStart();

        Timeline showStages = new Timeline(new KeyFrame(
                Duration.millis(breakForMinutes),
                event -> {
                    System.out.println("calling hide");
                    hideBreakPeriodStages();
                }
        )
        );

        showStages.play();
    }

    /* work period */
    public void hideBreakPeriodStages() {
        System.out.println("hideBreakPeriodStages()");

        timeoutStages.forEach(s -> s.hide());
        displayTimer.pause();

        new Timeline(new KeyFrame(
                Duration.millis(workForMinuts),
                event -> {
                    System.out.println("calling show");
                    showBreakPeriodStages();
                })).play();
    }


    public Stage setUpBreakStages(final String name, final Screen screen) {
        System.out.println("setUpBreakStages");

        final Stage stage = new Stage();

        Rectangle2D bounds = screen.getBounds();

        StackPane layout = new StackPane();

        /* display clock on primary monitor */
        if (screen.hashCode() == Screen.getPrimary().hashCode()) {
            breakTimerLbl.setId("breakTimerLbl");
            layout.getChildren().addAll(breakTimerLbl);
            timerText = breakForMinutes;

            displayTimer = new Timeline(new KeyFrame(
                    Duration.millis(250),
                    event -> {
//                        timerText -= 250L;
                        Long _minutes = TimeUnit.MILLISECONDS.toMinutes(timerText);
                            _minutes %= 60;
                        Long _seconds = TimeUnit.MILLISECONDS.toSeconds(timerText);
                            _seconds %= 60;
                        Long _millis = TimeUnit.MILLISECONDS.toMillis(timerText);
                            _millis %= 1000;

                        String minutesText = _minutes.toString();
                        String secondsText = _seconds.toString();
                        String millisText = _millis.toString();

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
                        if (timerText <= 0) {timerText = workForMinuts;}
                    }));
            displayTimer.setCycleCount(Timeline.INDEFINITE);
//            displayTimer.play();
        }

        layout.setStyle("-fx-background-color: rgba(0, 0, 0," + opacity + ");");


        Scene scene = new Scene(layout,
                bounds.getWidth(),
                bounds.getHeight());
        scene.getStylesheets().add("sample/main.css");
        scene.setFill(null);
        stage.setScene(scene);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.toFront();
        stage.initModality(Modality.WINDOW_MODAL);


        return stage;
    }


}
