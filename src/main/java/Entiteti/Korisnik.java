package Entiteti;

import java.time.LocalDate;

public final class Korisnik extends Osoba{
    private final String adresa;

    public Korisnik(Long id, String ime, String prezime, String korisnickoIme,
                    String lozinka, LocalDate datumRodjenja, String adresa) {
        super(id, ime, prezime, korisnickoIme, lozinka, datumRodjenja);
        this.adresa = adresa;
    }

    public String getAdresa() {
        return adresa;
    }

}
