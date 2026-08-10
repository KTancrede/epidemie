package modele;

import java.util.HashSet;

public class Individu {

	private final int id;
	private static int compteurID = 0;

	private int age;
	private int nbContactsSouhaites;

	private HashSet<Individu> contacts;

	private int joursDepuisInfection;
	private int jourDeLinfection;
	private EtatSante etat;

	public Individu(int age, EtatSante etat, int nbContactsSouhaites) {
		this.id = compteurID++;
		this.age = age;
		this.setEtat(etat);
		contacts = new HashSet<>();
		this.nbContactsSouhaites = nbContactsSouhaites;
	}

	public void addContact(Individu i) {
		if (i != this) {
			contacts.add(i);
			i.contacts.add(this);
		}
	}

	@Override
	public String toString() {
		if (this.age < 10)
			return "Individu " + this.id + "-> Age :" + this.age + "  - Etat :" + this.etat + "| en lien avec :"
					+ afficherContact() + "| Objectif de " + this.nbContactsSouhaites + " contacts";
		return "Individu " + this.id + "-> Age :" + this.age + " - Etat :" + this.etat + "| en lien avec :"
				+ afficherContact() + "| Objectif de " + this.nbContactsSouhaites + " contacts";
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

	public void setEtat(EtatSante etat) {
		this.etat = etat;
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

	public void setMalade(int joursDeLinfection) {
		this.etat = EtatSante.MALADE;
		this.joursDepuisInfection = 0;
		this.jourDeLinfection = joursDeLinfection;
		System.out.println("L'individu " + this.id + " est tombé malade au jour " + this.jourDeLinfection);
	}

	public void setJoursDepuisInfection(int joursDepuisInfection) {
		this.joursDepuisInfection = joursDepuisInfection;
	}
}