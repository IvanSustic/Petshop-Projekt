package com.example.petshopprojekt;

import Entiteti.ClickListener;
import Entiteti.Proizvod;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;

public class ProizvodController {
    @FXML
    Label proizvodLabel,cijenaLabel;

    @FXML
    ImageView slika;

    @FXML
    private void odaberi() {
        clickListener.onClickListener(proizvod);
    }

    private Proizvod proizvod;
    private ClickListener clickListener;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public void postaviVrijednosti(Proizvod proizvod, ClickListener clickListener) {
        this.proizvod = proizvod;
        this.clickListener = clickListener;
        proizvodLabel.setText(proizvod.getNaziv());
        cijenaLabel.setText(df.format(proizvod.getCijena())+" Eura");
        Image image = new Image(proizvod.getSlika().toURI().toString());
        slika.setImage(image);
    }
}
