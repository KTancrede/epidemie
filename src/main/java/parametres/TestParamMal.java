package parametres;

public class TestParamMal implements IParametresMaladie{

	@Override
	public double probabiliteTransmission() {
		return 0.15;
	}

	@Override
	public double probabiliteGuerison() {
		return 0.85;
	}

	@Override
	public int dureeIncubation() {
		return 5;
	}

	@Override
	public int dureeContagion() {
		return 4;
	}
	
	@Override
	public double facteurRisqueAge(int age) {

	    if (age < 1) {
	        return 2.0;
	    } else if (age < 20) {
	        return 0.2;
	    } else if (age < 40) {
	        return 0.5;
	    } else if (age < 50) {
	        return 1.0;
	    } else if (age < 65) {
	        return 2.0;
	    } else if (age < 75) {
	        return 4.0;
	    } else if (age < 85) {
	        return 8.0;
	    } else {
	        return 12.0;
	    }
	}
	
	@Override
	public double facteurRisqueDiabete() {
		return 1.3;
	}
	@Override
	public double probabiliteReinfection() {
	    return 0.20;
	}
}
