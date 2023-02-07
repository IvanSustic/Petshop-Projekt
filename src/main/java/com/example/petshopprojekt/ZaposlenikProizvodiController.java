package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZaposlenikProizvodiController {

    @FXML
    TextField naziv,cijena,kolicina;
    @FXML
    TextArea opis;
    @FXML
    ImageView slika;

    @FXML
    TableView<Proizvod> tablica;
    @FXML
    TableColumn<Proizvod,String>  nazivTablica,cijenaTablica,kolicinaTablica,opisTablica;
    private List<Proizvod> proizvodList;
    private Proizvod odabranProizvod;
    private File slikaFile;
    private final Stage stage = Run.getMainStage();

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);

    public void initialize(){

        try {
            proizvodList = BazaPodataka.vratiSveProizvode();
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }

        nazivTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getNaziv()));
        opisTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getOpis()));
        cijenaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty((df.format(celldata.getValue().getCijena()))+" €"));
        kolicinaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKolicina())));


        tablica.setItems(FXCollections.observableArrayList(proizvodList));

        nazivTablica.prefWidthProperty().setValue(1313/6);
        opisTablica.prefWidthProperty().setValue(1313/2);
        cijenaTablica.prefWidthProperty().setValue(1313/6);
        kolicinaTablica.prefWidthProperty().setValue(1313/6);
        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                nazivTablica.prefWidthProperty().setValue(1313/6);
                opisTablica.prefWidthProperty().setValue(1313/2);
                cijenaTablica.prefWidthProperty().setValue(1313/6);
                kolicinaTablica.prefWidthProperty().setValue(1313/6);

            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){

                nazivTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                opisTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/2.1);
                cijenaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                kolicinaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
            }

        });


        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (Run.getInteger().equals(5)){
                Platform.runLater(()->{
                    tablica.getItems().clear();
                    tablica.setItems(FXCollections.observableArrayList(proizvodList));
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

    public void unesiProizvod() {
        if (naziv.getText().isBlank() || cijena.getText().isBlank()
                || opis.getText().isBlank() || kolicina.getText().isBlank() || (slikaFile == null)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriBroj(kolicina.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravan broj");
            alert.setHeaderText("Niste unijeli dobru količinu.");
            alert.setContentText("Provjerite unesenu količinu.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriDouble(cijena.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravna cijena");
            alert.setHeaderText("Niste unijeli dobru cijenu.");
            alert.setContentText("Provjerite unesenu cijenu.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {

            try {
                Proizvod proizvod = new ProizvodBuilder().setId(null)
                        .setNaziv(naziv.getText()).setOpis(opis.getText())
                        .setKolicina(Integer.parseInt(kolicina.getText()))
                        .setCijena(Double.parseDouble(cijena.getText())).setSlika(slikaFile).createProizvod();
                BazaPodataka.spremiNoviProizvod(proizvod);
                Serijalizacija<Promjena<Proizvod, Zaposlenik>> promjenaSerijalizacija = new Serijalizacija<>();
                Promjena<Proizvod, Zaposlenik> promjena = new PromjenaBuilder()
                        .setPocetni(null).setPromijenjen(proizvod)
                        .setRola(Run.getZaposlenik()).createPromjena();
                promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/proizvodiPromjene.dat");
                proizvodList = BazaPodataka.vratiSveProizvode();
            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }


        }

    }

    public void izbrisiProizvod() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Ažuriranje");
        alert.setContentText("Jeste li sigurni da želite izbrisati proizvod.");
        Optional<ButtonType> rezultat = alert.showAndWait();
        if (rezultat.isPresent()){


        if(rezultat.get() == ButtonType.OK) {
            try {
                BazaPodataka.obrisiProizvod(odabranProizvod);
                proizvodList = BazaPodataka.vratiSveProizvode();
            } catch (IOException | SQLException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }

        }
        }
    }

    public void azurirajProizvod() {
        if (naziv.getText().isBlank() || cijena.getText().isBlank()
                || opis.getText().isBlank() || kolicina.getText().isBlank() || (slikaFile == null)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriBroj(kolicina.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravan broj");
            alert.setHeaderText("Niste unijeli dobru količinu.");
            alert.setContentText("Provjerite unesenu količinu.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriDouble(cijena.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravna cijena");
            alert.setHeaderText("Niste unijeli dobru cijenu.");
            alert.setContentText("Provjerite unesenu cijenu.");
            alert.showAndWait();
            throw new KriviUnosException();
        }  else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ažuriranje");
            alert.setHeaderText("Ažuriranje");
            alert.setContentText("Jeste li sigurni da želite ažurirati proizvod.");
            Optional<ButtonType> rezultat = alert.showAndWait();
            if (rezultat.isPresent()){


            if(rezultat.get() == ButtonType.OK) {

                try {
                    Proizvod azuriraniProizvod = new ProizvodBuilder()
                            .setId(odabranProizvod.getId()).setNaziv(naziv.getText())
                            .setOpis(opis.getText()).setKolicina(Integer.parseInt(kolicina.getText()))
                            .setCijena(Double.parseDouble(cijena.getText())).setSlika(slikaFile).createProizvod();
                    BazaPodataka.azurirajProizvod(azuriraniProizvod,
                            slikaFile.getName().equals(odabranProizvod.getSlika().getName()));
                    Serijalizacija<Promjena<Proizvod, Zaposlenik>> promjenaSerijalizacija = new Serijalizacija<>();
                    Promjena<Proizvod, Zaposlenik> promjena = new PromjenaBuilder()
                            .setPocetni(odabranProizvod).setPromijenjen(azuriraniProizvod)
                            .setRola(Run.getZaposlenik()).createPromjena();
                    promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/proizvodiPromjene.dat");

                    proizvodList = BazaPodataka.vratiSveProizvode();
                } catch (IOException e) {
                    logger.info(e.getMessage());
                    throw new RuntimeException(e);
                }

            }
            }
        }

    }

    public void filtriraj() {
        try {
            Integer kolicinaParse;
            Double cijenaParse;
            if (kolicina.getText().isBlank()){
                kolicinaParse = null;
            } else {
                kolicinaParse = Integer.parseInt(kolicina.getText());
            }
            if (cijena.getText().isBlank()){
                cijenaParse = null;
            } else {
                cijenaParse = Double.parseDouble(cijena.getText());

            }
            proizvodList = BazaPodataka.dohvatiProizvode(new ProizvodBuilder()
                    .setId(null).setNaziv(naziv.getText()).setOpis(opis.getText()).setKolicina(kolicinaParse)
                    .setCijena(cijenaParse).setSlika(slikaFile).createProizvod());
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public void odaberiProizvod() {
        odabranProizvod = proizvodList.get(tablica.getSelectionModel().getSelectedIndex());
        slikaFile =odabranProizvod.getSlika();
        opis.setText(odabranProizvod.getOpis());
        cijena.setText(odabranProizvod.getCijena().toString());
        naziv.setText(odabranProizvod.getNaziv());
        kolicina.setText(odabranProizvod.getKolicina().toString());
        slika.setImage(new Image(slikaFile.toURI().toString()));
    }

    public void makniSliku() {
        slikaFile= null;
        slika.setImage(null);
    }
}
