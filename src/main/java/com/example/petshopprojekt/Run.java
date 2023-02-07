package com.example.petshopprojekt;

import Entiteti.Korisnik;
import Entiteti.KupljeniProizvodi;
import Entiteti.Zaposlenik;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Run extends Application {
    private static Stage mainStage;
    private static Korisnik korisnik;
    private static Zaposlenik zaposlenik;
    private static Integer integer;
    private static Double zarada= (double) 0;
    private static final KupljeniProizvodi kupljeniProizvodi = new KupljeniProizvodi();



    @Override
    public void start(Stage stage) throws IOException {

        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
        Thread thread = new Thread(() -> {
            while (true) {
                var ref = new Object() {
                    Double adouble = (double) 0;
                };
                Run.getKupljeniProizvodi().dohvati().forEach(proizvodKorisnikPromjena ->
                        ref.adouble += proizvodKorisnikPromjena.getPromijenjen().getCijena() * proizvodKorisnikPromjena
                                .getPromijenjen().getKolicina());
                Run.setZarada(ref.adouble);


            }
        });
        thread.start();

    }

    public static void main(String[] args) {
        launch();
    }
    public static Stage getMainStage(){
        return mainStage;
    }

    public static void setKorisnik(Korisnik korisnik) {
        Run.korisnik = korisnik;
    }

    public static void setZaposlenik(Zaposlenik zaposlenik) {
        Run.zaposlenik = zaposlenik;
    }

    public static Korisnik getKorisnik() {
        return korisnik;
    }

    public static Zaposlenik getZaposlenik() {
        return zaposlenik;
    }



    public static Integer getInteger() {
        return integer;
    }

    public static void setInteger(Integer integer) {
        Run.integer = integer;
    }



    public static KupljeniProizvodi getKupljeniProizvodi() {
        return kupljeniProizvodi;
    }


    public static Double getZarada() {
        return zarada;
    }

    public static void setZarada(Double zarada) {
        Run.zarada = Run.zarada + zarada;
    }
}