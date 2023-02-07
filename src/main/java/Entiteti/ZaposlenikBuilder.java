package Entiteti;

import java.io.File;
import java.time.LocalDate;

public class ZaposlenikBuilder {
    private Long id;
    private String ime;
    private String prezime;
    private String korisnickoIme;
    private String lozinka;
    private LocalDate datumRodjenja;
    private File slika;

    public ZaposlenikBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public ZaposlenikBuilder setIme(String ime) {
        this.ime = ime;
        return this;
    }

    public ZaposlenikBuilder setPrezime(String prezime) {
        this.prezime = prezime;
        return this;
    }

    public ZaposlenikBuilder setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
        return this;
    }

    public ZaposlenikBuilder setLozinka(String lozinka) {
        this.lozinka = lozinka;
        return this;
    }

    public ZaposlenikBuilder setDatumRodjenja(LocalDate datumRodjenja) {
        this.datumRodjenja = datumRodjenja;
        return this;
    }

    public ZaposlenikBuilder setSlika(File slika) {
        this.slika = slika;
        return this;
    }

    public Zaposlenik createZaposlenik() {
        return new Zaposlenik(id, ime, prezime, korisnickoIme, lozinka, datumRodjenja, slika);
    }
}