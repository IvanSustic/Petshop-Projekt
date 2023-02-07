package Entiteti;

import java.io.File;
import java.time.LocalDate;

public final class Zaposlenik extends Osoba{
    private File slika;

    public Zaposlenik(Long id, String ime, String prezime,
                      String korisnickoIme, String lozinka, LocalDate datumRodjenja, File slika) {
        super(id, ime, prezime, korisnickoIme, lozinka, datumRodjenja);
        this.slika = slika;
    }

    public File getSlika() {
        return slika;
    }

    public void setSlika(File slika) {
        this.slika = slika;
    }
}
