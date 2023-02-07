package com.example.petshopprojekt;

import BazaPodataka.BazaPodataka;
import Entiteti.Proizvod;
import Entiteti.Zivotinja;
import Iznimke.BazaPodatakaException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class KorisnikPocetnaController {
    @FXML
    ImageView proizvodSlika,zivotinjaSlika;

    public void initialize(){
        try {
            List<Proizvod> proizvodList = BazaPodataka.vratiSveProizvode();
            List<Zivotinja> zivotinjaList = BazaPodataka.vratiSveZivotinje();

            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
            executorService.scheduleAtFixedRate(() -> {
                if (Run.getInteger().equals(7)){


                int randomNum = ThreadLocalRandom.current().nextInt(0, proizvodList.size());
                Image image1 = new Image(proizvodList.get(randomNum).getSlika().toURI().toString());
                int randomNum2 = ThreadLocalRandom.current().nextInt(0, zivotinjaList.size());
                Image image2 = new Image(zivotinjaList.get(randomNum2).getSlika().toURI().toString());
                Platform.runLater(()->{
                    proizvodSlika.setImage(image1);
                    zivotinjaSlika.setImage(image2);
                });
                }else {
                    executorService.shutdown();
                }
            },1,5, TimeUnit.SECONDS);
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }


    }
}
