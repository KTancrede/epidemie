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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;

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
    private Individu patientZeroOriginal;
    // ==========================================================
    // ELEMENTS GRAPHIQUES
    // ==========================================================

    private final Map<Individu, Circle> pointsIndividus =
            new HashMap<>();

    private final Map<Individu, Tooltip> tooltipsIndividus =
            new HashMap<>();

    private Label labelJour;

    private TableView<Individu> tablePopulation;

    // ==========================================================
    // STATISTIQUES
    // ==========================================================

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


    // ==========================================================
    // START
    // ==========================================================

    @Override
    public void start(Stage stage) {
    	
    	stage.getIcons().add(
    	        new Image(
    	                getClass()
    	                        .getResourceAsStream("/images/logo.png")
    	        )
    	);
        BorderPane root = new BorderPane();

        root.getStyleClass().add(
                "root-simulation"
        );

        // ======================================================
        // PANNEAU GAUCHE
        // ======================================================

        VBox panneauGauche =
                new VBox(12);

        panneauGauche.getStyleClass().add(
                "panneau-gauche"
        );

        panneauGauche.prefWidthProperty()
                .bind(
                        root.widthProperty()
                                .multiply(0.28)
                );

        // ======================================================
        // PANNEAU DROIT
        // ======================================================

        VBox panneauDroit =
                new VBox(10);

        panneauDroit.getStyleClass().add(
                "panneau-droit"
        );

        // ======================================================
        // ZONE POPULATION
        // ======================================================

        Pane zoneCentrale =
                new Pane();

        zoneCentrale.getStyleClass().add(
                "zone-population"
        );

        // ======================================================
        // ZONE INFORMATIONS
        // ======================================================

        HBox zoneInformations =
                new HBox();

        zoneInformations.getStyleClass().add(
                "zone-informations"
        );

        // 80 % de la hauteur pour la population
        zoneCentrale.prefHeightProperty()
                .bind(
                        panneauDroit
                                .heightProperty()
                                .multiply(0.80)
                );

        // 20 % pour les contrôles et paramètres
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

        // ======================================================
        // PARAMETRES MALADIE
        // ======================================================

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
                new Label(
                        "PARAMÈTRES POPULATION"
                );

        titrePopulation.getStyleClass().add(
                "titre-section"
        );

        Label labelTaille =
                new Label(
                        "Taille de la population"
                );

        labelTaille.getStyleClass().add(
                "label-parametre"
        );

        TextField champTaille =
                new TextField();

        champTaille.getStyleClass().add(
                "champ-parametre"
        );

        // ======================================================
        // BOUTONS POPULATION
        // ======================================================

        Button boutonGenererPopulation =
                new Button(
                        "Générer la population"
                );

        boutonGenererPopulation
                .getStyleClass()
                .add(
                        "bouton-principal"
                );

        Button boutonGenererLiens =
                new Button(
                        "Générer les liens"
                );

        boutonGenererLiens
                .getStyleClass()
                .add(
                        "bouton-secondaire"
                );

        boutonGenererLiens.setDisable(
                true
        );

        // Entrée dans le champ taille
        champTaille.setOnAction(
                event ->
                        boutonGenererPopulation.fire()
        );

        HBox ligneBoutons =
                new HBox(10);

        ligneBoutons.getStyleClass().add(
                "ligne-boutons"
        );

        ligneBoutons.getChildren().addAll(
                boutonGenererPopulation,
                boutonGenererLiens
        );

        // ======================================================
        // SEPARATEUR
        // ======================================================

        Separator separateur1 =
                new Separator();

        separateur1.getStyleClass().add(
                "separateur-section"
        );

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

        // Couleurs entièrement gérées en CSS

        labelSains
                .getStyleClass()
                .add("stat-sain");

        labelIncubation
                .getStyleClass()
                .add("stat-incubation");

        labelMalades
                .getStyleClass()
                .add("stat-malade");

        labelGueris
                .getStyleClass()
                .add("stat-gueri");

        labelMorts
                .getStyleClass()
                .add("stat-mort");

        // Première ligne statistiques

        HBox ligneStats1 =
                new HBox(30);

        ligneStats1.setAlignment(
                Pos.CENTER
        );

        ligneStats1.getStyleClass().add(
                "ligne-stats"
        );

        ligneStats1.getChildren().addAll(
                labelSains,
                labelMalades,
                labelIncubation
        );

        // Deuxième ligne statistiques

        HBox ligneStats2 =
                new HBox(30);

        ligneStats2.setAlignment(
                Pos.CENTER
        );

        ligneStats2.getStyleClass().add(
                "ligne-stats"
        );

        ligneStats2.getChildren().addAll(
                labelGueris,
                labelMorts
        );

        // Zone statistiques

        VBox zoneStats =
                new VBox(8);

        zoneStats.setAlignment(
                Pos.CENTER
        );

        zoneStats.getStyleClass().add(
                "zone-stats"
        );

        zoneStats.getChildren().addAll(
                ligneStats1,
                ligneStats2
        );

        // ======================================================
        // SECOND SEPARATEUR
        // ======================================================

        Separator separateur3 =
                new Separator();

        separateur3.getStyleClass().add(
                "separateur-section"
        );

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

        boutonGenererPopulation.setOnAction(
                event -> {

                    try {

                        int taille =
                                Integer.parseInt(
                                        champTaille
                                                .getText()
                                );

                        if (taille <= 0) {
                            return;
                        }

                        // Arrêt d'une ancienne simulation

                        if (
                                timelineSimulation
                                != null
                        ) {

                            timelineSimulation.stop();
                        }

                        Individu.resetCompteurID();

                        // ================= PARAM POP =================

                        IParametresPopulation paramPop =
                                new TestParamPop(
                                        taille
                                );

                        // ================= PARAM MAL =================

                        ParametresMaladie paramMal =
                                lireParametresMaladie();

                        // ================= SIMULATION =================

                        this.sim =
                                new Simulation(
                                        paramPop,
                                        paramMal,
                                        0
                                );

                        this.pop =
                                sim.getPop();

                        // ================= RESET =================

                        this.jourActuel =
                                0;

                        this.patientZeroCree =
                                false;

                        this.simulationTerminee =
                                false;

                        labelJour.setText(
                                "Jour 0"
                        );

                        // Les paramètres peuvent être modifiés
                        // jusqu'au lancement de la simulation.

                        zoneParametresMaladie
                                .setDisable(false);

                        boutonGenererLiens
                                .setDisable(false);

                        // ================= TABLEAU =================

                        tablePopulation.setItems(
                                FXCollections
                                        .observableArrayList(
                                                pop.getIndividus()
                                        )
                        );

                        // ================= GRAPH =================

                        afficherPopulation(
                                zoneCentrale
                        );

                        mettreAJourStatistiques();

                    } catch (
                            NumberFormatException e
                    ) {

                        System.out.println(
                                "Paramètre invalide"
                        );
                    }
                }
        );

        // ======================================================
        // GENERER LIENS
        // ======================================================

        boutonGenererLiens.setOnAction(
                event -> {

                    if (
                            this.pop == null
                    ) {
                        return;
                    }

                    this.pop.creationLiens();

                    afficherLiens(
                            zoneCentrale
                    );

                    mettreAJourTooltips();

                    tablePopulation.refresh();

                    boutonGenererLiens
                            .setDisable(true);
                }
        );

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

        // Chargement CSS

        scene.getStylesheets().add(
                getClass()
                        .getResource(
                                "/vue/simulation.css"
                        )
                        .toExternalForm()
        );

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

        zoneControleSimulation.getStyleClass().add(
                "zone-controle-simulation"
        );

        zoneControleSimulation.setAlignment(
                Pos.CENTER
        );

        // ======================================================
        // JOUR
        // ======================================================

        this.labelJour =
                new Label(
                        "Jour 0"
                );

        labelJour.getStyleClass().add(
                "label-jour"
        );

        // ======================================================
        // PLAY
        // ======================================================

        Button boutonPlay =
                new Button("▶");

        boutonPlay.getStyleClass().add(
                "bouton-play"
        );

        // ======================================================
        // PAUSE
        // ======================================================

        Button boutonPause =
                new Button("⏸");

        boutonPause.getStyleClass().add(
                "bouton-pause"
        );
        
        // ======================================================
        // RESET (1?)
        // ======================================================
        
        Button boutonReset =
                new Button("↻");

        boutonReset.getStyleClass().add(
                "bouton-reset"
        );
        
        Tooltip tooltipReset =
                new Tooltip(
                        "Reset de l'épidémie avec le même patient 0 et la même population"
                );

        tooltipReset.setShowDelay(
                Duration.millis(200)
        );

        Tooltip.install(
                boutonReset,
                tooltipReset
        );
        // ======================================================
        // VITESSE
        // ======================================================

        TextField champVitesse =
                new TextField();

        champVitesse.getStyleClass().add(
                "champ-vitesse"
        );

        champVitesse.setPromptText(
                "Jours / seconde"
        );

        // ======================================================
        // ACTION PLAY
        // ======================================================

        boutonPlay.setOnAction(
                event -> {

                    if (
                            sim == null
                            || simulationTerminee
                    ) {

                        return;
                    }

                    creerPatientZeroSiNecessaire();

                    // Champ vide :
                    // un clic = +1 jour

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

                        if (
                                vitesse <= 0
                        ) {

                            return;
                        }

                        if (
                                timelineSimulation
                                != null
                        ) {

                            timelineSimulation.stop();
                        }

                        double intervalleMillis =
                                1000.0
                                / vitesse;

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
                }
        );

        // ======================================================
        // ACTION PAUSE
        // ======================================================

        boutonPause.setOnAction(
                event -> {

                    if (
                            timelineSimulation
                            != null
                    ) {

                        timelineSimulation.pause();
                    }
                }
        );
        // ======================================================
        // ACTION RESET 1
        // ======================================================

        boutonReset.setOnAction(event -> {

            if (
                    sim == null
                    || patientZeroOriginal == null
            ) {
                return;
            }

            if (timelineSimulation != null) {
                timelineSimulation.stop();
            }

            // ================= REMISE A ZERO DES INDIVIDUS =================

            for (Individu individu : pop.getIndividus()) {

                individu.setEtat(
                        EtatSante.SAIN,
                        0
                );

                individu.setJoursDepuisInfection(
                        0
                );
            }

            // ================= RESET INTERFACE =================

            jourActuel = 0;

            simulationTerminee = false;

            patientZeroCree = false;

            labelJour.setText(
                    "Jour 0"
            );

            // ================= PARAMETRES MALADIE MODIFIABLES =================

            zoneParametresMaladie.setDisable(
                    false
            );

            mettreAJourCouleurs();
            mettreAJourTooltips();
            mettreAJourStatistiques();

            tablePopulation.refresh();
        });
        // ======================================================
        // AJOUT
        // ======================================================

        HBox lignePauseReset =
                new HBox(
                        8,
                        boutonPause,
                        boutonReset
                );

        lignePauseReset.setAlignment(
                Pos.CENTER
        );

        zoneControleSimulation
                .getChildren()
                .addAll(
                        labelJour,
                        boutonPlay,
                        champVitesse,
                        lignePauseReset
                );

        return zoneControleSimulation;
    }


    // ==========================================================
    // PATIENT ZERO
    // ==========================================================

    private void creerPatientZeroSiNecessaire() {

        if (
                pop == null
                || simulationTerminee
        ) {
            return;
        }

        if (!patientZeroCree) {

            ParametresMaladie paramMal =
                    lireParametresMaladie();

            // On recrée la simulation
            // MAIS avec la même population
            sim =
                    new Simulation(
                            pop,
                            paramMal,
                            0
                    );

            // Premier lancement :
            // patient zéro aléatoire
            if (patientZeroOriginal == null) {

                sim.patientZero();

                patientZeroOriginal =
                        sim.getPatientZero();

            } else {

                // Lancements suivants :
                // exactement le même patient zéro

                sim.definirPatientZero(
                        patientZeroOriginal
                );
            }

            patientZeroCree =
                    true;

            zoneParametresMaladie
                    .setDisable(true);

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

        // ======================================================
        // SIMULATION
        // ======================================================

        sim.jPlus1(
                jourActuel
        );

        labelJour.setText(
                "Jour "
                + jourActuel
        );

        // ======================================================
        // MISE A JOUR INTERFACE
        // ======================================================

        mettreAJourCouleurs();

        mettreAJourTooltips();

        tablePopulation.refresh();

        mettreAJourStatistiques();

        // ======================================================
        // FIN AUTOMATIQUE
        // ======================================================

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

        table.getStyleClass().add(
                "table-population"
        );
        table.setColumnResizePolicy(
        		 TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );
        
        table.setRowFactory(tv -> {

            TableRow<Individu> row =
                    new TableRow<>();

            row.setOnMouseEntered(event -> {

                Individu individu =
                        row.getItem();

                if (individu == null) {
                    return;
                }

                Circle point =
                        pointsIndividus.get(individu);

                if (point != null) {

                    point.setScaleX(2.0);
                    point.setScaleY(2.0);

                    point.toFront();
                }
            });

            row.setOnMouseExited(event -> {

                Individu individu =
                        row.getItem();

                if (individu == null) {
                    return;
                }

                Circle point =
                        pointsIndividus.get(individu);

                if (point != null) {

                    point.setScaleX(1.0);
                    point.setScaleY(1.0);
                }
            });

            return row;
        });
        
        // ======================================================
        // ID
        // ======================================================

        TableColumn<Individu, Integer> colonneId =
                new TableColumn<>("ID");

        colonneId.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getId()
                        )
        );

        // ======================================================
        // AGE
        // ======================================================

        TableColumn<Individu, Integer> colonneAge =
                new TableColumn<>("Âge");

        colonneAge.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getAge()
                        )
        );

        // ======================================================
        // SEXE
        // ======================================================

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

        // ======================================================
        // ETAT
        // ======================================================

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

        // ======================================================
        // DIABETE
        // ======================================================

        TableColumn<Individu, Boolean> colonneDiab =
                new TableColumn<>("Diab.");

        colonneDiab.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .isDiabetique()
                        )
        );

        // ======================================================
        // CONTACTS
        // ======================================================

        TableColumn<Individu, Integer> colonneContacts =
                new TableColumn<>(
                        "Contacts"
                );

        colonneContacts.setCellValueFactory(
                data ->
                        new ReadOnlyObjectWrapper<>(
                                data.getValue()
                                        .getTailleListeContacts()
                        )
        );

        // ======================================================
        // AJOUT COLONNES
        // ======================================================

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
                zoneCentrale
                        .getWidth();

        double hauteur =
                zoneCentrale
                        .getHeight();

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
                6.0;

        double espacement =
                rayonPoint * 2.5;

        int indexIndividu =
                0;

        // ======================================================
        // ANNEAUX
        // ======================================================

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

            // ==================================================
            // INDIVIDUS DE L'ANNEAU
            // ==================================================

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
                        * Math.cos(
                                angle
                        );

                double y =
                        centreY
                        + rayon
                        * Math.sin(
                                angle
                        );

                Circle point =
                        new Circle(
                                x,
                                y,
                                rayonPoint
                        );

                // Classe générique

                point.getStyleClass().add(
                        "individu"
                );

                // Classe correspondant à son état

                appliquerClasseEtat(
                        point,
                        individu.getEtat()
                );

                // ==================================================
                // TOOLTIP
                // ==================================================

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
                        .add(
                                point
                        );

                pointsIndividus.put(
                        individu,
                        point
                );

                indexIndividu++;
            }
        }
    }


    // ==========================================================
    // CLASSE CSS SELON ETAT
    // ==========================================================

    private void appliquerClasseEtat(
            Circle point,
            EtatSante etat
    ) {

        // On enlève les anciennes classes d'état

        point.getStyleClass().removeAll(
                "individu-sain",
                "individu-incubation",
                "individu-malade",
                "individu-gueri",
                "individu-mort"
        );

        // Puis on applique la nouvelle

        switch (etat) {

            case SAIN ->

                    point.getStyleClass().add(
                            "individu-sain"
                    );

            case INCUBATION ->

                    point.getStyleClass().add(
                            "individu-incubation"
                    );

            case MALADE ->

                    point.getStyleClass().add(
                            "individu-malade"
                    );

            case GUERI ->

                    point.getStyleClass().add(
                            "individu-gueri"
                    );

            case MORT ->

                    point.getStyleClass().add(
                            "individu-mort"
                    );
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

                // On évite A-B puis B-A

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

                    // Apparence entièrement dans CSS

                    ligne.getStyleClass().add(
                            "lien-contact"
                    );

                    // Les liens ne captent pas la souris

                    ligne.setMouseTransparent(
                            true
                    );

                    // Toujours derrière les points

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
    // MISE A JOUR COULEURS / CLASSES
    // ==========================================================

    private void mettreAJourCouleurs() {

        if (
                pop == null
        ) {

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

            if (
                    point != null
            ) {

                appliquerClasseEtat(
                        point,
                        individu.getEtat()
                );
            }
        }
    }


    // ==========================================================
    // STATISTIQUES
    // ==========================================================

    private void mettreAJourStatistiques() {

        if (
                pop == null
        ) {

            return;
        }

        int sains =
                0;

        int incubation =
                0;

        int malades =
                0;

        int gueris =
                0;

        int morts =
                0;

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

        if (
                pop == null
        ) {

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

            if (
                    tooltip != null
            ) {

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

        zone.getStyleClass().add(
                "zone-parametres-maladie"
        );

        // ======================================================
        // TITRE
        // ======================================================

        Label titre =
                new Label(
                        "PARAMÈTRES MALADIE"
                );

        titre.getStyleClass().add(
                "titre-section"
        );

        // ======================================================
        // TRANSMISSION
        // ======================================================

        Label labelTransmission =
                new Label(
                        "Transmission (%)"
                );

        labelTransmission
                .getStyleClass()
                .add(
                        "label-parametre"
                );
        labelTransmission.getStyleClass().add("label-maladie");
        champTransmission =
                new TextField(
                        "0.15"
                );

        champTransmission
                .getStyleClass()
                .add(
                        "champ-maladie"
                );

        // ======================================================
        // GUERISON
        // ======================================================

        Label labelGuerison =
                new Label(
                        "Guérison (%)"
                );

        labelGuerison
                .getStyleClass()
                .add(
                        "label-parametre"
                );
        labelGuerison
	        .getStyleClass()
	        .add("label-maladie");

        champGuerison =
                new TextField(
                        "0.85"
                );

        champGuerison
                .getStyleClass()
                .add(
                        "champ-maladie"
                );

        // ======================================================
        // INCUBATION
        // ======================================================

        Label labelIncubation =
                new Label(
                        "Incubation (j)"
                );

        labelIncubation
                .getStyleClass()
                .add(
                        "label-parametre"
                );
        labelIncubation.getStyleClass().add("label-maladie");
        champIncubation =
                new TextField(
                        "5"
                );

        champIncubation
                .getStyleClass()
                .add(
                        "champ-maladie"
                );

        // ======================================================
        // CONTAGION
        // ======================================================

        Label labelContagion =
                new Label(
                        "Contagion (j)"
                );

        labelContagion
                .getStyleClass()
                .add(
                        "label-parametre"
                );
        labelContagion.getStyleClass().add("label-maladie");
        champContagion =
                new TextField(
                        "4"
                );

        champContagion
                .getStyleClass()
                .add(
                        "champ-maladie"
                );

        // ======================================================
        // REINFECTION
        // ======================================================

        Label labelReinfection =
                new Label(
                        "Réinfection (%)"
                );

        labelReinfection
                .getStyleClass()
                .add(
                        "label-parametre"
                );
        labelReinfection.getStyleClass().add("label-maladie");
        champReinfection =
                new TextField(
                        "0.20"
                );

        champReinfection
                .getStyleClass()
                .add(
                        "champ-maladie"
                );

        // ======================================================
        // DIABETE
        // ======================================================

        Label labelDiabete =
                new Label(
                        "Risque diabète (x)"
                );

        labelDiabete
                .getStyleClass()
                .add(
                        "label-parametre"
                );

        champRisqueDiabete =
                new TextField(
                        "1.30"
                );
        labelDiabete.getStyleClass().add("label-maladie");
        champRisqueDiabete
                .getStyleClass()
                .add(
                        "champ-maladie"
                );

        // ======================================================
        // LIGNE 1
        // ======================================================

        HBox ligne1 =
                new HBox(
                        10,

                        labelTransmission,
                        champTransmission,

                        labelGuerison,
                        champGuerison
                );

        ligne1.getStyleClass().add(
                "ligne-parametres"
        );

        ligne1.setAlignment(
                Pos.CENTER_LEFT
        );

        // ======================================================
        // LIGNE 2
        // ======================================================

        HBox ligne2 =
                new HBox(
                        10,

                        labelIncubation,
                        champIncubation,

                        labelContagion,
                        champContagion
                );

        ligne2.getStyleClass().add(
                "ligne-parametres"
        );

        ligne2.setAlignment(
                Pos.CENTER_LEFT
        );

        // ======================================================
        // LIGNE 3
        // ======================================================

        HBox ligne3 =
                new HBox(
                        10,

                        labelReinfection,
                        champReinfection,

                        labelDiabete,
                        champRisqueDiabete
                );

        ligne3.getStyleClass().add(
                "ligne-parametres"
        );

        ligne3.setAlignment(
                Pos.CENTER_LEFT
        );

        // ======================================================
        // AJOUT
        // ======================================================

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