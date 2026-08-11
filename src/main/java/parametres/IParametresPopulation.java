package main.java.parametres;

import main.java.modele.Sexe;

public interface IParametresPopulation {
	
	public int taillePop();
	public Sexe sexe();
	public int ageIndividu();
	public int nbContacteSouhaite(int age);
	public boolean isDiabetique(int age,Sexe sexe);
}
