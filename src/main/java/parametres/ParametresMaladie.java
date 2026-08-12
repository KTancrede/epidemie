package parametres;

public class ParametresMaladie implements IParametresMaladie {

    private double probabiliteTransmission;
    private double probabiliteGuerison;

    private int dureeIncubation;
    private int dureeContagion;

    private double facteurRisqueDiabete;
    private double probabiliteReinfection;

    public ParametresMaladie(
            double probabiliteTransmission,
            double probabiliteGuerison,
            int dureeIncubation,
            int dureeContagion,
            double facteurRisqueDiabete,
            double probabiliteReinfection) {

        this.probabiliteTransmission = probabiliteTransmission;
        this.probabiliteGuerison = probabiliteGuerison;

        this.dureeIncubation = dureeIncubation;
        this.dureeContagion = dureeContagion;

        this.facteurRisqueDiabete = facteurRisqueDiabete;
        this.probabiliteReinfection = probabiliteReinfection;
    }

    @Override
    public double probabiliteTransmission() {
        return probabiliteTransmission;
    }

    @Override
    public double probabiliteGuerison() {
        return probabiliteGuerison;
    }

    @Override
    public int dureeIncubation() {
        return dureeIncubation;
    }

    @Override
    public int dureeContagion() {
        return dureeContagion;
    }

    @Override
    public double facteurRisqueDiabete() {
        return facteurRisqueDiabete;
    }

    @Override
    public double probabiliteReinfection() {
        return probabiliteReinfection;
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
}