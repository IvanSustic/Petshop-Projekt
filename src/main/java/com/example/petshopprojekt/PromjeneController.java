package com.example.petshopprojekt;

import Entiteti.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PromjeneController {

    @FXML
    TableView<Zivotinja> tablica;
    @FXML
    TableView<Zaposlenik> tablicaZaposlenici;
    @FXML
    TableView<Korisnik> tablicaKorisnici;
    @FXML
    TableView<Proizvod> tablicaProizvodi;
    @FXML
    TableView<Proizvod> tablicaProizvodi1;
    @FXML
    TableView<Proizvod> tablicaProizvodi2;
    @FXML
    TableColumn<Zivotinja,String> vrstaTablica,pasminaTablica,spolTablica,godineTablica,kontaktTablica;
    @FXML
    TableColumn<Zaposlenik,String> imeTablica,prezimeTablica,datumTablica,korisnickoTablica;
    @FXML
    TableColumn<Korisnik,String> imeTablicaKorisnik,prezimeTablicaKorisnik,datumTablicaKorisnik,

            korisnickoTablicaKorisnik,adresaTablicaKorisnik;
    @FXML
    TableColumn<Proizvod,String>  nazivTablica,cijenaTablica,kolicinaTablica,opisTablica;
    @FXML
    TableColumn<Proizvod,String>  nazivTablica1,cijenaTablica1,kolicinaTablica1,opisTablica1;
    @FXML
    TableColumn<Proizvod,String>  nazivTablica2,cijenaTablica2,kolicinaTablica2,opisTablica2;
    @FXML
    ComboBox korisnikFilter;
    private final Stage stage = Run.getMainStage();




    private  List<Promjena<Zivotinja,Zaposlenik>> listaPromjenaZivotinja;
    private  List<Promjena<Zaposlenik,Zaposlenik>> listPromjenaZaposlenika;
    private  List<Promjena<Korisnik,Zaposlenik>> listPromjenaKorisnika;

    private  List<Promjena<Proizvod,Zaposlenik>> listPromjenaProizvoda;
    private  List<Promjena<Proizvod,Korisnik>> listPromjenaProizvoda2;
    private  List<Promjena<Proizvod,Korisnik>> listPromjenaProizvoda3;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public void initialize(){
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

        imeTablicaKorisnik.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getIme()));
        prezimeTablicaKorisnik.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getPrezime()));
        datumTablicaKorisnik.setCellValueFactory(
                zaposlenik -> {
                    SimpleStringProperty property = new
                            SimpleStringProperty();
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                    property.setValue(
                            zaposlenik.getValue().getDatumRodjenja().format(formatter));
                    return property;
                });
        korisnickoTablicaKorisnik.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKorisnickoIme())));
        adresaTablicaKorisnik.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getAdresa())));
        nazivTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getNaziv()));
        opisTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getOpis()));
        cijenaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty((df.format(celldata.getValue().getCijena()))+" €"));
        kolicinaTablica.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKolicina())));
        nazivTablica1.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getNaziv()));
        opisTablica1.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getOpis()));
        cijenaTablica1.setCellValueFactory(celldata ->
                new SimpleStringProperty((df.format(celldata.getValue().getCijena()))+" €"));
        kolicinaTablica1.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKolicina())));
        nazivTablica2.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getNaziv()));
        opisTablica2.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getOpis()));
        cijenaTablica2.setCellValueFactory(celldata ->
                new SimpleStringProperty((df.format(celldata.getValue().getCijena()))+" €"));
        kolicinaTablica2.setCellValueFactory(celldata ->
                new SimpleStringProperty(String.valueOf(celldata.getValue().getKolicina())));

        velicinaTablice();
        stage.maximizedProperty().addListener((obs, oldVal, newVal)->{
            if (stage.isMaximized()){
                velicinaTablice();
            }
        });

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.maximizedProperty().getValue()){

                vrstaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0*2);
                godineTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0);
                pasminaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0*2);
                kontaktTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0*2);
                spolTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/8.0);
                imeTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                prezimeTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                korisnickoTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                datumTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/4);
                imeTablicaKorisnik.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                prezimeTablicaKorisnik.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                korisnickoTablicaKorisnik.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                datumTablicaKorisnik.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                adresaTablicaKorisnik.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/5);
                nazivTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                opisTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/2.1);
                cijenaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                kolicinaTablica.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                nazivTablica1.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                opisTablica1.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/2.1);
                cijenaTablica1.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                kolicinaTablica1.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                nazivTablica2.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                opisTablica2.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/2.1);
                cijenaTablica2.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
                kolicinaTablica2.prefWidthProperty().setValue(tablica.widthProperty().getValue().intValue()/6);
            }

        });


        listaPromjenaZivotinja =
                new Serijalizacija<Promjena<Zivotinja,Zaposlenik>>().ucitaj("Datoteke/zivotinjePromjene.dat");


        List<Zivotinja> noveZivotinjaList = new ArrayList<>();
        listaPromjenaZivotinja.forEach(zivotinjaZaposlenikPromjena ->
                noveZivotinjaList.add( zivotinjaZaposlenikPromjena.getPromijenjen()));
        tablica.setItems(FXCollections.observableArrayList(noveZivotinjaList));



        listPromjenaZaposlenika =
                new Serijalizacija<Promjena<Zaposlenik,Zaposlenik>>().ucitaj("Datoteke/zaposleniciPromjene.dat");


        List<Zaposlenik> noviZaposlenici = new ArrayList<>();
        listPromjenaZaposlenika.forEach(zaposlenikPromjena ->
                noviZaposlenici.add( zaposlenikPromjena.getPromijenjen()));
        tablicaZaposlenici.setItems(FXCollections.observableArrayList(noviZaposlenici));


        listPromjenaKorisnika  =
                new Serijalizacija<Promjena<Korisnik,Zaposlenik>>().ucitaj("Datoteke/korisniciPromjene.dat");


        List<Korisnik> noviKorisnici = new ArrayList<>();
        listPromjenaKorisnika.forEach(korisnikZaposlenikPromjena ->
                noviKorisnici.add( korisnikZaposlenikPromjena.getPromijenjen()));
        tablicaKorisnici.setItems(FXCollections.observableArrayList(noviKorisnici));


        listPromjenaProizvoda  =
                    new Serijalizacija<Promjena<Proizvod,Zaposlenik>>().ucitaj("Datoteke/proizvodiPromjene.dat");


        List<Proizvod> noviProizvod = new ArrayList<>();
        listPromjenaProizvoda.forEach( proizvodZaposlenikPromjena->
                noviProizvod.add( proizvodZaposlenikPromjena.getPromijenjen()));
        tablicaProizvodi.setItems(FXCollections.observableArrayList(noviProizvod));


        listPromjenaProizvoda2  =
                new Serijalizacija<Promjena<Proizvod,Korisnik>>().ucitaj("Datoteke/prodaniProizvodi.dat");

        List<Proizvod> noviProizvod2 = new ArrayList<>();
        listPromjenaProizvoda2.forEach( proizvodKorisnikPromjena->
                noviProizvod2.add( proizvodKorisnikPromjena.getPromijenjen()));
        tablicaProizvodi1.setItems(FXCollections.observableArrayList(noviProizvod2));


        listPromjenaProizvoda3  =
                new Serijalizacija<Promjena<Proizvod,Korisnik>>().ucitaj("Datoteke/kupljeniProizvodi.dat");

        List<Proizvod> noviProizvod3 = new ArrayList<>();
        listPromjenaProizvoda3.forEach( proizvodKorisnikPromjena->
                noviProizvod3.add( proizvodKorisnikPromjena.getPromijenjen()));
        tablicaProizvodi2.setItems(FXCollections.observableArrayList(noviProizvod3));

        listPromjenaProizvoda3.forEach(proizvodKorisnikPromjena -> {
            if (korisnikFilter.getItems().stream().noneMatch(item ->
                    item.toString().equals(proizvodKorisnikPromjena.getRola().getKorisnickoIme()))){
                korisnikFilter.getItems().add(proizvodKorisnikPromjena.getRola().getKorisnickoIme());
            }

        });
        korisnikFilter.getItems().add("Svi");
    }

    private void velicinaTablice() {
        vrstaTablica.prefWidthProperty().setValue(1313/8.0*2);
        godineTablica.prefWidthProperty().setValue(1313/8.0);
        pasminaTablica.prefWidthProperty().setValue(1313/8.0*2);
        kontaktTablica.prefWidthProperty().setValue(1313/8.0*2);
        spolTablica.prefWidthProperty().setValue(1313/8.0);
        imeTablica.prefWidthProperty().setValue(1313/4);
        prezimeTablica.prefWidthProperty().setValue(1313/4);
        korisnickoTablica.prefWidthProperty().setValue(1313/4);
        datumTablica.prefWidthProperty().setValue(1313/4);
        imeTablicaKorisnik.prefWidthProperty().setValue(1313/5);
        prezimeTablicaKorisnik.prefWidthProperty().setValue(1313/5);
        korisnickoTablicaKorisnik.prefWidthProperty().setValue(1313/5);
        datumTablicaKorisnik.prefWidthProperty().setValue(1313/5);
        adresaTablicaKorisnik.prefWidthProperty().setValue(1313/5);
        nazivTablica.prefWidthProperty().setValue(1313/6);
        opisTablica.prefWidthProperty().setValue(1313/2);
        cijenaTablica.prefWidthProperty().setValue(1313/6);
        kolicinaTablica.prefWidthProperty().setValue(1313/6);
        nazivTablica1.prefWidthProperty().setValue(1313/6);
        opisTablica1.prefWidthProperty().setValue(1313/2);
        cijenaTablica1.prefWidthProperty().setValue(1313/6);
        kolicinaTablica1.prefWidthProperty().setValue(1313/6);
        nazivTablica2.prefWidthProperty().setValue(1313/6);
        opisTablica2.prefWidthProperty().setValue(1313/2);
        cijenaTablica2.prefWidthProperty().setValue(1313/6);
        kolicinaTablica2.prefWidthProperty().setValue(1313/6);
    }

    public void odaberiZivotinju() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijašnje vrijednosti");
        alert.setHeaderText("Promijenjen od strane zaposlenika: "+listaPromjenaZivotinja
                .get(tablica.getSelectionModel().getSelectedIndex()).getRola().getKorisnickoIme());
        Zivotinja staraZivotinja =
        listaPromjenaZivotinja.get(tablica.getSelectionModel().getSelectedIndex()).getPocetni();
        if (staraZivotinja == null){
            String string = "Unesen je nova životinja \n"
                    +"Vrijeme promjene: "+listaPromjenaZivotinja
                    .get(tablica.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText(string);
            alert.showAndWait();
        } else {
            String string = "Vrsta: " + staraZivotinja.getVrsta().getOpis() + "\n" + "Pasmina: " + staraZivotinja.getPasmina() + "\n"
                    + "Godine: " + staraZivotinja.getGodine() + "\n" + "Spol: " + staraZivotinja.getSpol().getOpis() + "\n"
                    + "Kontakt: " + staraZivotinja.getKontakt() + "\n" + "Vrijeme promjene: " + listaPromjenaZivotinja
                    .get(tablica.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText("Stari podaci su:\n" + string);
            alert.showAndWait();
        }
    }

    public void odaberiZaposlenika() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijašnje vrijednosti");
        alert.setHeaderText("Promijenjen od strane zaposlenika: "+listPromjenaZaposlenika
                .get(tablicaZaposlenici.getSelectionModel().getSelectedIndex()).getRola().getKorisnickoIme());
        Zaposlenik stariZaposlenik =
                listPromjenaZaposlenika.get(tablicaZaposlenici.getSelectionModel().getSelectedIndex()).getPocetni();
        if (stariZaposlenik == null){
            String string = "Unesen je novi zaposlenik\n"
                    +"Vrijeme promjene: "+listPromjenaZaposlenika
                    .get(tablicaZaposlenici.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText(string);
            alert.showAndWait();
        } else {
            String string = "Ime: " + stariZaposlenik.getIme() + "\n" + "Prezime: " + stariZaposlenik.getPrezime() + "\n"
                    + "Korisnicko ime: " + stariZaposlenik.getKorisnickoIme() + "\n" + "Datum rođenja: " +
                    stariZaposlenik.getDatumRodjenja().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n"
                    + "Vrijeme promjene: " + listPromjenaZaposlenika
                    .get(tablicaZaposlenici.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText("Stari podaci su:\n" + string);
            alert.showAndWait();
        }
    }

    public void odaberiKorisnika() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijašnje vrijednosti");
        alert.setHeaderText("Promijenjen od strane zaposlenika: "+listPromjenaKorisnika
                .get(tablicaKorisnici.getSelectionModel().getSelectedIndex()).getRola().getKorisnickoIme());
        Korisnik stariKorisnik =
                listPromjenaKorisnika.get(tablicaKorisnici.getSelectionModel().getSelectedIndex()).getPocetni();
        if (stariKorisnik == null){
            String string = "Unesen je novi korisnik \n"
                    +"Vrijeme promjene: "+listPromjenaKorisnika
                    .get(tablicaKorisnici.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText(string);
            alert.showAndWait();
        } else {
            String string = "Ime: " + stariKorisnik.getIme() + "\n" + "Prezime: " + stariKorisnik.getPrezime() + "\n"
                    + "Korisnicko ime: " + stariKorisnik.getKorisnickoIme() + "\n" + "Datum rođenja: " +
                    stariKorisnik.getDatumRodjenja().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n"
                    + "Adresa: " + stariKorisnik.getAdresa() + "\n"
                    + "Vrijeme promjene: " + listPromjenaKorisnika
                    .get(tablicaKorisnici.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText("Stari podaci su:\n" + string);
            alert.showAndWait();
        }
    }

    public void odaberiProizvod() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijašnje vrijednosti");
        alert.setHeaderText("Promijenjen od strane zaposlenika: "+listPromjenaProizvoda
                .get(tablicaProizvodi.getSelectionModel().getSelectedIndex()).getRola().getKorisnickoIme());
        Proizvod stariProizvod =
                listPromjenaProizvoda.get(tablicaProizvodi.getSelectionModel().getSelectedIndex()).getPocetni();
        if (stariProizvod == null){
            String string = "Unesen je novi proizvod \n"
                    +"Vrijeme promjene: "+listPromjenaProizvoda
                    .get(tablicaProizvodi.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText(string);
            alert.showAndWait();
        } else {
            String string = "Naziv: "+stariProizvod.getNaziv()+"\n"+"Cijena: "+stariProizvod.getCijena()+"\n"
                    +"Količina: "+stariProizvod.getKolicina()+"\n"
                    +"Opis: "+stariProizvod.getOpis()+"\n"
                    +"Vrijeme promjene: "+listPromjenaProizvoda
                    .get(tablicaProizvodi.getSelectionModel().getSelectedIndex())
                    .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            alert.setContentText("Stari podaci su:\n"+string);
            alert.showAndWait();
        }

    }

    public void odaberiProizvod2() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijašnje vrijednosti");
        alert.setHeaderText("Promijenjen od strane korisnika: "+listPromjenaProizvoda2
                .get(tablicaProizvodi1.getSelectionModel().getSelectedIndex()).getRola().getKorisnickoIme());
        Proizvod stariProizvod =
                listPromjenaProizvoda2.get(tablicaProizvodi1.getSelectionModel().getSelectedIndex()).getPocetni();
        String string = "Naziv: "+stariProizvod.getNaziv()+"\n"+"Cijena: "+stariProizvod.getCijena()+"\n"
                +"Količina: "+stariProizvod.getKolicina()+"\n"
                +"Opis: "+stariProizvod.getOpis()+"\n"
                +"Vrijeme promjene: "+listPromjenaProizvoda2
                .get(tablicaProizvodi1.getSelectionModel().getSelectedIndex())
                .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        alert.setContentText("Stari podaci su:\n"+string);
        alert.showAndWait();
    }

    public void odaberiProizvod3() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijašnje vrijednosti");
        alert.setHeaderText("Promijenjen od strane korisnika: "+listPromjenaProizvoda3
                .get(tablicaProizvodi2.getSelectionModel().getSelectedIndex()).getRola().getKorisnickoIme());
        String string = "Proizvod je kupljen: " +listPromjenaProizvoda2
                .get(tablicaProizvodi2.getSelectionModel().getSelectedIndex())
                .getVrijeme().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        alert.setContentText(string);
        alert.showAndWait();
    }

    public void filtrirajPoKorisniku() {
        List<Proizvod> proizvodLista = new ArrayList<>();
        if (korisnikFilter.getValue().equals("Svi") || korisnikFilter.getValue()==null){
            listPromjenaProizvoda3.forEach(proizvodKorisnikPromjena ->
                    proizvodLista.add(proizvodKorisnikPromjena.getPromijenjen()));
        }else {
            listPromjenaProizvoda3.forEach(proizvodKorisnikPromjena -> {
                if (proizvodKorisnikPromjena.getRola().getKorisnickoIme().equals(korisnikFilter.getValue())){
                    proizvodLista.add(proizvodKorisnikPromjena.getPromijenjen());
                }
            });
        }
        tablicaProizvodi2.getItems().clear();
        tablicaProizvodi2.getItems().addAll(FXCollections.observableArrayList(proizvodLista));
    }
}
