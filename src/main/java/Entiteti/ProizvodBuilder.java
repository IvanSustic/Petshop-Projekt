package Entiteti;

import java.io.File;

public class ProizvodBuilder {
    private Long id;
    private String naziv;
    private String opis;
    private Integer kolicina;
    private Double cijena;
    private File slika;

    public ProizvodBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public ProizvodBuilder setNaziv(String naziv) {
        this.naziv = naziv;
        return this;
    }

    public ProizvodBuilder setOpis(String opis) {
        this.opis = opis;
        return this;
    }

    public ProizvodBuilder setKolicina(Integer kolicina) {
        this.kolicina = kolicina;
        return this;
    }

    public ProizvodBuilder setCijena(Double cijena) {
        this.cijena = cijena;
        return this;
    }

    public ProizvodBuilder setSlika(File slika) {
        this.slika = slika;
        return this;
    }

    public Proizvod createProizvod() {
        return new Proizvod(id, naziv, opis, kolicina, cijena, slika);
    }
}