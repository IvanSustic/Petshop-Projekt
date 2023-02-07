package Entiteti;

public enum Vrsta {
    PAS("Pas",0),MACKA("Mačka",1),ZAMORAC("Zamorac",2),GMAZ("Gmaz",3),
    PTICA("Ptica",4);

    private final String opis;
    private final Integer vrijednost;

    Vrsta(String opis, Integer vrijednost) {
        this.opis = opis;
        this.vrijednost = vrijednost;
    }

    public String getOpis() {
        return opis;
    }

    public Integer getVrijednost() {
        return vrijednost;
    }
}
