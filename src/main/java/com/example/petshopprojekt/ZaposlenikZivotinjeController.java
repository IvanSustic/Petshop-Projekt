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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZaposlenikZivotinjeController {
    @FXML
    TextField godine,pasmina,kontakt;
    @FXML
    ComboBox vrsta;
    @FXML
    RadioButton musko,zensko,svi;
    @FXML
    ImageView slika;

    @FXML
    TableView<Zivotinja> tablica;
    @FXML
    TableColumn<Zivotinja,String> vrstaTablica,godineTablica,pasminaTablica,kontaktTablica,spolTablica;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    private List<Zivotinja> zivotinjaList;
    private Zivotinja odabranaZivotinja;
    private File slikaFile;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final Stage stage = Run.getMainStage();


    public void initialize(){
            toggleGroup.getToggles().add(musko);
            toggleGroup.getToggles().add(zensko);
            toggleGroup.getToggles().add(svi);
        try {
            zivotinjaList = BazaPodataka.vratiSveZivotinje();
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }

        vrstaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getVrsta().getOpis()));
        godineTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getGodine().toString()));
        pasminaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getPasmina()));
        kontaktTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKontakt())));
        spolTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getSpol().getOpis())));


        tablica.setItems(FXCollections.observableArrayList(zivotinjaList));
        vrsta.getItems().add(Vrsta.PAS.getOpis());
        vrsta.getItems().add(Vrsta.MACKA.getOpis());
        vrsta.getItems().add(Vrsta.ZAMORAC.getOpis());
        vrsta.getItems().add(Vrsta.GMAZ.getOpis());
        vrsta.getItems().add(Vrsta.PTICA.getOpis());
        vrsta.getItems().add("Svi");
        toggleGroup.selectToggle(null);

        vrstaTablica.prefWidthProperty().setValue(1313/8.0*2);
        godineTablica.prefWidthProperty().setValue(1313/8.0);
        pasminaTablica.prefWidthProperty().setValue(1313/8.0*2);
        kontaktTablica.prefWidthProperty().setValue(1313/8.0*2);
        spolTablica.prefWidthProperty().setValue(1313/8.0);
        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                vrstaTablica.prefWidthProperty().setValue(1313/8.0*2);
                godineTablica.prefWidthProperty().setValue(1313/8.0);
                pasminaTablica.prefWidthProperty().setValue(1313/8.0*2);
                kontaktTablica.prefWidthProperty().setValue(1313/8.0*2);
                spolTablica.prefWidthProperty().setValue(1313/8.0);

            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){

                vrstaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0*2);
                godineTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0);
                pasminaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0*2);
                kontaktTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0*2);
                spolTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0);
            }

        });
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (Run.getInteger().equals(4)){
                Platform.runLater(()->{
                    tablica.getItems().clear();
                    tablica.setItems(FXCollections.observableArrayList(zivotinjaList));
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

    public void unesiZivotinju() {
        if (godine.getText().isBlank() || pasmina.getText().isBlank()
                || kontakt.getText().isBlank() || vrsta.getSelectionModel().isEmpty() || (slikaFile == null)
                || !toggleGroup.getSelectedToggle().isSelected() || toggleGroup.getSelectedToggle().equals(svi) ||
                vrsta.getSelectionModel().getSelectedItem().toString().equals("Svi")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriMail(kontakt.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravan mail");
            alert.setHeaderText("Niste unijeli dobar email.");
            alert.setContentText("Provjerite uneseni email.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriBroj(godine.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravan broj");
            alert.setHeaderText("Niste unijeli dobre godine.");
            alert.setContentText("Provjerite unesene godine.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else {

            try {
                Spol spol = null;
                if (toggleGroup.getToggles().get(0).isSelected()){
                    spol = Spol.M;
                } else if (toggleGroup.getToggles().get(1).isSelected()) {
                    spol = Spol.F;
                }
                Zivotinja zivotinja = new ZivotinjaBuilder().setId(null)
                        .setPasmina(pasmina.getText())
                        .setGodine(Integer.parseInt(godine.getText()))
                        .setSpol(spol).setVrsta(Metode.vratiVrstu(vrsta.getValue().toString()))
                        .setSlika(slikaFile).setKontakt(kontakt.getText()).createZivotinja();
                BazaPodataka.spremiNovuZivotinju(zivotinja);
                Serijalizacija<Promjena<Zivotinja, Zaposlenik>> promjenaSerijalizacija = new Serijalizacija<>();
                Promjena<Zivotinja, Zaposlenik> promjena = new PromjenaBuilder()
                        .setPocetni(null).setPromijenjen(zivotinja)
                        .setRola(Run.getZaposlenik()).createPromjena();
                promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/zivotinjePromjene.dat");
                zivotinjaList = BazaPodataka.vratiSveZivotinje();
            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }


        }

    }

    public void izbrisiZivotinju() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Brisanje");
        alert.setContentText("Jeste li sigurni da želite izbrisati životinju.");
        Optional<ButtonType> rezultat = alert.showAndWait();
        if (rezultat.isPresent()){


        if(rezultat.get() == ButtonType.OK) {
            try {
                BazaPodataka.obrisiZivotinju(odabranaZivotinja);
                zivotinjaList = BazaPodataka.vratiSveZivotinje();
            } catch (IOException | SQLException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }

        }
        }
    }

    public void azurirajZivotinju() {
        if (godine.getText().isBlank() || pasmina.getText().isBlank()
                || kontakt.getText().isBlank() || vrsta.getSelectionModel().isEmpty() || (slikaFile == null)
                || !toggleGroup.getSelectedToggle().isSelected() || toggleGroup.getSelectedToggle().equals(svi) ||
                vrsta.getSelectionModel().getSelectedItem().toString().equals("Svi")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste unijeli sve podatke.");
            alert.setContentText("Provjerite da ste unesli sve podatke.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriMail(kontakt.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravan mail");
            alert.setHeaderText("Niste unijeli dobar email.");
            alert.setContentText("Provjerite uneseni email.");
            alert.showAndWait();
            throw new KriviUnosException();
        }else if (!Metode.provjeriBroj(godine.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neispravan broj");
            alert.setHeaderText("Niste unijeli dobre godine.");
            alert.setContentText("Provjerite unesene godine.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ažuriranje");
            alert.setHeaderText("Ažuriranje");
            alert.setContentText("Jeste li sigurni da želite ažurirati korisnika.");
            Optional<ButtonType> rezultat = alert.showAndWait();
            if (rezultat.isPresent()){

            if(rezultat.get() == ButtonType.OK) {
                try {
                    Spol spol = null;
                    if (toggleGroup.getToggles().get(0).isSelected()) {
                        spol = Spol.M;
                    } else if (toggleGroup.getToggles().get(1).isSelected()) {
                        spol = Spol.F;
                    }

                    Zivotinja azuriranaZivotinja = new ZivotinjaBuilder()
                            .setId(odabranaZivotinja.getId()).setPasmina(pasmina.getText())
                            .setGodine(Integer.parseInt(godine.getText())).setSpol(spol)
                            .setVrsta(Metode.vratiVrstu(vrsta.getValue().toString())).setSlika(slikaFile)
                            .setKontakt(kontakt.getText()).createZivotinja();
                    BazaPodataka.azurirajZivotinju(azuriranaZivotinja,
                            slikaFile.getName().equals(odabranaZivotinja.getSlika().getName()));
                    Serijalizacija<Promjena<Zivotinja, Zaposlenik>> promjenaSerijalizacija = new Serijalizacija<>();
                    Promjena<Zivotinja, Zaposlenik> promjena = new PromjenaBuilder()
                            .setPocetni(odabranaZivotinja).setPromijenjen(azuriranaZivotinja)
                            .setRola(Run.getZaposlenik()).createPromjena();
                    promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/zivotinjePromjene.dat");

                    zivotinjaList = BazaPodataka.vratiSveZivotinje();
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
            Integer godineParse;
            if (godine.getText().isBlank()){
                godineParse = null;
            } else {
                godineParse = Integer.parseInt(godine.getText());
            }
            Spol spol = null;
            if (toggleGroup.getToggles().get(0).isSelected()){
                spol = Spol.M;
            } else if (toggleGroup.getToggles().get(1).isSelected()) {
                spol = Spol.F;
            }
            Vrsta vrstaParse = null;
            if (!vrsta.getSelectionModel().isEmpty()){
                vrstaParse = Metode.vratiVrstu(vrsta.getSelectionModel().getSelectedItem().toString());
                if (vrsta.getSelectionModel().getSelectedItem().toString().equals("Svi")) {
                    vrstaParse =null;
                }
            }
            zivotinjaList = BazaPodataka.dohvatiZivotinje(new ZivotinjaBuilder()
                    .setId(null).setPasmina(pasmina.getText()).setGodine(godineParse)
                    .setSpol(spol).setVrsta(vrstaParse).setSlika(slikaFile)
                    .setKontakt(kontakt.getText()).createZivotinja());
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }



    }

    public void odaberiZivotinju() {
        odabranaZivotinja = zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex());
        slikaFile = zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex()).getSlika();
        kontakt.setText(zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex()).getKontakt());
        pasmina.setText(zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex()).getPasmina());
        godine.setText(zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex()).getGodine().toString());
        vrsta.getSelectionModel().select(zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex())
                .getVrsta().getVrijednost().intValue());
        if (zivotinjaList.get(tablica.getSelectionModel().getSelectedIndex()).getSpol().getOpis().equals("Muško")){
            toggleGroup.selectToggle(musko);
        } else {
            toggleGroup.selectToggle(zensko);
        }
        slika.setImage(new Image(slikaFile.toURI().toString()));

    }

    public void makniSliku() {
        slikaFile= null;
        slika.setImage(null);
    }
}
