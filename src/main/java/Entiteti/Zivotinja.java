package Entiteti;

import java.io.File;

public class Zivotinja extends Entitet {
    private final String pasmina;
    private final Integer godine;
    private Spol spol;
    private Vrsta vrsta;
    private File slika;
    private final String kontakt;

    public Zivotinja(Long id, String pasmina, Integer godine, Spol spol, Vrsta vrsta, File slika, String kontakt) {
        super(id);
        this.pasmina = pasmina;
        this.godine = godine;
        this.spol = spol;
        this.vrsta = vrsta;
        this.slika = slika;
        this.kontakt = kontakt;
    }

    public String getPasmina() {
        return pasmina;
    }

    public Integer getGodine() {
        return godine;
    }

    public Spol getSpol() {
        return spol;
    }

    public void setSpol(Spol spol) {
        this.spol = spol;
    }

    public Vrsta getVrsta() {
        return vrsta;
    }

    public void setVrsta(Vrsta vrsta) {
        this.vrsta = vrsta;
    }

    public File getSlika() {
        return slika;
    }

    public void setSlika(File slika) {
        this.slika = slika;
    }

    public String getKontakt() {
        return kontakt;
    }

}
