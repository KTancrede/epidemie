package modele;

import java.util.HashSet;

import parametres.IParametresMaladie;
import parametres.IParametresPopulation;

public class Simulation {

    private int tempsSimulation;
    private IParametresPopulation paramPop;
    private IParametresMaladie paramMal;
    private Population pop;
    private HashSet<Individu> infectes;

    private int dernierJourNouvelleInfection = 0;

    public Simulation(
            IParametresPopulation paramPop,
            IParametresMaladie paramMal,
            int tempsSimulation) {

        this.paramMal = paramMal;
        this.paramPop = paramPop;
        this.pop = new Population(paramPop);
        this.tempsSimulation = tempsSimulation;
        this.infectes = new HashSet<>();
    }

    public void patientZero() {

        int z = (int) (
                Math.random()
                * pop.getTaillePop()
        );

        Individu patientZero =
                pop.getIndividus().get(z);

        patientZero.setEtat(
                EtatSante.INCUBATION,
                0
        );

        this.infectes.add(patientZero);

        this.dernierJourNouvelleInfection = 0;

        System.out.println(patientZero);
    }

    public Population getPop() {
        return pop;
    }

    public int getTempsSim() {
        return this.tempsSimulation;
    }

    public void jPlus1(int jourSim) {

        HashSet<Individu> nouveauxMalades =
                new HashSet<>();

        HashSet<Individu> finMaladie =
                new HashSet<>();

        for (Individu malade : infectes) {

            // ================= AVANCER D'UN JOUR =================

            malade.setJoursDepuisInfection(
                    malade.getJoursDepuisInfection() + 1
            );

            int joursInfecte =
                    malade.getJoursDepuisInfection();

            // ================= FIN INCUBATION =================

            if (
                    joursInfecte
                    == paramMal.dureeIncubation()
            ) {

                malade.setEtat(
                        EtatSante.MALADE,
                        jourSim
                );
            }

            // ================= PERIODE CONTAGIEUSE =================

            if (
                    joursInfecte
                    >= paramMal.dureeIncubation()

                    &&

                    joursInfecte
                    < paramMal.dureeIncubation()
                    + paramMal.dureeContagion()
            ) {

                for (Individu contact : malade.getContacts()) {

                    // ================= PERSONNE SAINE =================

                    if (
                            contact.getEtat()
                            == EtatSante.SAIN
                    ) {

                        if (
                                Math.random()
                                <= paramMal.probabiliteTransmission()
                        ) {

                            contact.setEtat(
                                    EtatSante.INCUBATION,
                                    jourSim
                            );

                            contact.setJoursDepuisInfection(0);

                            nouveauxMalades.add(contact);

                            dernierJourNouvelleInfection =
                                    jourSim;
                        }
                    }

                    // ================= PERSONNE GUERIE =================

                    else if (
                            contact.getEtat()
                            == EtatSante.GUERI
                    ) {

                        double probaReinfection =
                                paramMal.probabiliteTransmission()
                                * paramMal.probabiliteReinfection();

                        if (
                                Math.random()
                                <= probaReinfection
                        ) {

                            contact.setEtat(
                                    EtatSante.INCUBATION,
                                    jourSim
                            );

                            contact.setJoursDepuisInfection(0);

                            nouveauxMalades.add(contact);

                            dernierJourNouvelleInfection =
                                    jourSim;
                        }
                    }
                }
            }

            // ================= FIN DE LA MALADIE =================

            if (
                    joursInfecte
                    >= paramMal.dureeIncubation()
                    + paramMal.dureeContagion()
            ) {

                double probaDeces =
                        1.0
                        - paramMal.probabiliteGuerison();

                probaDeces *=
                        paramMal.facteurRisqueAge(
                                malade.getAge()
                        );

                if (malade.isDiabetique()) {

                    probaDeces *=
                            paramMal.facteurRisqueDiabete();
                }

                probaDeces =
                        Math.min(
                                probaDeces,
                                1.0
                        );

                if (
                        Math.random()
                        < probaDeces
                ) {

                    malade.setEtat(
                            EtatSante.MORT,
                            jourSim
                    );

                } else {

                    malade.setEtat(
                            EtatSante.GUERI,
                            jourSim
                    );
                }

                finMaladie.add(malade);
            }
        }

        infectes.removeAll(finMaladie);
        infectes.addAll(nouveauxMalades);
    }

    // ================= FIN DE SIMULATION =================

    public boolean estTerminee(int jourSim) {

        return infectes.isEmpty();
                
    }
}