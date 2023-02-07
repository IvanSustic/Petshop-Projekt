package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
import Datoteke.Login;
import Entiteti.*;
import Iznimke.KriviUnosException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    TextField korisnickoIme;
    @FXML
    PasswordField lozinka;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);


    public void prikaziEkran(String file, String naslov) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource(file));
        Scene scene = new Scene(fxmlLoader.load(), 1536,800);
        Run.getMainStage().setTitle(naslov);
        Run.getMainStage().setScene(scene);
        Run.getMainStage().show();

    }

    public void login() {
        if (lozinka.getText().isBlank() || korisnickoIme.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else {
            try {
                List<Osoba> osobaList = Login.ucitajLozinke();

                Osoba osoba = null;
                for (Osoba osobaIterator : osobaList) {
                    if (osobaIterator.getKorisnickoIme().equals(korisnickoIme.getText()) &&
                            osobaIterator.getLozinka().equals(Metode.hashirajLozinku(lozinka.getText()))) {
                        osoba = osobaIterator;
                    }
                }
                if (osoba != null && !BazaPodataka.dovatiZaposlenike(new ZaposlenikBuilder()
                                .setId(null).setIme(null).setPrezime(null)
                                .setKorisnickoIme(osoba.getKorisnickoIme())
                                .setLozinka(osoba.getLozinka()).setDatumRodjenja(null)
                                .setSlika(null).createZaposlenik())
                        .isEmpty()) {
                    Run.setZaposlenik(BazaPodataka.dovatiZaposlenike(new ZaposlenikBuilder()
                            .setId(null).setIme(null).setPrezime(null)
                            .setKorisnickoIme(osoba.getKorisnickoIme())
                            .setLozinka(osoba.getLozinka()).setDatumRodjenja(null)
                            .setSlika(null).createZaposlenik()).get(0));
                    prikaziEkran("PocetnaZaposlenik.fxml", "Početna stranica");
                } else if (osoba != null && !BazaPodataka.dohvatiKorisnike(new KorisnikBuilder()
                                .setId(null).setIme(null).setPrezime(null)
                                .setKorisnickoIme(osoba.getKorisnickoIme())
                                .setLozinka(osoba.getLozinka()).setDatumRodjenja(null)
                                .setAdresa(null).createKorisnik())
                        .isEmpty()) {
                    Run.setKorisnik(BazaPodataka.dohvatiKorisnike(new KorisnikBuilder()
                            .setId(null).setIme(null).setPrezime(null)
                            .setKorisnickoIme(osoba.getKorisnickoIme())
                            .setLozinka(osoba.getLozinka()).setDatumRodjenja(null)
                            .setAdresa(null).createKorisnik()).get(0));
                    Run.setInteger(7);
                    prikaziEkran("KorisnikPocetna.fxml", "Početna stranica");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText("Ne postoji takav korisnik");
                    alert.setContentText("Molimo vas da provjerite unesene podatke.");
                    alert.showAndWait();
                    throw new KriviUnosException();
                }


            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }


}