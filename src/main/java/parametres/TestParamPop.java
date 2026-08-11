package parametres;

import java.util.Random;

import modele.Sexe;


public class TestParamPop implements IParametresPopulation {
	private int taillePop;
	Random r = new Random();
	
	public TestParamPop(int taillePop) {
		this.taillePop = taillePop;
	}
	@Override
	public int taillePop() {
		return this.taillePop;
	}
	
	public Sexe sexe() {
		if(Math.random()<0.50) return Sexe.FEMME;
		return Sexe.HOMME;
	}
    // Selon
	// https://fr.wikipedia.org/wiki/D%C3%A9mographie_de_la_France#:~:text=R%C3%A9partition%20par%20sexe%20et%20par%20%C3%A2gemodifiermodifier%20le%20code
	@Override
	public int ageIndividu() {
		double p = Math.random();
		if (p<=0.239) 
			return (int)(Math.random()*20); // 0 à 20 ans 
		if (p<=0.732)
			return 20 + (int)(Math.random()*40); // 20 à 59 ans
		if (p<=0.739) 
			return 60 + (int)(Math.random()*4); // 60 à 64 ans
		else 
			return 65 + (int) (Math.random()*30); // 65 ans et plus
	}

	// selon https://pmc.ncbi.nlm.nih.gov/articles/PMC4503306/
	//	Âge	Moyenne contacts	Écart-type
	//	0–2	4	1.2
	//	3–5	6	2
	//	6–17	11	3
	//	18–29	9	3
	//	30–49	8	3
	//	50–64	6	2
	//	65–79	4.5	2
	//	80+	3	1.5
	@Override
	public int nbContacteSouhaite(int age) {
		double moyenne;
		double ecartType;
		
		if (age <= 2) {
			moyenne = 4;
			ecartType = 1.2;
		}
		if (age <= 5) {
			moyenne = 6;
			ecartType = 2;
		}
		if (age <= 17) {
			moyenne = 11;
			ecartType = 3;
		}
		if (age <= 29) {
			moyenne = 9;
			ecartType = 3;
		}
		if (age <= 49) {
			moyenne = 8;
			ecartType = 3;
		}
		if (age <= 64) {
			moyenne = 6;
			ecartType = 2;
		}
		if (age <= 79) {
			moyenne = 4.5;
			ecartType = 2;
		} else {
			moyenne = 3.5;
			ecartType = 1.5;
		}

		int nbContacts = (int) Math.round(this.r.nextGaussian() * ecartType + moyenne);

		nbContacts = Math.max(0, nbContacts);
		nbContacts = Math.min(taillePop() - 1, nbContacts);

		return nbContacts;

	}
	
	
	// Diabète 
	// https://www.santepubliquefrance.fr/sites/default/files/rdd/images/image/repartition-par-age-et-sexe-de-la-prevalence-du-diabete-.png
	@Override
	public boolean isDiabetique(int age, Sexe sexe) {

	    double p;

	    if (age < 30) {
	        p = 0.005;
	    }
	    else if (age < 40) {
	        p = 0.01;
	    }
	    else if (age < 45) {
	        p = (sexe == Sexe.HOMME) ? 0.018 : 0.013;
	    }
	    else if (age < 50) {
	        p = (sexe == Sexe.HOMME) ? 0.032 : 0.025;
	    }
	    else if (age < 55) {
	        p = (sexe == Sexe.HOMME) ? 0.057 : 0.040;
	    }
	    else if (age < 60) {
	        p = (sexe == Sexe.HOMME) ? 0.093 : 0.063;
	    }
	    else if (age < 65) {
	        p = (sexe == Sexe.HOMME) ? 0.140 : 0.088;
	    }
	    else if (age < 70) {
	        p = (sexe == Sexe.HOMME) ? 0.178 : 0.108;
	    }
	    else if (age < 75) {
	        p = (sexe == Sexe.HOMME) ? 0.208 : 0.134;
	    }
	    else if (age < 80) {
	        p = (sexe == Sexe.HOMME) ? 0.210 : 0.144;
	    }
	    else if (age < 85) {
	        p = (sexe == Sexe.HOMME) ? 0.204 : 0.147;
	    }
	    else if (age < 90) {
	        p = (sexe == Sexe.HOMME) ? 0.174 : 0.129;
	    }
	    else {
	        p = (sexe == Sexe.HOMME) ? 0.120 : 0.089;
	    }

	    return Math.random() < p;
	}
}
