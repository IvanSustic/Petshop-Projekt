package com.example.petshopprojekt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class KorisnikNavigacijaController {
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
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

    public void pikraziZivotinjeScreen() {
        try {
            prikaziEkran("KorisnikZivotinje.fxml","Proizvodi");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(8);
    }

    public void pikraziProizvodiScreen() {
        try {
            prikaziEkran("KorisnikProizvodi.fxml","Proizvodi");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(9);
    }

    public void pikraziPocetniScreen() {
        try {
            prikaziEkran("KorisnikPocetna.fxml","Početna");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        Run.setInteger(7);
    }

}
