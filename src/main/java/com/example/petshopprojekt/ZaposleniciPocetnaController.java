package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
import Entiteti.Proizvod;
import Iznimke.BazaPodatakaException;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.List;

public class ZaposleniciPocetnaController {
    @FXML
    BarChart<String, Number> graf;
    @FXML
    Label zarada;
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public void initialize(){
        zarada.setText("Ukupna zarada je: "+df.format(Run.getZarada()) +" Eura");
        try {
            List<Proizvod> listProizvoda = BazaPodataka.vratiSveProizvode();
            listProizvoda.forEach(proizvod -> {
                XYChart.Series<String,Number> series = new XYChart.Series<>();
                series.setName(proizvod.getNaziv());
                series.getData().add(new XYChart.Data<>("Količina",proizvod.getKolicina()));
                graf.getData().add(series);

            });

        } catch (BazaPodatakaException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }

    }


}
