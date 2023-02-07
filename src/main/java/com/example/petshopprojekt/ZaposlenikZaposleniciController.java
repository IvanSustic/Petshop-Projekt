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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZaposlenikZaposleniciController {
    @FXML
    TextField ime,prezime,korisnicko;
    @FXML
    PasswordField lozinka,lozinka1;
    @FXML
    DatePicker datum;
    @FXML
    ImageView slika;

    @FXML
    TableView<Zaposlenik> tablica;
    @FXML
    TableColumn<Zaposlenik,String> imeTablica,prezimeTablica,datumTablica,korisnickoTablica;
    private List<Zaposlenik> zaposlenikList;
    private Zaposlenik odabranZaposlenik;
    private File slikaFile;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    private final Stage stage = Run.getMainStage();
    public void initialize(){

        try {
            zaposlenikList = BazaPodataka.vratiSveZaposlenike();
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


        tablica.setItems(FXCollections.observableArrayList(zaposlenikList));

        imeTablica.prefWidthProperty().setValue(1313/4);
        prezimeTablica.prefWidthProperty().setValue(1313/4);
        korisnickoTablica.prefWidthProperty().setValue(1313/4);
        datumTablica.prefWidthProperty().setValue(1313/4);
        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                imeTablica.prefWidthProperty().setValue(1313/4);
                prezimeTablica.prefWidthProperty().setValue(1313/4);
                korisnickoTablica.prefWidthProperty().setValue(1313/4);
                datumTablica.prefWidthProperty().setValue(1313/4);
            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){

                imeTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                prezimeTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                korisnickoTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                datumTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
            }

        });


        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (Run.getInteger().equals(2)){
                Platform.runLater(()->{
                    tablica.getItems().clear();
                    tablica.setItems(FXCollections.observableArrayList(zaposlenikList));
                });
            }else {
                scheduledExecutorService.shutdown();
            }

        },1,1, TimeUnit.SECONDS);
    }



    public void odaberiSliku() {
        slikaFile = Metode.odaberiSliku();
        slika.setImage(new Image(slikaFile.toURI().toString()));

    }

    public void unesiZaposlenika() {

        if (ime.getText().isBlank() || prezime.getText().isBlank()
                || lozinka.getText().isBlank() || lozinka1.getText().isBlank() || (slikaFile == null)
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
            alert.setContentText("Molimimo vas da provjerite lozinke.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {
            try {
                if (Login.ucitajLozinke().stream().anyMatch(osoba ->
                        osoba.getKorisnickoIme().equals(korisnicko.getText()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Korisnik postoji");
                    alert.setHeaderText("Korisnikčko ime več postoji.");
                    alert.setContentText("Unesite drugo korisničko ime.");
                    alert.showAndWait();
                    throw new KriviUnosException();
                } else {
                    try {
                        Zaposlenik noviZaposlenik = new ZaposlenikBuilder().setId(null)
                                .setIme(ime.getText()).setPrezime(prezime.getText())
                                .setKorisnickoIme(korisnicko.getText())
                                .setLozinka(Metode.hashirajLozinku(lozinka.getText()))
                                .setDatumRodjenja(datum.getValue()).setSlika(slikaFile).createZaposlenik();
                        BazaPodataka.spremiNovogZaposlenika(noviZaposlenik);
                        Serijalizacija<Promjena<Zaposlenik, Zaposlenik>> promjenaSerijalizacija
                                = new Serijalizacija<>();
                        Promjena<Zaposlenik, Zaposlenik> promjena = new PromjenaBuilder()
                                .setPocetni(null).setPromijenjen(noviZaposlenik)
                                .setRola(Run.getZaposlenik()).createPromjena();
                        promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/zaposleniciPromjene.dat");
                        Login.unesiLozinku(noviZaposlenik);
                        zaposlenikList = BazaPodataka.vratiSveZaposlenike();
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

    public void izbrisiZaposlenika() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Ažuriranje");
        alert.setContentText("Jeste li sigurni da želite izbrisati zaposlenika.");
        Optional<ButtonType> rezultat = alert.showAndWait();
        if (rezultat.isPresent()){

        if(rezultat.get() == ButtonType.OK) {
            try {
                BazaPodataka.obrisiZaposlenika(odabranZaposlenik);
                Login.izbrisiLozinku(odabranZaposlenik);
                zaposlenikList = BazaPodataka.vratiSveZaposlenike();
            } catch (IOException | SQLException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }

        }
        }
    }

    public void azurirajZaposlenika() {

        if (ime.getText().isBlank() || prezime.getText().isBlank()
                || (slikaFile == null)
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
            alert.setContentText("Molimo vas da provjerite lozinke.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {
            try {
                if (Login.ucitajLozinke().stream().anyMatch(osoba ->
                        osoba.getKorisnickoIme().equals(korisnicko.getText()))
                && !odabranZaposlenik.getKorisnickoIme().equals(korisnicko.getText())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Korisnik postoji");
                    alert.setHeaderText("Korisnikčko ime več postoji.");
                    alert.setContentText("Unesite drugo korisničko ime.");
                    alert.showAndWait();
                    throw new KriviUnosException();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Ažuriranje");
                    alert.setHeaderText("Ažuriranje");
                    alert.setContentText("Jeste li sigurni da želite ažurirati zaposlenika.");
                    Optional<ButtonType> rezultat = alert.showAndWait();
                    if (rezultat.isPresent()){


                    if(rezultat.get() == ButtonType.OK) {
                        try {

                            String novaLozinka;
                            if (lozinka1.getText().isBlank() && lozinka.getText().isBlank()){
                                novaLozinka = odabranZaposlenik.getLozinka();
                            } else {
                                novaLozinka = Metode.hashirajLozinku(lozinka.getText());
                            }
                            Zaposlenik noviZaposlenik = new ZaposlenikBuilder()
                                    .setId(odabranZaposlenik.getId())
                                    .setIme(ime.getText()).setPrezime(prezime.getText())
                                    .setKorisnickoIme(korisnicko.getText()).setLozinka(novaLozinka)
                                    .setDatumRodjenja(datum.getValue()).setSlika(slikaFile).createZaposlenik();

                            BazaPodataka.azurirajZaposlenika(noviZaposlenik,
                                    slikaFile.getName().equals(odabranZaposlenik.getSlika().getName()));
                            Login.azurirajLozinku(odabranZaposlenik, noviZaposlenik);
                            if (odabranZaposlenik.getKorisnickoIme().equals(Run.getZaposlenik().getKorisnickoIme())){
                                Run.setZaposlenik(noviZaposlenik);
                            }
                            Serijalizacija<Promjena<Zaposlenik, Zaposlenik>> promjenaSerijalizacija
                                    = new Serijalizacija<>();
                            Promjena<Zaposlenik, Zaposlenik> promjena = new PromjenaBuilder()
                                    .setPocetni(odabranZaposlenik).setPromijenjen(noviZaposlenik)
                                    .setRola(Run.getZaposlenik()).createPromjena();
                            promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/zaposleniciPromjene.dat");

                            zaposlenikList = BazaPodataka.vratiSveZaposlenike();
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
            zaposlenikList = BazaPodataka.dovatiZaposlenike(new ZaposlenikBuilder()
                    .setId(null).setIme(ime.getText()).setPrezime(prezime.getText())
                    .setKorisnickoIme(korisnicko.getText()).setLozinka(null).setDatumRodjenja(localDate)
                    .setSlika(slikaFile).createZaposlenik());
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }



    }

    public void odaberiZaposlenika() {
        odabranZaposlenik = zaposlenikList.get(tablica.getSelectionModel().getSelectedIndex());
        slikaFile =odabranZaposlenik.getSlika();
        ime.setText(odabranZaposlenik.getIme());
        prezime.setText(odabranZaposlenik.getPrezime());
        korisnicko.setText(odabranZaposlenik.getKorisnickoIme());
        lozinka.setText("");
        lozinka1.setText("");
        datum.setValue(odabranZaposlenik.getDatumRodjenja());
        slika.setImage(new Image(slikaFile.toURI().toString()));
    }

    public void makniSliku() {
        slikaFile= null;
        slika.setImage(null);
    }
}
