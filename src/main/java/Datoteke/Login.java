package Datoteke;

import Entiteti.KorisnikBuilder;
import Entiteti.Osoba;
import Iznimke.LoginException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Login {

    private static final Integer BROJ_ZAPISA =2;

    public static List<Osoba> ucitajLozinke() throws LoginException {
        List<Osoba> osobe = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("Lozinke/Lozinke.txt"))) {
            List<String> datotekaLozinki  = bufferedReader.lines().toList();
            for (int i =0; i<datotekaLozinki.size()/BROJ_ZAPISA; i++){
                String korisnickoIme = datotekaLozinki.get(i*BROJ_ZAPISA);
                String sifra = datotekaLozinki.get(i*BROJ_ZAPISA+1);
                osobe.add(new KorisnikBuilder().setId(null).setIme(null).setPrezime(null)
                        .setKorisnickoIme(korisnickoIme).setLozinka(sifra).setDatumRodjenja(null)
                        .setAdresa(null).createKorisnik());
            }
        } catch (IOException e) {
            throw new LoginException();
        }

        return osobe;
    }

    public static String vratiLozinku(String korisnicko) throws LoginException {
        List<Osoba> osobe = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("Lozinke/Lozinke.txt"))) {
            List<String> datotekaLozinki  = bufferedReader.lines().toList();
            for (int i =0; i<datotekaLozinki.size()/BROJ_ZAPISA; i++){
                String korisnickoIme = datotekaLozinki.get(i*BROJ_ZAPISA);
                String sifra = datotekaLozinki.get(i*BROJ_ZAPISA+1);
                osobe.add(new KorisnikBuilder().setId(null).setIme(null).setPrezime(null)
                        .setKorisnickoIme(korisnickoIme).setLozinka(sifra).setDatumRodjenja(null)
                        .setAdresa(null).createKorisnik());
            }
        } catch (IOException e) {
            throw new LoginException();
        }

        String lozinka = null;
        for (Osoba osoba : osobe) {
            if (osoba.getKorisnickoIme().equals(korisnicko)) {
                lozinka = osoba.getLozinka();
            }
        }

        return lozinka;
    }
    public static void unesiLozinku(Osoba osoba) throws LoginException {
        List<Osoba> osobaList;
        try {
            osobaList = ucitajLozinke();
        } catch (LoginException e) {
            throw new LoginException();
        }
        osobaList.add(osoba);
        try(PrintWriter out = new PrintWriter(new FileWriter("Lozinke/Lozinke.txt"))) {

            for (Osoba value : osobaList) {
                out.println(value.getKorisnickoIme());
                out.println(value.getLozinka());
            }
        } catch (IOException e) {
            throw new LoginException();
        }

    }

    public static void izbrisiLozinku(Osoba osoba) throws LoginException {
        List<Osoba> osobaList = ucitajLozinke();
        for (int i=0;i<osobaList.size();i++){
            if (osobaList.get(i).getKorisnickoIme().equals(osoba.getKorisnickoIme())){
                osobaList.remove(i);
            }
        }
        try(PrintWriter out = new PrintWriter(new FileWriter("Lozinke/Lozinke.txt"))) {

            for (Osoba value : osobaList) {
                out.println(value.getKorisnickoIme());
                out.println(value.getLozinka());
            }
        } catch (IOException e) {
            throw new LoginException();
        }

    }

    public static void azurirajLozinku(Osoba staraOsoba, Osoba novaOsoba) throws LoginException {
        List<Osoba> osobaList = ucitajLozinke();
        osobaList.forEach(osoba1 -> {
            if (osoba1.getKorisnickoIme().equals(staraOsoba.getKorisnickoIme())) {
                osoba1.setKorisnickoIme(novaOsoba.getKorisnickoIme());
                osoba1.setLozinka(novaOsoba.getLozinka());
            }
        });
        try(PrintWriter out = new PrintWriter(new FileWriter("Lozinke/Lozinke.txt"))) {

            for (Osoba osoba : osobaList) {
                out.println(osoba.getKorisnickoIme());
                out.println(osoba.getLozinka());
            }
        } catch (IOException e) {
            throw new LoginException();
        }

    }
}
