package parametres;

public interface IParametresMaladie {
	
	public double probabiliteTransmission();
	public double probabiliteGuerison();
	//public double probabiliteDeces();
	
	public int dureeIncubation();
	public int dureeContagion();
	public double facteurRisqueAge(int age);
	public double facteurRisqueDiabete();
}
