package main.java.modele;

import java.util.HashSet;

import main.java.parametres.IParametresMaladie;
import main.java.parametres.IParametresPopulation;

public class Simulation {
	
	private int tempsSimulation;
	private IParametresPopulation paramPop;
	private IParametresMaladie paramMal;
	private Population pop;
	private HashSet<Individu> infectes;
	
	public Simulation(IParametresPopulation paramPop,IParametresMaladie paramMal,int tempsSimulation) {
		this.paramMal = paramMal;
		this.paramPop = paramPop;
		this.pop= new Population(paramPop);
		this.tempsSimulation=tempsSimulation;
		this.infectes = new HashSet<>();
	}
	
	public void patientZero() {
		int z = (int) (Math.random() * pop.getTaillePop());

		Individu patientZero = pop.getIndividus().get(z);
		patientZero.setEtat(EtatSante.INCUBATION, 0);
	    this.infectes.add(patientZero);
	    System.out.println(patientZero);
	}
	
	public Population getPop() {
		return pop;
	}
	public int getTempsSim() {
		return this.tempsSimulation;
	}
	
	public void jPlus1(int jourSim) {

	    HashSet<Individu> nouveauxMalades = new HashSet<>();
	    HashSet<Individu> finMaladie = new HashSet<>();

	    for (Individu malade : infectes) {

	        // On avance d'un jour
	        malade.setJoursDepuisInfection(
	                malade.getJoursDepuisInfection() + 1
	        );

	        int joursInfecte = malade.getJoursDepuisInfection();

	        // Fin de l'incubation
	        if (joursInfecte == paramMal.dureeIncubation()) {
	            malade.setEtat(EtatSante.MALADE, jourSim);
	        }

	        // Période contagieuse
	        if (joursInfecte >= paramMal.dureeIncubation()
	                && joursInfecte < paramMal.dureeIncubation()
	                        + paramMal.dureeContagion()) {

	            for (Individu contact : malade.getContacts()) {

	                if (contact.getEtat() == EtatSante.SAIN
	                        && Math.random() <= paramMal.probabiliteTransmission()) {

	                    contact.setEtat(EtatSante.INCUBATION, jourSim);
	                    nouveauxMalades.add(contact);
	                }
	            }
	        }

	        // Fin de la maladie
	        
	        if (joursInfecte >= paramMal.dureeIncubation()
	                + paramMal.dureeContagion()) {
	        	double probaDeces = 1.0 - paramMal.probabiliteGuerison();

	        	probaDeces *= paramMal.facteurRisqueAge(malade.getAge());
	        	if (malade.isDiabetique()) {
	        		probaDeces *= paramMal.facteurRisqueDiabete();
	        	}
	        	
	        	probaDeces = Math.min(probaDeces, 1.0);
	        	if (Math.random() < probaDeces) {
	        	    malade.setEtat(EtatSante.MORT, jourSim);
	        	} else {
	        	    malade.setEtat(EtatSante.GUERI, jourSim);
	        	}

	            finMaladie.add(malade);
	        }
	    }

	    infectes.removeAll(finMaladie);
	    infectes.addAll(nouveauxMalades);
	}
}