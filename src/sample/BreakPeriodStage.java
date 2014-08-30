package sample;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by stephen on 8/24/2014.
 */
public class BreakPeriodStage extends Stage {

    Stage stage;



    public BreakPeriodStage(final String name, final Screen screen, Double opacity,
                            Label breakTimerLbl, EventHandler hideStagesEvent) {
        System.out.println("BreakPeriodStage.class");


        setStage(new Stage());

        StackPane layout = new StackPane();

        Rectangle2D bounds = screen.getBounds();

        /* put countdown clock on primary monitor */
        if (screen.hashCode() == Screen.getPrimary().hashCode()) {
            breakTimerLbl.setId("breakTimerLbl");
            layout.getChildren().addAll(breakTimerLbl);
        }

        layout.setStyle("-fx-background-color: rgba(0, 0, 0," + opacity + ");");


        Scene scene = new Scene(layout,
                bounds.getWidth(),
                bounds.getHeight());

        scene.getStylesheets().add("sample/main.css");
        scene.setFill(null);

        scene.addEventHandler(KeyEvent.KEY_RELEASED, hideStagesEvent);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.toFront();
        stage.initModality(Modality.WINDOW_MODAL);
    }



    public Stage getStage() {return this.stage;}
    public void setStage(Stage stage) {this.stage = stage;}
}