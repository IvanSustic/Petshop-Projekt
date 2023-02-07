package Entiteti;

import java.io.File;

public class ZivotinjaBuilder {
    private Long id;
    private String pasmina;
    private Integer godine;
    private Spol spol;
    private Vrsta vrsta;
    private File slika;
    private String kontakt;

    public ZivotinjaBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public ZivotinjaBuilder setPasmina(String pasmina) {
        this.pasmina = pasmina;
        return this;
    }

    public ZivotinjaBuilder setGodine(Integer godine) {
        this.godine = godine;
        return this;
    }

    public ZivotinjaBuilder setSpol(Spol spol) {
        this.spol = spol;
        return this;
    }

    public ZivotinjaBuilder setVrsta(Vrsta vrsta) {
        this.vrsta = vrsta;
        return this;
    }

    public ZivotinjaBuilder setSlika(File slika) {
        this.slika = slika;
        return this;
    }

    public ZivotinjaBuilder setKontakt(String kontakt) {
        this.kontakt = kontakt;
        return this;
    }

    public Zivotinja createZivotinja() {
        return new Zivotinja(id, pasmina, godine, spol, vrsta, slika, kontakt);
    }
}