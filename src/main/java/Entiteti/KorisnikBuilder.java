package Entiteti;

import java.time.LocalDate;

public class KorisnikBuilder {
    private Long id;
    private String ime;
    private String prezime;
    private String korisnickoIme;
    private String lozinka;
    private LocalDate datumRodjenja;
    private String adresa;

    public KorisnikBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public KorisnikBuilder setIme(String ime) {
        this.ime = ime;
        return this;
    }

    public KorisnikBuilder setPrezime(String prezime) {
        this.prezime = prezime;
        return this;
    }

    public KorisnikBuilder setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
        return this;
    }

    public KorisnikBuilder setLozinka(String lozinka) {
        this.lozinka = lozinka;
        return this;
    }

    public KorisnikBuilder setDatumRodjenja(LocalDate datumRodjenja) {
        this.datumRodjenja = datumRodjenja;
        return this;
    }

    public KorisnikBuilder setAdresa(String adresa) {
        this.adresa = adresa;
        return this;
    }

    public Korisnik createKorisnik() {
        return new Korisnik(id, ime, prezime, korisnickoIme, lozinka, datumRodjenja, adresa);
    }
}