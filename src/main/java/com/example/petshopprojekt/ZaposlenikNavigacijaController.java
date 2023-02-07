package com.example.petshopprojekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZaposlenikNavigacijaController {
    @FXML
    ImageView slika;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    public void initialize(){
        slika.setImage(new Image(Run.getZaposlenik().getSlika().toURI().toString()));
    }

    public void prikaziEkran(String file, String naslov) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource(file));
        Scene scene = new Scene(fxmlLoader.load(), 1536,800);
        Run.getMainStage().setTitle(naslov);
        Run.getMainStage().setScene(scene);
        Run.getMainStage().show();

    }
    public void odjava() {
        try {
            prikaziEkran("Login.fxml","Login");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(0);
    }

    public void pikraziPromjeneScreen() {
        try {
            prikaziEkran("Promjene.fxml","Promjene");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(1);
    }

    public void pikraziZaposleniciScreen() {
        try {
            prikaziEkran("ZaposlenikZaposlenici.fxml","Zaposlenici");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(2);
    }

    public void pikraziKorisniciScreen() {
        try {
            prikaziEkran("ZaposlenikKorisnici.fxml","Korisnici");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(3);
    }

    public void pikraziZivotinjeScreen() {
        try {
            prikaziEkran("ZaposlenikZivotinje.fxml","Životinje");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(4);
    }

    public void pikraziProizvodiScreen() {
        try {
            prikaziEkran("ZaposlenikProizvodi.fxml","Proizvodi");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(5);
    }

    public void pikraziPocetniScreen() {
        try {
            prikaziEkran("PocetnaZaposlenik.fxml","Početna");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(6);
    }
}
