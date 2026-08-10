package modele;

import parametres.ParametresMaladie;
import parametres.ParametresPopulation;
import parametres.TestParamMal;
import parametres.TestParamPop;



public class Simulation {
	public static final int NB_JOURS_SIMULATION = 10;
	
	
	public static void main(String[] args) {
		
		ParametresPopulation paramPop = new TestParamPop();
		ParametresMaladie paramMal = new TestParamMal();
		Population pop = new  Population(paramPop);
		
		System.out.println("======= MISE EN PLACE POPULATION =======");
		System.out.println(pop);
		
		System.out.println("\n======= MISE EN PLACE DES LIENS =======");
		pop.creationLiens();
		System.out.println(pop);
		
		System.out.println("\n======= Jour 0 =======");
		pop.patientZero();
		
		for (int i = 0; i <NB_JOURS_SIMULATION; i++) {
			System.out.println("\n===========Jour "+i +"===========");
			
		}
		
	}
}