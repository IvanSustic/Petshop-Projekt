package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
import Entiteti.*;
import Iznimke.BazaPodatakaException;
import Iznimke.KriviUnosException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class KorisnikProizvodiController {
    @FXML
    VBox odabranProizvod;

    @FXML
    Label proizvodNaziv,prozivodCijena,ukupnaCijena;

    @FXML
    ImageView proizvodSlika;

    @FXML
    ScrollPane izbornik;

    @FXML
    GridPane grid;
    @FXML
    ComboBox kolicina;
    @FXML
    TextField pretraga;
    @FXML
    TextArea opis;
    @FXML
    AnchorPane pocetna,kosarica;
    @FXML
    TableView<Proizvod> tablica;
    @FXML
    TableColumn<Proizvod,String>  nazivTablica,cijenaTablica,kolicinaTablica,opisTablica;



    private final Stage stage = Run.getMainStage();
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private List<Proizvod> proizvodList;
    private final List<Proizvod> proizvodiUKosarici = new ArrayList<>();
    private ClickListener clickListener;

    private Proizvod odabraniProizvod;
    private Proizvod odabranProizvodKosarice;
    private Boolean aBoolean=true;
    private Integer brojStupca = 4;


    private void odaberiProizvod(Proizvod proizvod) {
        odabraniProizvod = proizvod;
        kolicina.getItems().clear();
        proizvodNaziv.setText(proizvod.getNaziv());
        prozivodCijena.setText(df.format(proizvod.getCijena())+" Eura");
        opis.setText(proizvod.getOpis());
        Image image = new Image(proizvod.getSlika().toURI().toString());
        proizvodSlika.setImage(image);
        odabranProizvod.setStyle("-fx-background-color: lightgreen;"+"\n" +
                "    -fx-background-radius: 30;");
        if (odabraniProizvod.getKolicina()!=0){
            IntStream.range(1,proizvod.getKolicina()+1).forEach(broj -> kolicina.getItems().add(broj));
        }  else {
            kolicina.getItems().add("Proizvod je rasprodan.");
        }


    }

    public void prikazi(){
        grid.getChildren().clear();
        if (aBoolean) {
            if (proizvodList.size() > 0) {
                odaberiProizvod(proizvodList.get(0));
                clickListener = this::odaberiProizvod;
                aBoolean=false;
            }
        }
        int column = 0;
        int row = 1;
        try {
            for (Proizvod proizvod : proizvodList) {
                if (proizvod.getKolicina() >= 1) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("Proizvod.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    ProizvodController proizvodController = fxmlLoader.getController();
                    proizvodController.postaviVrijednosti(proizvod, clickListener);

                    if (column == brojStupca) {
                        column = 0;
                        row++;
                    }

                    grid.add(anchorPane, column++, row);
                    grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                    grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    grid.setMaxWidth(Region.USE_PREF_SIZE);
                    grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                    grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    grid.setMaxHeight(Region.USE_PREF_SIZE);

                    GridPane.setMargin(anchorPane, new Insets(10));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        try {
            proizvodList = BazaPodataka.vratiSveProizvode();
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        prikazi();
        nazivTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getNaziv()));
        opisTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getOpis()));
        cijenaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty((df.format(celldata.getValue().getCijena()))+" €"));
        kolicinaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKolicina())));

        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                nazivTablica.prefWidthProperty().setValue(1313/6);
                opisTablica.prefWidthProperty().setValue(1313/2);
                cijenaTablica.prefWidthProperty().setValue(1313/6);
                kolicinaTablica.prefWidthProperty().setValue(1313/6);
                brojStupca =4;
                prikazi();
            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){

                nazivTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                opisTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/2.1);
                cijenaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                kolicinaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                if (tablica.widthProperty().getValue()<900 && brojStupca !=1){
                    brojStupca=1;
                    prikazi();
                } else if (tablica.widthProperty().getValue()<1200 &&
                        tablica.widthProperty().getValue()>900  && brojStupca != 2){
                    brojStupca=2;
                    prikazi();
                } else if (tablica.widthProperty().getValue()>1200 &&brojStupca!= 3){
                    brojStupca=3;
                    prikazi();
                }
            }

        });


    }

    public void pretrazi() {
        try {
            proizvodList = BazaPodataka.dohvatiProizvode(new ProizvodBuilder().setId(null)
                    .setNaziv(pretraga.getText()).setOpis(null).setKolicina(null).setCijena(null)
                    .setSlika(null).createProizvod());
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        prikazi();
    }

    public void dodajUKosaricu() {
        if (kolicina.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Niste odabrali kolicinu.");
            alert.setContentText("Odaberite količinu.");
            alert.showAndWait();
            throw new KriviUnosException();

        } else if (kolicina.getValue().toString().equals("Proizvod je rasprodan")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Proizvoda trenutno nema na zalihi.");
            alert.setContentText("Odaberite drugi proizvod.");
            alert.showAndWait();
            throw new KriviUnosException();
        } else {
            odabraniProizvod.setKolicina(odabraniProizvod.getKolicina() - (Integer) kolicina.getValue());
            if (proizvodiUKosarici.stream().anyMatch(proizvod -> proizvod.getId().equals(odabraniProizvod.getId()))){
                proizvodiUKosarici.forEach(proizvod -> {
                    if (proizvod.getId().equals(odabraniProizvod.getId())){
                        proizvod.setKolicina(proizvod.getKolicina()+ (Integer) kolicina.getValue());
                    }
                });
            } else {
                proizvodiUKosarici.add(new ProizvodBuilder().setId(odabraniProizvod.getId())
                        .setNaziv(odabraniProizvod.getNaziv()).setOpis(odabraniProizvod.getOpis()).setKolicina((Integer)
                                kolicina.getValue())
                        .setCijena(odabraniProizvod.getCijena()).setSlika(odabraniProizvod.getSlika()).createProizvod());
            }
            var ref = new Object() {
                Double ukupnaCijenaText = (double) 0;
            };
            proizvodiUKosarici.forEach(proizvod -> ref.ukupnaCijenaText += proizvod.getCijena()*proizvod.getKolicina());
            kolicina.getItems().clear();
            IntStream.range(1, odabraniProizvod.getKolicina() + 1).forEach(broj -> kolicina.getItems().add(broj));
            prikazi();
            tablica.getItems().clear();
            tablica.getItems().addAll(FXCollections.observableArrayList(proizvodiUKosarici));
            ukupnaCijena.setText("Ukupna cijena je: "+ df.format(ref.ukupnaCijenaText)+" Eura");
        }
    }

    public void prikaziKosaricu() {
        pocetna.setVisible(false);
        kosarica.setVisible(true);
    }

    public void odaberiProizvod() {
        odabranProizvodKosarice = proizvodiUKosarici.get(tablica.getSelectionModel().getSelectedIndex());
    }

    public void izbrisiProizvod() {
        proizvodiUKosarici.remove(odabranProizvodKosarice);
        proizvodList.forEach(proizvod -> {
            if (proizvod.getNaziv().equals(odabranProizvodKosarice.getNaziv())) {
                proizvod.setKolicina(proizvod.getKolicina()+ odabranProizvodKosarice.getKolicina());
            }
        });
        tablica.getItems().clear();
        tablica.getItems().addAll(FXCollections.observableArrayList(proizvodiUKosarici));
        var ref = new Object() {
            Double ukupnaCijenaText = (double) 0;
        };
        proizvodiUKosarici.forEach(proizvod -> ref.ukupnaCijenaText += proizvod.getCijena()*proizvod.getKolicina());
        ukupnaCijena.setText("Ukupna cijena je: "+ df.format(ref.ukupnaCijenaText)+" Eura");
    }

    public void kupi() {
            Thread thread = new Thread(() -> Run.getKupljeniProizvodi().poslaji(proizvodiUKosarici,Run.getKorisnik()));
            thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        proizvodList.forEach(proizvod -> {
                try {
                    Serijalizacija<Promjena<Proizvod, Korisnik>> promjenaSerijalizacija = new Serijalizacija<>();
                    Promjena<Proizvod, Korisnik> promjena = new PromjenaBuilder()
                            .setPocetni(BazaPodataka.dohvatiProizvode(new ProizvodBuilder().setId(proizvod.getId())
                                    .setNaziv(null).setOpis(null).setKolicina(null).setCijena(null).setSlika(null)
                                    .createProizvod()).get(0)).setPromijenjen(proizvod).setRola(Run.getKorisnik())
                            .createPromjena();
                    promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/prodaniProizvodi.dat");
                    BazaPodataka.azurirajProizvod(proizvod,true);

                } catch (BazaPodatakaException e) {
                    logger.info(e.getMessage());
                    throw new RuntimeException(e);
                }
        });
            try {
                proizvodList= BazaPodataka.vratiSveProizvode();
            } catch (BazaPodatakaException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }
            proizvodiUKosarici.clear();
            tablica.getItems().clear();
            ukupnaCijena.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspješna kupnja");
            alert.setHeaderText("Uspješna kupnja.");
            alert.setContentText("Proizvodi će biti dostavljeni na adresu: "+Run.getKorisnik().getAdresa());
            alert.showAndWait();
    }

    public void natrag() {
        pocetna.setVisible(true);
        kosarica.setVisible(false);
        prikazi();
    }
}
