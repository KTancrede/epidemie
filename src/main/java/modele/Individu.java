package main.java.modele;

import java.util.HashSet;

public class Individu {

	private final int id;
	private static int compteurID = 0;

	private int age;
	private Sexe sexe;
	private int nbContactsSouhaites;
	private boolean isDiabetique;
	private HashSet<Individu> contacts;

	private int joursDepuisInfection;
	private int jourDeLinfection;
	private EtatSante etat;

	public Individu(int age, EtatSante etat, int nbContactsSouhaites,Sexe sexe, boolean isDiabetique) {
		this.id = compteurID++;
		this.age = age;
		this.etat=etat;
		contacts = new HashSet<>();
		this.nbContactsSouhaites = nbContactsSouhaites;
		this.sexe=sexe;
		this.isDiabetique = isDiabetique;
	}

	public void addContact(Individu i) {
		if (i != this) {
			contacts.add(i);
			i.contacts.add(this);
		}
	}

	@Override
	public String toString() {
	    return String.format(
	    	"Individu %-4d -> Age : %3d | %-5s | Etat : %-10s %-10s %-20s",
	        id,
	        age,
	        sexe,
	        etat,
	        isDiabetique ? " | Diab " : "",
	        !this.contacts.isEmpty() ?  ("En lien avec "+ afficherContact()): ""
	    );
	}

	public String afficherContact() {
		StringBuilder sb = new StringBuilder();
		for (Individu i : contacts) {
			sb.append(i.id + " ");
		}
		return sb.toString();
	}

	public int getAge() {
		return age;
	}

	public EtatSante getEtat() {
		return etat;
	}

	public int getNbContactsSouhaites() {
		return nbContactsSouhaites;
	}

	public int getTailleListeContacts() {
		return contacts.size();
	}

	public boolean isLinkedTo(Individu i) {
		return this.contacts.contains(i);
	}

	public int getJoursDepuisInfection() {
		return joursDepuisInfection;
	}

	public void setEtat(EtatSante nouvelEtat, int jourSimulation) {

	    this.etat = nouvelEtat;

	    switch (nouvelEtat) {

	    case SAIN:
	        System.out.println("L'individu " + this.id + " est sain.");
	        break;

	    case INCUBATION:
	        this.jourDeLinfection = jourSimulation;
	        this.joursDepuisInfection = 0;

	        System.out.println(
	            "L'individu " + this.id +
	            " a été infecté "//+au jour \" + jourSimulation
	        );
	        break;

	    case MALADE:
	        System.out.println(
	            "L'individu " + this.id +
	            " est devenu malade "//+au jour \" + jourSimulation
	        );
	        break;

	    case GUERI:
	        System.out.println(
	            "L'individu " + this.id + 
	            " a guéri "//+au jour " + jourSimulation
	        );
	        break;

	    case MORT:
	        System.out.println(
	            "L'individu " + this.id +
	            " est mort " //+au jour " + jourSimulation
	        );
	        break;
	    }
	}

	public void setJoursDepuisInfection(int joursDepuisInfection) {
		this.joursDepuisInfection = joursDepuisInfection;
	}
	
	public  HashSet<Individu> getContacts(){
		return this.contacts;
	}
	public boolean isDiabetique() {
		return this.isDiabetique;
	}
}