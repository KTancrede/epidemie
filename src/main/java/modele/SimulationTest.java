package main.java.modele;

import main.java.parametres.TestParamMal;
import main.java.parametres.TestParamPop;

public class SimulationTest {
	
	public static void main(String[] args) {
		Simulation sim = new Simulation(new TestParamPop(2000), new TestParamMal(), 1000);
		Population pop = sim.getPop();
		
		System.out.println("===================== MISE EN PLACE POPULATION =====================");
		System.out.println(pop);
		
		System.out.println("\n===================== MISE EN PLACE DES LIENS =====================");
		pop.creationLiens();
		System.out.println(pop);
		
		System.out.println("\n===========Jour 0===========");
		sim.patientZero();
		
		for (int i = 1; i <=sim.getTempsSim(); i++) {
			System.out.println("\n===========Jour "+i +"===========");
			sim.jPlus1(i);
		}
	}
		
}
