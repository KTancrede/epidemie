package vue;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import modele.EtatSante;
import modele.Individu;
import modele.Population;
import modele.Simulation;
import parametres.IParametresPopulation;
import parametres.TestParamMal;
import parametres.TestParamPop;

public class VueSimulation extends Application {

    // ==========================================================
    // MODELE
    // ==========================================================

    private Simulation sim;
    private Population pop;

    // ==========================================================
    // SIMULATION
    // ==========================================================

    private int jourActuel = 0;
    private boolean patientZeroCree = false;

    private Timeline timelineSimulation;

    // ==========================================================
    // ELEMENTS GRAPHIQUES
    // ==========================================================

    private final Map<Individu, Circle> pointsIndividus =
            new HashMap<>();

    private final Map<Individu, Tooltip> tooltipsIndividus =
            new HashMap<>();

    private Label labelJour;

    private TableView<Individu> tablePopulation;


    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        // ======================================================
        // PANNEAU GAUCHE
        // ======================================================

        VBox panneauGauche = new VBox(12);

        panneauGauche.prefWidthProperty()
                .bind(root.widthProperty().multiply(0.28));

        panneauGauche.setStyle(
                "-fx-border-color: transparent gray transparent transparent;" +
                "-fx-border-width: 0 1 0 0;" +
                "-fx-padding: 20;"
        );

        // ======================================================
        // PANNEAU DROIT
        // ======================================================

        VBox panneauDroit = new VBox(10);

        Pane zoneCentrale = new Pane();

        HBox zoneInformations = new HBox();

        zoneCentrale.setStyle(
                "-fx-background-color: #EEEEEE;"
        );

        zoneInformations.setStyle(
                "-fx-background-color: #f4f4f4;" +
                "-fx-border-color: #999999;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
        );

        zoneCentrale.prefHeightProperty()
                .bind(
                        panneauDroit
                                .heightProperty()
                                .multiply(0.80)
                );

        zoneInformations.prefHeightProperty()
                .bind(
                        panneauDroit
                                .heightProperty()
                                .multiply(0.20)
                );

        VBox.setMargin(
                zoneInformations,
                new Insets(10)
        );

        panneauDroit.getChildren().addAll(
                zoneCentrale,
                zoneInformations
        );

        // ======================================================
        // CONTROLES SIMULATION
        // ======================================================

        VBox zoneControleSimulation =
                creerControleSimulation();

        zoneControleSimulation.prefWidthProperty()
                .bind(
                        zoneInformations
                                .widthProperty()
                                .multiply(0.15)
                );

        Pane zoneStatistiques = new Pane();

        HBox.setHgrow(
                zoneStatistiques,
                Priority.ALWAYS
        );

        zoneInformations.getChildren().addAll(
                zoneControleSimulation,
                zoneStatistiques
        );

        // ======================================================
        // PARAMETRES POPULATION
        // ======================================================

        Label titrePopulation =
                new Label("PARAMÈTRES POPULATION");

        Label labelTaille =
                new Label("Taille de la population");

        TextField champTaille =
                new TextField();

        Button boutonGenererPopulation =
                new Button("Générer la population");

        champTaille.setOnAction(event ->
                boutonGenererPopulation.fire()
        );

        Separator separateur1 =
                new Separator();

        // ======================================================
        // LIENS
        // ======================================================

        Label titreReseau =
                new Label("Réseau de contacts");

        Button boutonGenererLiens =
                new Button("Générer les liens");

        boutonGenererLiens.setDisable(true);

        Separator separateur2 =
                new Separator();

        // ======================================================
        // STATUT
        // ======================================================

        Label statutPopulation =
                new Label("Population : non générée");

        Label statutLiens =
                new Label("Liens générés : non");

        Separator separateur3 =
                new Separator();

        // ======================================================
        // TABLEAU
        // ======================================================

        this.tablePopulation =
                creerTablePopulation();

        VBox.setVgrow(
                tablePopulation,
                Priority.ALWAYS
        );

        // ======================================================
        // GENERER POPULATION
        // ======================================================

        boutonGenererPopulation.setOnAction(event -> {

            try {

                int taille =
                        Integer.parseInt(
                                champTaille.getText()
                        );

                if (taille <= 0) {

                    statutPopulation.setText(
                            "Taille invalide"
                    );

                    return;
                }

                // Arrêt d'une éventuelle simulation en cours
                if (timelineSimulation != null) {
                    timelineSimulation.stop();
                }

                Individu.resetCompteurID();

                IParametresPopulation paramPop =
                        new TestParamPop(taille);

                // Création de la vraie simulation
                this.sim =
                        new Simulation(
                                paramPop,
                                new TestParamMal(),
                                0
                        );

                // On récupère sa population
                this.pop =
                        sim.getPop();

                // Remise à zéro de la simulation
                this.jourActuel = 0;
                this.patientZeroCree = false;

                labelJour.setText("Jour 0");

                statutPopulation.setText(
                        "Population : "
                        + taille
                        + " individus"
                );

                statutLiens.setText(
                        "Liens générés : non"
                );

                boutonGenererLiens.setDisable(false);

                // Mise à jour du tableau
                tablePopulation.setItems(
                        FXCollections.observableArrayList(
                                pop.getIndividus()
                        )
                );

                // Affichage graphique
                afficherPopulation(
                        zoneCentrale
                );

            } catch (NumberFormatException e) {

                statutPopulation.setText(
                        "Veuillez entrer un nombre entier"
                );
            }
        });

        // ======================================================
        // GENERER LIENS
        // ======================================================

        boutonGenererLiens.setOnAction(event -> {

            if (this.pop == null) {
                return;
            }

            this.pop.creationLiens();

            afficherLiens(
                    zoneCentrale
            );

            mettreAJourTooltips();

            tablePopulation.refresh();

            statutLiens.setText(
                    "Liens générés : oui"
            );

            boutonGenererLiens.setDisable(true);
        });

        // ======================================================
        // PANNEAU GAUCHE
        // ======================================================

        panneauGauche.getChildren().addAll(
                titrePopulation,
                labelTaille,
                champTaille,
                boutonGenererPopulation,
                separateur1,
                titreReseau,
                boutonGenererLiens,
                separateur2,
                statutPopulation,
                statutLiens,
                separateur3,
                tablePopulation
        );

        root.setLeft(
                panneauGauche
        );

        root.setCenter(
                panneauDroit
        );

        // ======================================================
        // FENETRE
        // ======================================================

        Scene scene =
                new Scene(root);

        stage.setScene(scene);

        stage.setTitle(
                "Simulation épidémique"
        );

        stage.show();

        stage.setMaximized(true);
    }


    // ==========================================================
    // CONTROLE SIMULATION
    // ==========================================================

    private VBox creerControleSimulation() {

        VBox zoneControleSimulation =
                new VBox(10);

        this.labelJour =
                new Label("Jour 0");

        Button boutonPlay =
                new Button("▶");

        Button boutonPause =
                new Button("⏸");

        TextField champVitesse =
                new TextField();

        champVitesse.setPromptText(
                "Jours / seconde"
        );

        zoneControleSimulation.setAlignment(
                Pos.CENTER
        );

        zoneControleSimulation.setStyle(
                "-fx-border-color: transparent #999999 transparent transparent;" +
                "-fx-border-width: 0 1 0 0;" +
                "-fx-padding: 10;"
        );

        // ======================================================
        // PLAY
        // ======================================================

        boutonPlay.setOnAction(event -> {

            if (sim == null) {
                return;
            }

            /*
             * Si le patient zéro n'existe pas encore,
             * on le crée.
             */
            creerPatientZeroSiNecessaire();

            /*
             * Champ vide :
             *
             * un clic sur Play = exactement un jour.
             */
            if (champVitesse.getText().isBlank()) {

                avancerUnJour();

                return;
            }

            try {

                double vitesse =
                        Double.parseDouble(
                                champVitesse.getText()
                        );

                if (vitesse <= 0) {
                    return;
                }

                /*
                 * On arrête l'ancienne Timeline
                 * s'il y en avait déjà une.
                 */
                if (timelineSimulation != null) {

                    timelineSimulation.stop();
                }

                /*
                 * Exemple :
                 *
                 * 1 jour/sec  -> 1000 ms
                 * 2 jours/sec -> 500 ms
                 * 10 jours/sec -> 100 ms
                 */
                double intervalleMillis =
                        1000.0 / vitesse;

                timelineSimulation =
                        new Timeline(
                                new KeyFrame(
                                        Duration.millis(
                                                intervalleMillis
                                        ),

                                        e -> avancerUnJour()
                                )
                        );

                timelineSimulation.setCycleCount(
                        Timeline.INDEFINITE
                );

                timelineSimulation.play();

            } catch (NumberFormatException e) {

                champVitesse.clear();
            }
        });

        // ======================================================
        // PAUSE
        // ======================================================

        boutonPause.setOnAction(event -> {

            if (timelineSimulation != null) {

                timelineSimulation.pause();
            }
        });

        zoneControleSimulation.getChildren().addAll(
                labelJour,
                boutonPlay,
                champVitesse,
                boutonPause
        );

        return zoneControleSimulation;
    }


    // ==========================================================
    // PATIENT ZERO
    // ==========================================================

    private void creerPatientZeroSiNecessaire() {

        if (sim == null) {
            return;
        }

        if (!patientZeroCree) {

            sim.patientZero();

            patientZeroCree = true;

            /*
             * Le patient zéro est en INCUBATION.
             * On actualise donc immédiatement l'affichage.
             */

            mettreAJourCouleurs();

            mettreAJourTooltips();

            tablePopulation.refresh();
        }
    }


    // ==========================================================
    // AVANCER D'UN JOUR
    // ==========================================================

    private void avancerUnJour() {

        if (sim == null) {
            return;
        }

        /*
         * Sécurité :
         * si jamais cette méthode est appelée directement,
         * on crée quand même le patient zéro.
         */
        creerPatientZeroSiNecessaire();

        jourActuel++;

        /*
         * ICI on appelle réellement ta méthode Simulation.jPlus1()
         */
        sim.jPlus1(
                jourActuel
        );

        labelJour.setText(
                "Jour " + jourActuel
        );

        /*
         * Mise à jour de toute l'interface
         */
        mettreAJourCouleurs();

        mettreAJourTooltips();

        tablePopulation.refresh();
    }


    // ==========================================================
    // TABLEAU
    // ==========================================================

    private TableView<Individu> creerTablePopulation() {

        TableView<Individu> table =
                new TableView<>();

        TableColumn<Individu, Integer> colonneId =
                new TableColumn<>("ID");

        colonneId.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getId()
                )
        );

        TableColumn<Individu, Integer> colonneAge =
                new TableColumn<>("Âge");

        colonneAge.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getAge()
                )
        );

        TableColumn<Individu, String> colonneSexe =
                new TableColumn<>("Sexe");

        colonneSexe.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue()
                                .getSexe()
                                .toString()
                )
        );

        TableColumn<Individu, String> colonneEtat =
                new TableColumn<>("État");

        colonneEtat.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue()
                                .getEtat()
                                .toString()
                )
        );

        TableColumn<Individu, Boolean> colonneDiab =
                new TableColumn<>("Diab.");

        colonneDiab.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue()
                                .isDiabetique()
                )
        );

        TableColumn<Individu, Integer> colonneContacts =
                new TableColumn<>("Contacts");

        colonneContacts.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue()
                                .getTailleListeContacts()
                )
        );

        table.getColumns().addAll(
                colonneId,
                colonneAge,
                colonneSexe,
                colonneEtat,
                colonneDiab,
                colonneContacts
        );

        return table;
    }


    // ==========================================================
    // AFFICHAGE POPULATION
    // ==========================================================

    private void afficherPopulation(
            Pane zoneCentrale
    ) {

        zoneCentrale
                .getChildren()
                .clear();

        pointsIndividus.clear();

        tooltipsIndividus.clear();

        int nbIndividus =
                pop.getIndividus().size();

        double largeur =
                zoneCentrale.getWidth();

        double hauteur =
                zoneCentrale.getHeight();

        double centreX =
                largeur / 2.0;

        double centreY =
                hauteur / 2.0;

        double rayonMax =
                Math.min(
                        largeur,
                        hauteur
                ) * 0.42;

        double rayonPoint = 4.0;

        double espacement =
                rayonPoint * 2.5;

        int indexIndividu = 0;

        for (
                double rayon = rayonMax;

                rayon > 20
                && indexIndividu < nbIndividus;

                rayon -= espacement
        ) {

            double circonference =
                    2
                    * Math.PI
                    * rayon;

            int capaciteAnneau =
                    (int) (
                            circonference
                            / espacement
                    );

            int nbSurAnneau =
                    Math.min(
                            capaciteAnneau,

                            nbIndividus
                            - indexIndividu
                    );

            for (
                    int i = 0;

                    i < nbSurAnneau;

                    i++
            ) {

                Individu individu =
                        pop.getIndividus()
                                .get(
                                        indexIndividu
                                );

                double angle =
                        2
                        * Math.PI
                        * i
                        / nbSurAnneau;

                double x =
                        centreX
                        + rayon
                        * Math.cos(angle);

                double y =
                        centreY
                        + rayon
                        * Math.sin(angle);

                Circle point =
                        new Circle(
                                x,
                                y,
                                rayonPoint
                        );

                point.setFill(
                        couleurEtat(
                                individu.getEtat()
                        )
                );

                // ================= TOOLTIP =================

                Tooltip tooltip =
                        new Tooltip(
                                texteTooltip(
                                        individu
                                )
                        );

                tooltip.setShowDelay(
                        Duration.ZERO
                );

                Tooltip.install(
                        point,
                        tooltip
                );

                tooltipsIndividus.put(
                        individu,
                        tooltip
                );

                zoneCentrale
                        .getChildren()
                        .add(point);

                pointsIndividus.put(
                        individu,
                        point
                );

                indexIndividu++;
            }
        }
    }


    // ==========================================================
    // AFFICHAGE LIENS
    // ==========================================================

    private void afficherLiens(
            Pane zoneCentrale
    ) {

        for (Individu individu :
                pop.getIndividus()) {

            Circle pointA =
                    pointsIndividus.get(
                            individu
                    );

            for (Individu contact :
                    individu.getContacts()) {

                /*
                 * Empêche de dessiner :
                 *
                 * A -> B
                 * puis
                 * B -> A
                 */

                if (
                        individu.getId()
                        < contact.getId()
                ) {

                    Circle pointB =
                            pointsIndividus.get(
                                    contact
                            );

                    Line ligne =
                            new Line(
                                    pointA.getCenterX(),
                                    pointA.getCenterY(),
                                    pointB.getCenterX(),
                                    pointB.getCenterY()
                            );

                    ligne.setStroke(
                            Color.rgb(
                                    80,
                                    80,
                                    80,
                                    0.35
                            )
                    );

                    ligne.setStrokeWidth(
                            0.6
                    );

                    /*
                     * Les lignes ne gênent pas
                     * le survol des points.
                     */
                    ligne.setMouseTransparent(
                            true
                    );

                    /*
                     * Index 0 :
                     * ligne derrière les points.
                     */
                    zoneCentrale
                            .getChildren()
                            .add(
                                    0,
                                    ligne
                            );
                }
            }
        }
    }


    // ==========================================================
    // COULEURS ETATS
    // ==========================================================

    private Color couleurEtat(
            EtatSante etat
    ) {

        return switch (etat) {

            case SAIN ->
                    Color.GREEN;

            case INCUBATION ->
                    Color.ORANGE;

            case MALADE ->
                    Color.RED;

            case GUERI ->
                    Color.DODGERBLUE;

            case MORT ->
                    Color.DARKGRAY;
        };
    }


    // ==========================================================
    // MISE A JOUR COULEURS
    // ==========================================================

    private void mettreAJourCouleurs() {

        if (pop == null) {
            return;
        }

        for (Individu individu :
                pop.getIndividus()) {

            Circle point =
                    pointsIndividus.get(
                            individu
                    );

            if (point != null) {

                point.setFill(
                        couleurEtat(
                                individu.getEtat()
                        )
                );
            }
        }
    }


    // ==========================================================
    // TOOLTIP
    // ==========================================================

    private String texteTooltip(
            Individu individu
    ) {

        return "Individu "
                + individu.getId()

                + "\nÂge : "
                + individu.getAge()

                + "\nSexe : "
                + individu.getSexe()

                + "\nÉtat : "
                + individu.getEtat()

                + "\nDiabétique : "
                + (
                        individu.isDiabetique()
                        ? "Oui"
                        : "Non"
                )

                + "\nContacts : "
                + individu
                        .getTailleListeContacts();
    }


    // ==========================================================
    // MISE A JOUR TOOLTIPS
    // ==========================================================

    private void mettreAJourTooltips() {

        if (pop == null) {
            return;
        }

        for (Individu individu :
                pop.getIndividus()) {

            Tooltip tooltip =
                    tooltipsIndividus.get(
                            individu
                    );

            if (tooltip != null) {

                tooltip.setText(
                        texteTooltip(
                                individu
                        )
                );
            }
        }
    }


    // ==========================================================
    // MAIN
    // ==========================================================

    public static void main(String[] args) {

        launch(args);
    }
}