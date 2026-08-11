package vue;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class VueSimulation extends Application {

    @Override
    public void start(Stage stage) {

        Rectangle2D ecran = Screen.getPrimary().getVisualBounds();

        double largeur = ecran.getWidth();
        double hauteur = ecran.getHeight();

        Pane root = new Pane();

        Scene scene = new Scene(root, largeur, hauteur);

        stage.setScene(scene);
        stage.setTitle("Simulation épidémique");

        stage.setX(ecran.getMinX());
        stage.setY(ecran.getMinY());
        stage.setWidth(largeur);
        stage.setHeight(hauteur);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}