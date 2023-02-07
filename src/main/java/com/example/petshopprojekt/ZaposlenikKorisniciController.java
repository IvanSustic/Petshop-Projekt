package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
import Datoteke.Login;
import Entiteti.*;
import Iznimke.BazaPodatakaException;
import Iznimke.KriviUnosException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZaposlenikKorisniciController {
    @FXML
    TextField ime,prezime,korisnicko,adresa;
    @FXML
    PasswordField lozinka,lozinka1;
    @FXML
    DatePicker datum;

    @FXML
    TableView<Korisnik> tablica;
    @FXML
    TableColumn<Korisnik,String> imeTablica,prezimeTablica,datumTablica,korisnickoTablica,adresaTablica;
    private List<Korisnik> korisnikList;
    private Korisnik odabranKorisnik;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    private final Stage stage = Run.getMainStage();
    public void initialize(){

        try {
            korisnikList = BazaPodataka.vratiSveKorisnike();
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }

        imeTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getIme()));
        prezimeTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getPrezime()));
        datumTablica.setCellValueFactory(
                zaposlenik -> {
                    SimpleStringProperty property = new
                            SimpleStringProperty();
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                    property.setValue(
                            zaposlenik.getValue().getDatumRodjenja().format(formatter));
                    return property;
                });
        korisnickoTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKorisnickoIme())));
        adresaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getAdresa())));
        tablica.setItems(FXCollections.observableArrayList(korisnikList));

        imeTablica.prefWidthProperty().setValue(1313/5);
        prezimeTablica.prefWidthProperty().setValue(1313/5);
        korisnickoTablica.prefWidthProperty().setValue(1313/5);
        datumTablica.prefWidthProperty().setValue(1313/5);
        adresaTablica.prefWidthProperty().setValue(1313/5);
        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                imeTablica.prefWidthProperty().setValue(1313/5);
                prezimeTablica.prefWidthProperty().setValue(1313/5);
                korisnickoTablica.prefWidthProperty().setValue(1313/5);
                datumTablica.prefWidthProperty().setValue(1313/5);
                adresaTablica.prefWidthProperty().setValue(1313/5);
            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){

                imeTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                prezimeTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                korisnickoTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                datumTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                adresaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
            }

        });

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (Run.getInteger().equals(3)){
                Platform.runLater(()->{
                    tablica.getItems().clear();
                    tablica.setItems(FXCollections.observableArrayList(korisnikList));
                });
            }else {
                scheduledExecutorService.shutdown();
            }

        },1,1, TimeUnit.SECONDS);

    }




    public void unesiKorisnika() {

        if (ime.getText().isBlank() || prezime.getText().isBlank() || adresa.getText().isBlank()
                || lozinka.getText().isBlank() || lozinka1.getText().isBlank()
                || datum.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else if (!lozinka.getText().equals(lozinka1.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Lozinke se ne podudaraju");
            alert.setContentText("Molmimo vas da provjerite lozinke.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {
            try {
                if (Login.ucitajLozinke().stream().anyMatch(osoba ->
                        osoba.getKorisnickoIme().equals(korisnicko.getText()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Korisnik postoji");
                    alert.setHeaderText("Korisnikčko več ime postoji.");
                    alert.setContentText("Unesite drugo korisničko ime.");
                    alert.showAndWait();
                    throw new KriviUnosException();
                } else {
                    try {
                        Korisnik noviKorisnik = new KorisnikBuilder().setId(null)
                                .setIme(ime.getText()).setPrezime(prezime.getText())
                                .setKorisnickoIme(korisnicko.getText())
                                .setLozinka(Metode.hashirajLozinku(lozinka.getText()))
                                .setDatumRodjenja(datum.getValue()).setAdresa(adresa.getText()).createKorisnik();
                        BazaPodataka.spremiNovogKorisnika(noviKorisnik);
                        Serijalizacija<Promjena<Korisnik, Zaposlenik>> promjenaSerijalizacija
                                = new Serijalizacija<>();
                        Promjena<Korisnik, Zaposlenik> promjena = new PromjenaBuilder()
                                .setPocetni(null).setPromijenjen(noviKorisnik)
                                .setRola(Run.getZaposlenik()).createPromjena();
                        promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/korisniciPromjene.dat");
                        Login.unesiLozinku(noviKorisnik);
                        korisnikList = BazaPodataka.vratiSveKorisnike();
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                        throw new RuntimeException(e);
                    }


                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }

    public void izbrisiKorisnika() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Brisanje");
        alert.setContentText("Jeste li sigurni da želite obrisati korisnika.");
        Optional<ButtonType> rezultat = alert.showAndWait();
        if (rezultat.isPresent()){
        if(rezultat.get() == ButtonType.OK) {
            try {
                BazaPodataka.obrisiKorisnika(odabranKorisnik);
                Login.izbrisiLozinku(odabranKorisnik);
                korisnikList = BazaPodataka.vratiSveKorisnike();
            } catch (IOException | SQLException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        }
    }

    public void azurirajKorisnika() {

        if (ime.getText().isBlank() || prezime.getText().isBlank()
                 || adresa.getText().isBlank()
                || datum.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else if (!lozinka.getText().equals(lozinka1.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Lozinke se ne podudaraju");
            alert.setContentText("Molmimo vas da provjerite lozinke.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {
            try {
                if (Login.ucitajLozinke().stream().anyMatch(osoba -> osoba.getKorisnickoIme().equals(korisnicko.getText()))
                        && !odabranKorisnik.getKorisnickoIme().equals(korisnicko.getText())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Korisnik postoji");
                    alert.setHeaderText("Korisnikčko več ime postoji.");
                    alert.setContentText("Unesite drugo korisničko ime.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Ažuriranje");
                    alert.setHeaderText("Ažuriranje");
                    alert.setContentText("Jeste li sigurni da želite ažurirati korisnika.");
                    Optional<ButtonType> rezultat = alert.showAndWait();
                    if (rezultat.isPresent()) {
                        if (rezultat.get() == ButtonType.OK) {
                            try {
                                String novaLozinka;
                                if (lozinka1.getText().isBlank() && lozinka.getText().isBlank()) {
                                    novaLozinka = odabranKorisnik.getLozinka();
                                } else {
                                    novaLozinka = Metode.hashirajLozinku(lozinka.getText());
                                }

                                Korisnik noviKorisnik = new KorisnikBuilder()
                                        .setId(odabranKorisnik.getId()).setIme(ime.getText())
                                        .setPrezime(prezime.getText()).setKorisnickoIme(korisnicko.getText())
                                        .setLozinka(novaLozinka).setDatumRodjenja(datum.getValue())
                                        .setAdresa(adresa.getText()).createKorisnik();
                                BazaPodataka.azurirajKorisnika(noviKorisnik);
                                Serijalizacija<Promjena<Korisnik, Zaposlenik>> promjenaSerijalizacija
                                        = new Serijalizacija<>();
                                Promjena<Korisnik, Zaposlenik> promjena = new PromjenaBuilder()
                                        .setPocetni(odabranKorisnik).setPromijenjen(noviKorisnik)
                                        .setRola(Run.getZaposlenik()).createPromjena();
                                promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/korisniciPromjene.dat");
                                Login.azurirajLozinku(odabranKorisnik, noviKorisnik);
                                korisnikList = BazaPodataka.vratiSveKorisnike();
                            } catch (IOException e) {
                                logger.info(e.getMessage());
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }

    public void filtriraj() {
        try {
            LocalDate localDate = null;
            if (datum !=null){
                localDate = datum.getValue();
            }
            korisnikList = BazaPodataka.dohvatiKorisnike(new KorisnikBuilder().setId(null)
                    .setIme(ime.getText()).setPrezime(prezime.getText())
                    .setKorisnickoIme(korisnicko.getText()).setLozinka(null)
                    .setDatumRodjenja(localDate).setAdresa(adresa.getText()).createKorisnik());
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void odaberiKorisnika() {
        odabranKorisnik = korisnikList.get(tablica.getSelectionModel().getSelectedIndex());
        ime.setText(odabranKorisnik.getIme());
        prezime.setText(odabranKorisnik.getPrezime());
        korisnicko.setText(odabranKorisnik.getKorisnickoIme());
        lozinka.setText("");
        lozinka1.setText("");
        datum.setValue(odabranKorisnik.getDatumRodjenja());
        adresa.setText(odabranKorisnik.getAdresa());
    }


}
