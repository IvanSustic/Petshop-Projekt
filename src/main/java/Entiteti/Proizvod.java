package Entiteti;

import java.io.File;

public class Proizvod extends Entitet{
    private final String naziv;
    private String opis;
    private Integer kolicina;
    private final Double cijena;
    private File slika;

    public Proizvod(Long id, String naziv, String opis, Integer kolicina, Double cijena, File slika) {
        super(id);
        this.naziv = naziv;
        this.opis = opis;
        this.kolicina = kolicina;
        this.cijena = cijena;
        this.slika = slika;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Integer getKolicina() {
        return kolicina;
    }

    public void setKolicina(Integer kolicina) {
        this.kolicina = kolicina;
    }

    public Double getCijena() {
        return cijena;
    }

    public File getSlika() {
        return slika;
    }

    public void setSlika(File slika) {
        this.slika = slika;
    }
}
