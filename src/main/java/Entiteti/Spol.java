package Entiteti;

public enum Spol {
    M("Muško"),F("Žensko");

    private final String opis;

    Spol(String opis) {
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }

}
