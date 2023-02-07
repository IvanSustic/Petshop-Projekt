package com.example.petshopprojekt;

import Entiteti.Zivotinja;
import Entiteti.ZivotinjaClickListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ZivotinjaController {
    @FXML
    Label vrstaLabel,pasminaLabel;

    @FXML
    ImageView slika;

    @FXML
    private void odaberi() {
        zivotinjaClickListener.onClickListener(zivotinja);
    }

    private Zivotinja zivotinja;
    private ZivotinjaClickListener zivotinjaClickListener;

    public void postaviVrijednosti(Zivotinja zivotinja, ZivotinjaClickListener zivotinjaClickListener) {
        this.zivotinja = zivotinja;
        this.zivotinjaClickListener = zivotinjaClickListener;
        pasminaLabel.setText(zivotinja.getPasmina());
        vrstaLabel.setText(zivotinja.getVrsta().getOpis());
        Image image = new Image(zivotinja.getSlika().toURI().toString());
        slika.setImage(image);
    }
}
