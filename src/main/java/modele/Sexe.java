package main.java.modele;

public enum Sexe {
	FEMME, HOMME;
	
	@Override
    public String toString() {
        switch (this) {
        case FEMME:
            return " F ";
        case HOMME:
            return " H ";
        default:
            return "";
        }
    }
}
