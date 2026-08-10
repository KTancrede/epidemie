package modele;

import java.util.ArrayList;
import java.util.HashSet;

import parametres.ParametresPopulation;

public class Population {

	private ParametresPopulation param;
	private int taillePop;
	private ArrayList<Individu> individus = new ArrayList<>();

	public Population(ParametresPopulation param) {
		this.param = param;
		this.taillePop = param.taillePop();

		for (int i = 0; i < this.taillePop; i++) {
			individus.add(new Individu(param.ageIndividu(), EtatSante.SAIN, param.nbContacteSouhaite()));
		}
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

	public void patientZero() {
		int z = (int) (Math.random() * taillePop);

		Individu patientZero = individus.get(z);
	    patientZero.setMalade(0);

	    System.out.println(patientZero);

	}
}
