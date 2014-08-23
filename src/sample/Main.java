package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.List;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        List<Screen> allScreens = Screen.getScreens();
        int screenCount = allScreens.size();

        for(Screen s : allScreens){
            setAndShowScreens("Screen::" + screenCount--, new Stage(), s);
        }


    }

    public void setAndShowScreens(final String name, final Stage stage, final Screen screen) {
        final Label label = new Label("Welcome");
        label.setStyle("-fx-text-fill: rgba(255, 255, 255, 1); -fx-font-size: 44");

//        StackPane clockContainer = new StackPane();
//        clockContainer.getChildren().add(label);
//        clockContainer.setStyle("-fx-background-color: rgba(0, 0, 255, 1);");
//        clockContainer.setOpacity(0);
//        Text sceneTitle = new Text("Welcome");
//            sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
//            sceneTitle.setStroke(Color.web("#FFFFFF"));
//            sceneTitle.setFill(Color.web("#FFFFFF"));
//            sceneTitle.setOpacity(100);


//        GridPane gridPane = new GridPane();

        Rectangle2D bounds = screen.getBounds();




//        clockContainer.setMaxWidth(bounds.getWidth()/3);
//        clockContainer.setMaxHeight(bounds.getHeight()/3);

        final StackPane layout = new StackPane();
        layout.getChildren().addAll(label);
        layout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");


        Scene scene = new Scene(layout  ,
                        bounds.getWidth(),
                        bounds.getHeight());
        stage.setScene(scene);
        scene.setFill(null);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
//        stage.setOpacity(0.5);


        /* top left of each display */
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

        stage.show();

        /* remove windows */
        new Timeline(new KeyFrame(
                Duration.millis(2500),
                ae -> stage.close()))
                .play();
    }

}
