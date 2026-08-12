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
import parametres.ParametresMaladie;
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
    private boolean simulationTerminee = false;

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

    private Label labelSains;
    private Label labelIncubation;
    private Label labelMalades;
    private Label labelGueris;
    private Label labelMorts;

    // ==========================================================
    // PARAMETRES MALADIE
    // ==========================================================

    private TextField champTransmission;
    private TextField champGuerison;
    private TextField champIncubation;
    private TextField champContagion;
    private TextField champReinfection;
    private TextField champRisqueDiabete;

    private VBox zoneParametresMaladie;


    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        // ======================================================
        // PANNEAU GAUCHE
        // ======================================================

        VBox panneauGauche = new VBox(12);

        panneauGauche.prefWidthProperty()
                .bind(
                        root.widthProperty()
                                .multiply(0.28)
                );

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

        this.zoneParametresMaladie =
                creerZoneParametresMaladie();

        HBox.setHgrow(
                zoneParametresMaladie,
                Priority.ALWAYS
        );

        zoneInformations.getChildren().addAll(
                zoneControleSimulation,
                zoneParametresMaladie
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

        Button boutonGenererLiens =
                new Button("Générer les liens");

        boutonGenererLiens.setDisable(true);

        champTaille.setOnAction(
                event ->
                        boutonGenererPopulation.fire()
        );

        HBox ligneBoutons =
                new HBox(10);

        ligneBoutons.getChildren().addAll(
                boutonGenererPopulation,
                boutonGenererLiens
        );

        Separator separateur1 =
                new Separator();

        // ======================================================
        // STATISTIQUES
        // ======================================================

        labelSains =
                new Label("Sains : 0");

        labelIncubation =
                new Label("Incubation : 0");

        labelMalades =
                new Label("Malades : 0");

        labelGueris =
                new Label("Guéris : 0");

        labelMorts =
                new Label("Morts : 0");

        labelSains.setTextFill(
                couleurEtat(
                        EtatSante.SAIN
                )
        );

        labelIncubation.setTextFill(
                couleurEtat(
                        EtatSante.INCUBATION
                )
        );

        labelMalades.setTextFill(
                couleurEtat(
                        EtatSante.MALADE
                )
        );

        labelGueris.setTextFill(
                couleurEtat(
                        EtatSante.GUERI
                )
        );

        labelMorts.setTextFill(
                couleurEtat(
                        EtatSante.MORT
                )
        );

        HBox ligneStats1 =
                new HBox(30);

        ligneStats1.setAlignment(
                Pos.CENTER
        );

        ligneStats1.getChildren().addAll(
                labelSains,
                labelMalades,
                labelIncubation
        );

        HBox ligneStats2 =
                new HBox(30);

        ligneStats2.setAlignment(
                Pos.CENTER
        );

        ligneStats2.getChildren().addAll(
                labelGueris,
                labelMorts
        );

        VBox zoneStats =
                new VBox(8);

        zoneStats.setAlignment(
                Pos.CENTER
        );

        zoneStats.getChildren().addAll(
                ligneStats1,
                ligneStats2
        );

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
                    return;
                }

                // Arrêt d'une ancienne simulation
                if (timelineSimulation != null) {

                    timelineSimulation.stop();
                }

                Individu.resetCompteurID();

                IParametresPopulation paramPop =
                        new TestParamPop(
                                taille
                        );

                ParametresMaladie paramMal =
                        lireParametresMaladie();

                this.sim =
                        new Simulation(
                                paramPop,
                                paramMal,
                                0
                        );

                this.pop =
                        sim.getPop();

                // ================= RESET =================

                this.jourActuel = 0;
                this.patientZeroCree = false;
                this.simulationTerminee = false;

                labelJour.setText(
                        "Jour 0"
                );

                zoneParametresMaladie.setDisable(
                        false
                );

                boutonGenererLiens.setDisable(
                        false
                );

                // Tableau
                tablePopulation.setItems(
                        FXCollections.observableArrayList(
                                pop.getIndividus()
                        )
                );

                // Affichage
                afficherPopulation(
                        zoneCentrale
                );

                mettreAJourStatistiques();

            } catch (NumberFormatException e) {

                System.out.println(
                        "Paramètre invalide"
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

            boutonGenererLiens.setDisable(
                    true
            );
        });

        // ======================================================
        // PANNEAU GAUCHE
        // ======================================================

        panneauGauche.getChildren().addAll(
                titrePopulation,
                labelTaille,
                champTaille,
                ligneBoutons,

                separateur1,

                zoneStats,

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

        stage.setScene(
                scene
        );

        stage.setTitle(
                "Simulation épidémique"
        );

        stage.show();

        stage.setMaximized(
                true
        );
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

            if (
                    sim == null
                    || simulationTerminee
            ) {
                return;
            }

            creerPatientZeroSiNecessaire();

            // Champ vide = +1 jour
            if (
                    champVitesse
                            .getText()
                            .isBlank()
            ) {

                avancerUnJour();

                return;
            }

            try {

                double vitesse =
                        Double.parseDouble(
                                champVitesse
                                        .getText()
                        );

                if (vitesse <= 0) {
                    return;
                }

                if (
                        timelineSimulation
                        != null
                ) {

                    timelineSimulation.stop();
                }

                double intervalleMillis =
                        1000.0 / vitesse;

                timelineSimulation =
                        new Timeline(
                                new KeyFrame(
                                        Duration.millis(
                                                intervalleMillis
                                        ),

                                        e ->
                                                avancerUnJour()
                                )
                        );

                timelineSimulation.setCycleCount(
                        Timeline.INDEFINITE
                );

                timelineSimulation.play();

            } catch (
                    NumberFormatException e
            ) {

                champVitesse.clear();
            }
        });

        // ======================================================
        // PAUSE
        // ======================================================

        boutonPause.setOnAction(event -> {

            if (
                    timelineSimulation
                    != null
            ) {

                timelineSimulation.pause();
            }
        });

        zoneControleSimulation
                .getChildren()
                .addAll(
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

        if (
                sim == null
                || simulationTerminee
        ) {
            return;
        }

        if (!patientZeroCree) {

            sim.patientZero();

            patientZeroCree = true;

            // On verrouille les paramètres maladie
            zoneParametresMaladie.setDisable(
                    true
            );

            mettreAJourCouleurs();

            mettreAJourTooltips();

            tablePopulation.refresh();

            mettreAJourStatistiques();
        }
    }


    // ==========================================================
    // AVANCER D'UN JOUR
    // ==========================================================

    private void avancerUnJour() {

        if (
                sim == null
                || simulationTerminee
        ) {
            return;
        }

        creerPatientZeroSiNecessaire();

        jourActuel++;

        // ================= VRAIE SIMULATION =================

        sim.jPlus1(
                jourActuel
        );

        labelJour.setText(
                "Jour "
                + jourActuel
        );

        // ================= MISE A JOUR UI =================

        mettreAJourCouleurs();

        mettreAJourTooltips();

        tablePopulation.refresh();

        mettreAJourStatistiques();

        // ================= FIN AUTOMATIQUE =================

        if (
                sim.estTerminee(
                        jourActuel
                )
        ) {

            simulationTerminee =
                    true;

            if (
                    timelineSimulation
                    != null
            ) {

                timelineSimulation.stop();
            }

            labelJour.setText(
                    "Jour "
                    + jourActuel
                    + " — TERMINÉE"
            );

            System.out.println(
                    "Simulation terminée au jour "
                    + jourActuel
            );
        }
    }


    // ==========================================================
    // TABLEAU
    // ==========================================================

    private TableView<Individu> creerTablePopulation() {

        TableView<Individu> table =
                new TableView<>();

        TableColumn<Individu, Integer> colonneId =
                new TableColumn<>("ID");

        colonneId.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getId()
                        )
        );

        TableColumn<Individu, Integer> colonneAge =
                new TableColumn<>("Âge");

        colonneAge.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getAge()
                        )
        );

        TableColumn<Individu, String> colonneSexe =
                new TableColumn<>("Sexe");

        colonneSexe.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getSexe()
                                        .toString()
                        )
        );

        TableColumn<Individu, String> colonneEtat =
                new TableColumn<>("État");

        colonneEtat.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getEtat()
                                        .toString()
                        )
        );

        TableColumn<Individu, Boolean> colonneDiab =
                new TableColumn<>("Diab.");

        colonneDiab.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .isDiabetique()
                        )
        );

        TableColumn<Individu, Integer> colonneContacts =
                new TableColumn<>("Contacts");

        colonneContacts.setCellValueFactory(
                data ->
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
                pop.getIndividus()
                        .size();

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

        double rayonPoint =
                4.0;

        double espacement =
                rayonPoint * 2.5;

        int indexIndividu =
                0;

        for (
                double rayon =
                        rayonMax;

                rayon > 20
                && indexIndividu
                < nbIndividus;

                rayon -=
                        espacement
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

        for (
                Individu individu :
                pop.getIndividus()
        ) {

            Circle pointA =
                    pointsIndividus.get(
                            individu
                    );

            for (
                    Individu contact :
                    individu.getContacts()
            ) {

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

                    ligne.setMouseTransparent(
                            true
                    );

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

        for (
                Individu individu :
                pop.getIndividus()
        ) {

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
    // STATISTIQUES
    // ==========================================================

    private void mettreAJourStatistiques() {

        if (pop == null) {
            return;
        }

        int sains = 0;
        int incubation = 0;
        int malades = 0;
        int gueris = 0;
        int morts = 0;

        for (
                Individu individu :
                pop.getIndividus()
        ) {

            switch (
                    individu.getEtat()
            ) {

                case SAIN:
                    sains++;
                    break;

                case INCUBATION:
                    incubation++;
                    break;

                case MALADE:
                    malades++;
                    break;

                case GUERI:
                    gueris++;
                    break;

                case MORT:
                    morts++;
                    break;
            }
        }

        labelSains.setText(
                "Sains : "
                + sains
        );

        labelIncubation.setText(
                "Incubation : "
                + incubation
        );

        labelMalades.setText(
                "Malades : "
                + malades
        );

        labelGueris.setText(
                "Guéris : "
                + gueris
        );

        labelMorts.setText(
                "Morts : "
                + morts
        );
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

        for (
                Individu individu :
                pop.getIndividus()
        ) {

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
    // PARAMETRES MALADIE
    // ==========================================================

    private VBox creerZoneParametresMaladie() {

        VBox zone =
                new VBox(8);

        zone.setPadding(
                new Insets(10)
        );

        Label titre =
                new Label(
                        "PARAMÈTRES MALADIE"
                );

        // ================= TRANSMISSION =================

        Label labelTransmission =
                new Label(
                        "Transmission"
                );

        champTransmission =
                new TextField(
                        "0.15"
                );

        // ================= GUERISON =================

        Label labelGuerison =
                new Label(
                        "Guérison"
                );

        champGuerison =
                new TextField(
                        "0.85"
                );

        // ================= INCUBATION =================

        Label labelIncubation =
                new Label(
                        "Incubation"
                );

        champIncubation =
                new TextField(
                        "5"
                );

        // ================= CONTAGION =================

        Label labelContagion =
                new Label(
                        "Contagion"
                );

        champContagion =
                new TextField(
                        "4"
                );

        // ================= REINFECTION =================

        Label labelReinfection =
                new Label(
                        "Réinfection"
                );

        champReinfection =
                new TextField(
                        "0.20"
                );

        // ================= DIABETE =================

        Label labelDiabete =
                new Label(
                        "Risque diabète"
                );

        champRisqueDiabete =
                new TextField(
                        "1.30"
                );

        // ================= LIGNES =================

        HBox ligne1 =
                new HBox(
                        10,
                        labelTransmission,
                        champTransmission,
                        labelGuerison,
                        champGuerison
                );

        HBox ligne2 =
                new HBox(
                        10,
                        labelIncubation,
                        champIncubation,
                        labelContagion,
                        champContagion
                );

        HBox ligne3 =
                new HBox(
                        10,
                        labelReinfection,
                        champReinfection,
                        labelDiabete,
                        champRisqueDiabete
                );

        ligne1.setAlignment(
                Pos.CENTER_LEFT
        );

        ligne2.setAlignment(
                Pos.CENTER_LEFT
        );

        ligne3.setAlignment(
                Pos.CENTER_LEFT
        );

        zone.getChildren().addAll(
                titre,
                ligne1,
                ligne2,
                ligne3
        );

        return zone;
    }


    // ==========================================================
    // LECTURE PARAMETRES MALADIE
    // ==========================================================

    private ParametresMaladie lireParametresMaladie() {

        double transmission =
                Double.parseDouble(
                        champTransmission
                                .getText()
                );

        double guerison =
                Double.parseDouble(
                        champGuerison
                                .getText()
                );

        int incubation =
                Integer.parseInt(
                        champIncubation
                                .getText()
                );

        int contagion =
                Integer.parseInt(
                        champContagion
                                .getText()
                );

        double reinfection =
                Double.parseDouble(
                        champReinfection
                                .getText()
                );

        double risqueDiabete =
                Double.parseDouble(
                        champRisqueDiabete
                                .getText()
                );

        return new ParametresMaladie(
                transmission,
                guerison,
                incubation,
                contagion,
                risqueDiabete,
                reinfection
        );
    }


    // ==========================================================
    // MAIN
    // ==========================================================

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}