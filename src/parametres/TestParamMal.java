package parametres;

public class TestParamMal implements ParametresMaladie{

	@Override
	public double probabiliteTransmission() {
		return 0;
	}

	@Override
	public double probabiliteGuerison() {
		return 0.95;
	}

	@Override
	public int dureeIncubation() {
		return 5;
	}

	@Override
	public int dureeContagion() {
		return 4;
	}

}
