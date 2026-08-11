package modele;

import java.util.ArrayList;

import IParametresPopulation;

public class Population {

	private IParametresPopulation param;
	private int taillePop;
	private ArrayList<Individu> individus = new ArrayList<>();

	public Population(IParametresPopulation param) {
		this.param = param;
		this.taillePop = param.taillePop();

		for (int i = 0; i < this.taillePop; i++) {
			int age = param.ageIndividu(); // repartition selon des sources 
			Sexe sexe = param.sexe();
			individus.add(
					new Individu(age, 
							EtatSante.SAIN, 
							param.nbContacteSouhaite(age), // nombre en fonction de l'age sourcé aussi
							sexe, // distribution uniforme
							param.isDiabetique(age,sexe)
							)
					); 
		}
	}
	
	public ArrayList<Individu> getIndividus(){
		return individus;
	}
	
	public int getTaillePop() {
		return taillePop;
	}
	
	@Override
	public String toString() {
		StringBuilder r = new StringBuilder();

		for (Individu i : individus) {
			r.append(i + "\n");
		}
		return r.toString();
	}

	public void creationLiens() {
		for (Individu i : individus) {
			while (i.getTailleListeContacts() < i.getNbContactsSouhaites()) {
				ArrayList<Individu> disponibles = new ArrayList<>();

				for (Individu ii : individus) {
					if (ii != i && !i.isLinkedTo(ii) && ii.getTailleListeContacts() < ii.getNbContactsSouhaites()) {
						disponibles.add(ii);
					}
				}
				// Plus personne avec qui créer un lien
				if (disponibles.isEmpty()) {
					break;
				}
				// On choisit quelqu'un au hasard parmi les disponibles
				Individu ii = disponibles.get((int) (Math.random() * disponibles.size()));
				i.addContact(ii);
			}
		}
	}
}
