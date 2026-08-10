package parametres;

public interface ParametresMaladie {
	
	public double probabiliteTransmission();
	public double probabiliteGuerison();
	//public double probabiliteDeces();
	
	public int dureeIncubation();
	public int dureeContagion();
}
