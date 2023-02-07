package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
import Entiteti.*;
import Iznimke.BazaPodatakaException;
import Iznimke.NemoguceOtvoritiMailException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class KorisnikZivotinjeController {
    @FXML
    VBox odabranaZivotinja;

    @FXML
    Label zivotinjaVrsta,zivotinjaPasmina,zivotinjaSpol,zivotinjaGodine;

    @FXML
    ImageView zivotinjaSlika;

    @FXML
    ScrollPane izbornik;

    @FXML
    GridPane grid;
    @FXML
    ComboBox vrstaBox;
    @FXML
    TextField pasminaPretraga;

    private final Stage stage = Run.getMainStage();
    private List<Zivotinja> zivotinjaList;
    private ZivotinjaClickListener zivotinjaClickListener;
    private Zivotinja odabranaZivotinjaClick;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);

    private Integer brojStupca = 4;


    private void odaberiProizvod(Zivotinja zivotinja) {
        odabranaZivotinjaClick = zivotinja;
        zivotinjaVrsta.setText(zivotinja.getVrsta().getOpis());
        zivotinjaGodine.setText(zivotinja.getGodine().toString());
        zivotinjaPasmina.setText(zivotinja.getPasmina());
        zivotinjaSpol.setText(zivotinja.getSpol().getOpis());
        Image image = new Image(zivotinja.getSlika().toURI().toString());
        zivotinjaSlika.setImage(image);
        odabranaZivotinja.setStyle("-fx-background-color: lightgreen;"+"\n" +
                "    -fx-background-radius: 30;");

    }

    public void prikazi(){
        grid.getChildren().clear();
        if (zivotinjaList.size() > 0) {
            odaberiProizvod(zivotinjaList.get(0));
            zivotinjaClickListener = this::odaberiProizvod;
        }

        int column = 0;
        int row = 1;
        try {
            for (Zivotinja zivotinja : zivotinjaList) {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("Zivotinja.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ZivotinjaController zivotinjaController = fxmlLoader.getController();
                zivotinjaController.postaviVrijednosti(zivotinja, zivotinjaClickListener);

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
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    public void initialize() {
        try {
            zivotinjaList = BazaPodataka.vratiSveZivotinje();
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        prikazi();

        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                brojStupca =4;
                prikazi();
            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){
                if (izbornik.widthProperty().getValue()<350 && brojStupca !=1){
                    brojStupca=1;
                    prikazi();
                } else if (izbornik.widthProperty().getValue()<650 &&
                        izbornik.widthProperty().getValue()>350  && brojStupca != 2){
                    brojStupca=2;
                    prikazi();
                } else if (izbornik.widthProperty().getValue()>650 &&brojStupca!= 3){
                    brojStupca=3;
                    prikazi();
                }
            }

        });
        vrstaBox.getItems().add(Vrsta.PAS.getOpis());
        vrstaBox.getItems().add(Vrsta.MACKA.getOpis());
        vrstaBox.getItems().add(Vrsta.ZAMORAC.getOpis());
        vrstaBox.getItems().add(Vrsta.GMAZ.getOpis());
        vrstaBox.getItems().add(Vrsta.PTICA.getOpis());
        vrstaBox.getItems().add("Svi");

    }

    public void pretrazi() {
        Vrsta vrsta=null;
        if (vrstaBox.getValue() != null && !vrstaBox.getValue().equals("Svi")){
            vrsta = Metode.vratiVrstu(vrstaBox.getValue().toString());
        }
        try {
            zivotinjaList = BazaPodataka.dohvatiZivotinje(new ZivotinjaBuilder()
                    .setId(null).setPasmina(pasminaPretraga.getText()).setGodine(null)
                    .setSpol(null).setVrsta(vrsta).setSlika(null).setKontakt(null).createZivotinja());
        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        prikazi();
    }

    public void kontaktiraj() throws URISyntaxException, IOException {
        Desktop desktop;
        if (Desktop.isDesktopSupported()
                && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
            URI mailto = new URI("mailto:"+odabranaZivotinjaClick.getKontakt());
            desktop.mail(mailto);
        } else {
            throw new NemoguceOtvoritiMailException();
        }
    }
}
