package Entiteti;

import java.time.LocalDate;

public abstract sealed class Osoba extends Entitet permits Korisnik, Zaposlenik {
    private String ime,prezime,korisnickoIme,lozinka;
    private final LocalDate datumRodjenja;

    public Osoba(Long id, String ime, String prezime, String korisnickoIme, String lozinka, LocalDate datumRodjenja) {
        super(id);
        this.ime = ime;
        this.prezime = prezime;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.datumRodjenja = datumRodjenja;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public LocalDate getDatumRodjenja() {
        return datumRodjenja;
    }

}
