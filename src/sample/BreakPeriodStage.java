////package sample;
//
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.geometry.Rectangle2D;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Modality;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import javafx.util.Duration;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by stephen on 8/24/2014.
// */
//public class BreakPeriodStage extends Stage{
//    private Double opacity = 8.0;
//    private static Stage stage;
//    private static Screen screen;
//public Label breakTimerLbl;
//    public Timeline displayTimer;
//    public Label timerText;
//
//    public  BreakPeriodStage(final String name, final Stage stage, final Screen screen) {
//
//        final Stage stage = new Stage();
//
//        Rectangle2D bounds = screen.getBounds();
//
//        StackPane layout = new StackPane();
//
//        /* display clock on primary monitor */
//        if (screen.hashCode() == Screen.getPrimary().hashCode()) {
//            breakTimerLbl.setId("breakTimerLbl");
//            layout.getChildren().addAll(breakTimerLbl);
//
//            displayTimer = new Timeline(new KeyFrame(
//                    Duration.millis(250),
//                    event -> {
//                        timerText -= 250L;
//                        Long _minutes = TimeUnit.MILLISECONDS.toMinutes(timerText);
//                        _minutes %= 60;
//                        Long _seconds = TimeUnit.MILLISECONDS.toSeconds(timerText);
//                        _seconds %= 60;
//                        Long _millis = TimeUnit.MILLISECONDS.toMillis(timerText);
//                        _millis %= 1000;
//
//                        String minutesText = _minutes.toString();
//                        String secondsText = _seconds.toString();
//                        String millisText = _millis.toString();
//
//                        if (minutesText.length() == 1) {
//                            minutesText = "0" + minutesText;
//                        }
//                        if (secondsText.length() == 1) {
//                            secondsText = "0" + secondsText;
//                        }
//                        if (millisText.length() == 1) {
//                            millisText = "0" + millisText;
//                        }
//
//                        breakTimerLbl.setText(minutesText + ":" +
//                                secondsText + ":" +
//                                millisText.substring(0, 2));
//                        if (timerText <= 0) {timerText = workForMinuts;}
//                    }));
//            displayTimer.setCycleCount(Timeline.INDEFINITE);
////            displayTimer.play();
//        }
//
//        layout.setStyle("-fx-background-color: rgba(0, 0, 0," + opacity + ");");
//
//
//        Scene scene = new Scene(layout,
//                bounds.getWidth(),
//                bounds.getHeight());
//        scene.getStylesheets().add("sample/main.css");
//        scene.setFill(null);
//        stage.setScene(scene);
//
//        stage.initStyle(StageStyle.UNDECORATED);
//        stage.initStyle(StageStyle.TRANSPARENT);
//
//        stage.setX(bounds.getMinX());
//        stage.setY(bounds.getMinY());
//        stage.toFront();
//        stage.initModality(Modality.WINDOW_MODAL);
//
//
//        return stage;
//
//    }
//
//    /* to set opacity from Main */
////    public Double getOpacity() {return opacity;}
////    public void setOpacity(Double opacity) {this.opacity = opacity;}
//
//    /* to call stage.show() from Main */
//    public Stage getStage() {return stage;}
//    public void setStage(Stage stage) {this.stage = stage;}
//
//
//    public Screen getScreen() {return screen;}
//    public void setScreen(Screen s) {screen = s;}
//}
