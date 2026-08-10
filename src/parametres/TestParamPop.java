package parametres;

import java.util.Random;

public class TestParamPop implements ParametresPopulation {
	
	Random r = new Random();
	@Override
	public int taillePop() {
		return 10;
	}

	@Override
	public int ageIndividu() {
		return (int) (Math.random()*101);
	}

	@Override
	public int nbContacteSouhaite() {
		
		double moyenne = 4;
		double ecartType = 1.2;

		int nbContacts = (int) Math.round(
		    this.r.nextGaussian() * ecartType + moyenne
		);
		
		nbContacts = Math.max(0, nbContacts);
	    nbContacts = Math.min(taillePop() - 1, nbContacts);
	    
		return nbContacts;
	}
}
