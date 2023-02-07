package BazaPodataka;

import Datoteke.Login;
import Entiteti.*;
import Iznimke.BazaPodatakaException;
import com.example.petshopprojekt.Run;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BazaPodataka {
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    private static final String DATABASE_FILE = "Baza.properties";
    private static Connection spajanjeNaBazu() throws SQLException, IOException {
        Properties svojstva = new Properties();
        svojstva.load(new FileReader(DATABASE_FILE));
        String urlBazePodataka = svojstva.getProperty("bazaPodatakaUrl");
        String korisnickoIme = svojstva.getProperty("korisnickoIme");
        String lozinka = svojstva.getProperty("lozinka");
        return DriverManager.getConnection(urlBazePodataka,
                korisnickoIme,lozinka);
    }


    public static List<Proizvod> dohvatiProizvode(Proizvod proizvod) throws
            BazaPodatakaException {
        List<Proizvod> listaProizvoda = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder(
                    "SELECT * FROM PROIZVOD WHERE 1 = 1");
            if (Optional.ofNullable(proizvod).isPresent()) {
                if (Optional.of(proizvod).map(
                        Proizvod::getId).isPresent()) {
                    sqlUpit.append(" AND ID = ").append(proizvod.getId());
                }
                if (!Optional.ofNullable(proizvod.getNaziv()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND NAZIV LIKE '%").append(proizvod.getNaziv()).append("%'");
                }
                if (Optional.ofNullable(proizvod.getCijena()).isPresent()) {
                    sqlUpit.append(" AND CIJENA LIKE '%").append(proizvod.getCijena()).append("%'");
                }
                if (Optional.ofNullable(proizvod.getKolicina()).isPresent()) {
                    sqlUpit.append(" AND KOLICINA LIKE '%").append(proizvod.getKolicina()).append("%'");
                }
                if (!Optional.ofNullable(proizvod.getOpis()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND OPIS LIKE '%").append(proizvod.getOpis()).append("%'");
                }
                if (Optional.ofNullable(proizvod.getSlika()).isPresent()) {
                    if (!Optional.of(proizvod.getSlika().getName()).map(
                            String::isBlank).orElse(true)) {
                        sqlUpit.append(" AND SLIKA LIKE '%").append(proizvod.getSlika().getName()).append("%'");
                    }
                }
            }
            Statement upit = veza.createStatement();
            ResultSet resultSet = upit.executeQuery(sqlUpit.toString());
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String naziv = resultSet.getString("naziv");
                Double cijena = resultSet.getDouble("cijena");
                Integer kolicina = resultSet.getInt("kolicina");
                String slika = resultSet.getString("slika");
                String opis = resultSet.getString("opis");
                Proizvod noviProizvod = new ProizvodBuilder().setId(id).setNaziv(naziv)
                        .setOpis(opis).setKolicina(kolicina).setCijena(cijena)
                        .setSlika(new File("src/main/resources/Slike/" + slika)).createProizvod();
                listaProizvoda.add(noviProizvod);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaProizvoda;
    }

    public static List<Proizvod> vratiSveProizvode() throws BazaPodatakaException {
        return dohvatiProizvode(new ProizvodBuilder().setId(null).setNaziv(null)
                .setOpis(null).setKolicina(null).setCijena(null).setSlika(null).createProizvod());
    }

    public static void spremiNoviProizvod(Proizvod proizvod) throws
            BazaPodatakaException {
        int i =0;
        String nazivSlike;
        if (Files.exists(Path.of("src/main/resources/Slike/"+proizvod.getSlika().getName()))){

            while (Files.exists(Path.of("src/main/resources/Slike/"+
                            proizvod.getSlika().getName().substring(0,proizvod.getSlika().getName().length()-4)+i
                    +proizvod.getSlika().getName().substring(proizvod.getSlika().getName().length()-4)))){
                i++;
            }
            nazivSlike = proizvod.getSlika().getName().substring(0,proizvod.getSlika().getName().length()-4)+i
                    +proizvod.getSlika().getName().substring(proizvod.getSlika().getName().length()-4);
        } else {
            nazivSlike =  proizvod.getSlika().getName();
        }



        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "INSERT INTO PROIZVOD(naziv, cijena, kolicina, opis,slika) VALUES (?, ?, ?, ?,?)");
            preparedStatement.setString(1, proizvod.getNaziv());
            preparedStatement.setString(2, proizvod.getCijena().toString());
            preparedStatement.setString(3, proizvod.getKolicina().toString());
            preparedStatement.setString(4, proizvod.getOpis());
            preparedStatement.setString(5, nazivSlike);
            preparedStatement.executeUpdate();
            Files.copy(proizvod.getSlika().toPath(),Path.of("src/main/resources/Slike/"+nazivSlike));
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void azurirajProizvod(Proizvod proizvod,Boolean bool) throws
            BazaPodatakaException {
        String nazivSlike ="";
        if (!bool) {
            int i = 0;

            if (Files.exists(Path.of("src/main/resources/Slike/" + proizvod.getSlika().getName()))) {

                while (Files.exists(Path.of("src/main/resources/Slike/" +
                        proizvod.getSlika().getName().substring(0, proizvod.getSlika().getName().length() - 4) + i
                        + proizvod.getSlika().getName()
                        .substring(proizvod.getSlika().getName().length() - 4)))) {
                    i++;
                }
                nazivSlike = proizvod.getSlika().getName().substring(0, proizvod.getSlika().getName().length() - 4) + i
                        + proizvod.getSlika().getName()
                        .substring(proizvod.getSlika().getName().length() - 4);
            } else {
                nazivSlike = proizvod.getSlika().getName();
            }

        }

        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "UPDATE PROIZVOD SET NAZIV = ? , CIJENA = ?, OPIS = ? , KOLICINA = ?, SLIKA = ? " +
                                    "WHERE ID ="+proizvod.getId());
            preparedStatement.setString(1, proizvod.getNaziv());
            preparedStatement.setString(2, proizvod.getCijena().toString());
            preparedStatement.setString(3, proizvod.getOpis());
            preparedStatement.setString(4, proizvod.getKolicina().toString());
            if (!bool){
                preparedStatement.setString(5, nazivSlike);
            } else {
                preparedStatement.setString(5, proizvod.getSlika().getName());
            }

            preparedStatement.executeUpdate();
            if (!bool){
            Files.copy(proizvod.getSlika().toPath(),Path.of("src/main/resources/Slike/"+nazivSlike));
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }
    public static void obrisiProizvod(Proizvod proizvod) throws
            IOException, SQLException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "DELETE FROM PROIZVOD WHERE id=?");
            preparedStatement.setString(1, String.valueOf(proizvod.getId()));
            Files.delete(proizvod.getSlika().toPath());
            preparedStatement.executeUpdate();
        }
    }



    public static List<Zivotinja> dohvatiZivotinje(Zivotinja zivotinja) throws
            BazaPodatakaException {
        List<Zivotinja> listaZivotinja = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder(
                    "SELECT * FROM ZIVOTINJE WHERE 1 = 1");
            if (Optional.ofNullable(zivotinja).isPresent()) {
                if (Optional.of(zivotinja).map(
                        Zivotinja::getId).isPresent()) {
                    sqlUpit.append(" AND ID = ").append(zivotinja.getId());
                }
                if (!Optional.ofNullable(zivotinja.getPasmina()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND PASMINA LIKE '%").append(zivotinja.getPasmina()).append("%'");
                }
                if (Optional.ofNullable(zivotinja.getVrsta()).isPresent()) {
                    sqlUpit.append(" AND VRSTA LIKE '%").append(zivotinja.getVrsta().getOpis()).append("%'");
                }
                if (Optional.ofNullable(zivotinja.getGodine()).isPresent()) {
                    sqlUpit.append(" AND GODINE LIKE '%").append(zivotinja.getGodine()).append("%'");
                }
                if (Optional.ofNullable(zivotinja.getSpol()).isPresent()) {
                    sqlUpit.append(" AND SPOL LIKE '%").append(zivotinja.getSpol().getOpis()).append("%'");
                }
                if (!Optional.ofNullable(zivotinja.getKontakt()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND KONTAKT LIKE '%").append(zivotinja.getKontakt()).append("%'");
                }
                if (Optional.ofNullable(zivotinja.getSlika()).isPresent()) {
                    if (!Optional.of(zivotinja.getSlika().getName()).map(
                            String::isBlank).orElse(true)) {
                        sqlUpit.append(" AND SLIKA LIKE '%").append(zivotinja.getSlika().getName()).append("%'");
                    }
                }
            }
            Statement upit = veza.createStatement();
            ResultSet resultSet = upit.executeQuery(sqlUpit.toString());
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String pasmina = resultSet.getString("pasmina");
                Integer godine = resultSet.getInt("godine");
                String kontakt = resultSet.getString("kontakt");
                String slika = resultSet.getString("slika");
                Vrsta vrsta = Metode.vratiVrstu(resultSet.getString("vrsta"));
                Spol spol = Metode.vratiSpol(resultSet.getString("spol"));
                Zivotinja novaZivotinja = new ZivotinjaBuilder().setId(id)
                        .setPasmina(pasmina).setGodine(godine).setSpol(spol)
                        .setVrsta(vrsta).setSlika(new File("src/main/resources/Slike/" + slika))
                        .setKontakt(kontakt).createZivotinja();
                listaZivotinja.add(novaZivotinja);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaZivotinja;
    }

    public static List<Zivotinja> vratiSveZivotinje() throws BazaPodatakaException {
        return dohvatiZivotinje(new ZivotinjaBuilder().setId(null).setPasmina(null)
                .setGodine(null).setSpol(null).setVrsta(null).setSlika(null)
                .setKontakt(null).createZivotinja());
    }

    public static void spremiNovuZivotinju(Zivotinja zivotinja) throws
            BazaPodatakaException {
        int i =0;
        String nazivSlike;
        if (Files.exists(Path.of("src/main/resources/Slike/"+zivotinja.getSlika().getName()))){

            while (Files.exists(Path.of("src/main/resources/Slike/"+
                    zivotinja.getSlika().getName().substring(0,zivotinja.getSlika().getName().length()-4)+i
                    +zivotinja.getSlika().getName().substring(zivotinja.getSlika().getName().length()-4)))){
                i++;
            }
            nazivSlike = zivotinja.getSlika().getName().substring(0,zivotinja.getSlika().getName().length()-4)+i
                    +zivotinja.getSlika().getName().substring(zivotinja.getSlika().getName().length()-4);
        } else {
            nazivSlike =  zivotinja.getSlika().getName();
        }



        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "INSERT INTO ZIVOTINJE(vrsta, pasmina, godine, spol,slika,kontakt) VALUES (?,?,?,?,?,?)");
            preparedStatement.setString(1, zivotinja.getVrsta().getOpis());
            preparedStatement.setString(2, zivotinja.getPasmina());
            preparedStatement.setString(3, zivotinja.getGodine().toString());
            preparedStatement.setString(4, zivotinja.getSpol().getOpis());
            preparedStatement.setString(5, nazivSlike);
            preparedStatement.setString(6,zivotinja.getKontakt());
            preparedStatement.executeUpdate();
            Files.copy(zivotinja.getSlika().toPath(),Path.of("src/main/resources/Slike/"+nazivSlike));
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void azurirajZivotinju(Zivotinja zivotinja,Boolean bool) throws
            BazaPodatakaException {
        String nazivSlike ="";
        if (!bool) {
            int i = 0;

            if (Files.exists(Path.of("src/main/resources/Slike/" + zivotinja.getSlika().getName()))) {

                while (Files.exists(Path.of("src/main/resources/Slike/" +
                        zivotinja.getSlika().getName().substring(0, zivotinja.getSlika().getName().length() - 4) + i
                        + zivotinja.getSlika().getName()
                        .substring(zivotinja.getSlika().getName().length() - 4)))) {
                    i++;
                }
                nazivSlike = zivotinja.getSlika().getName().substring(0, zivotinja.getSlika().getName().length() - 4) + i
                        + zivotinja.getSlika().getName()
                        .substring(zivotinja.getSlika().getName().length() - 4);
            } else {
                nazivSlike = zivotinja.getSlika().getName();
            }

        }

        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "UPDATE ZIVOTINJE SET VRSTA = ? , PASMINA = ?, SPOL = ? , GODINE = ?, SLIKA = ?, " +
                                    "KONTAKT = ? WHERE ID ="+zivotinja.getId());
            preparedStatement.setString(1, zivotinja.getVrsta().getOpis());
            preparedStatement.setString(2, zivotinja.getPasmina());
            preparedStatement.setString(3, zivotinja.getSpol().getOpis());
            preparedStatement.setString(4, zivotinja.getGodine().toString());
            if (!bool){
                preparedStatement.setString(5, nazivSlike);
            } else {
                preparedStatement.setString(5, zivotinja.getSlika().getName());
            }
            preparedStatement.setString(6,zivotinja.getKontakt());
            preparedStatement.executeUpdate();
            if (!bool){
                Files.copy(zivotinja.getSlika().toPath(),Path.of("src/main/resources/Slike/"+nazivSlike));
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }
    public static void obrisiZivotinju(Zivotinja zivotinja) throws
            IOException, SQLException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "DELETE FROM ZIVOTINJE WHERE id=?");
            preparedStatement.setString(1, String.valueOf(zivotinja.getId()));
            Files.delete(zivotinja.getSlika().toPath());
            preparedStatement.executeUpdate();
        }
    }



    public static List<Zaposlenik> dovatiZaposlenike(Zaposlenik zaposlenik) throws
            BazaPodatakaException {
        List<Zaposlenik> listaZaposlenika = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder(
                    "SELECT * FROM Zaposlenik WHERE 1 = 1");
            if (Optional.ofNullable(zaposlenik).isPresent()) {
                if (Optional.of(zaposlenik).map(
                        Zaposlenik::getId).isPresent()) {
                    sqlUpit.append(" AND ID = ").append(zaposlenik.getId());
                }
                if (!Optional.ofNullable(zaposlenik.getIme()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND IME LIKE '%").append(zaposlenik.getIme()).append("%'");
                }
                if (!Optional.ofNullable(zaposlenik.getPrezime()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND PREZIME LIKE '%").append(zaposlenik.getPrezime()).append("%'");
                }
                if (Optional.ofNullable(zaposlenik.getDatumRodjenja()).isPresent()) {
                    sqlUpit.append(" AND DATUM_RODJENJA LIKE '%").append(zaposlenik.getDatumRodjenja()
                            .format((DateTimeFormatter.ofPattern("yyyy-MM-dd")))).append("%'");
                }
                if (!Optional.ofNullable(zaposlenik.getKorisnickoIme()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND KORISNICKO_IME LIKE '%").append(zaposlenik.getKorisnickoIme()).append("%'");
                }
                if (Optional.ofNullable(zaposlenik.getSlika()).isPresent()) {
                    if (!Optional.of(zaposlenik.getSlika().getName()).map(
                            String::isBlank).orElse(true)) {
                        sqlUpit.append(" AND SLIKA LIKE '%").append(zaposlenik.getSlika().getName()).append("%'");
                    }
                }
            }
            Statement upit = veza.createStatement();
            ResultSet resultSet = upit.executeQuery(sqlUpit.toString());
            while (resultSet.next()) {
                String lozinka = null;
                Long id = resultSet.getLong("id");
                String korisnickoIme = resultSet.getString("korisnicko_ime");
                String ime = resultSet.getString("ime");
                String prezime = resultSet.getString("prezime");
                String slika = resultSet.getString("slika");
                LocalDate datumRodjenja = LocalDate.
                        parse(resultSet.getString("datum_rodjenja"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (korisnickoIme != null){
                    if (!korisnickoIme.isBlank()) {
                        lozinka = Login.vratiLozinku(korisnickoIme);
                    }
                }

                Zaposlenik noviZaposlenik = new ZaposlenikBuilder().setId(id).setIme(ime)
                        .setPrezime(prezime).setKorisnickoIme(korisnickoIme).setLozinka(lozinka)
                        .setDatumRodjenja(datumRodjenja)
                        .setSlika(new File("src/main/resources/Slike/" + slika))
                        .createZaposlenik();
                listaZaposlenika.add(noviZaposlenik);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaZaposlenika;
    }

    public static List<Zaposlenik> vratiSveZaposlenike() throws BazaPodatakaException {
        return dovatiZaposlenike(new ZaposlenikBuilder().setId(null)
                .setIme(null).setPrezime(null).setKorisnickoIme(null)
                .setLozinka(null).setDatumRodjenja(null).setSlika(null).createZaposlenik());
    }

    public static void spremiNovogZaposlenika(Zaposlenik zaposlenik) throws
            BazaPodatakaException {
        int i =0;
        String nazivSlike;
        if (Files.exists(Path.of("src/main/resources/Slike/"+zaposlenik.getSlika().getName()))){

            while (Files.exists(Path.of("src/main/resources/Slike/"+
                    zaposlenik.getSlika().getName().substring(0,zaposlenik.getSlika().getName().length()-4)+i
                    +zaposlenik.getSlika().getName().substring(zaposlenik.getSlika().getName().length()-4)))){
                i++;
            }
            nazivSlike = zaposlenik.getSlika().getName().substring(0,zaposlenik.getSlika().getName().length()-4)+i
                    +zaposlenik.getSlika().getName().substring(zaposlenik.getSlika().getName().length()-4);
        } else {
            nazivSlike =  zaposlenik.getSlika().getName();
        }



        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "INSERT INTO ZAPOSLENIK(ime, prezime, korisnicko_ime,datum_rodjenja,slika) " +
                                    "VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, zaposlenik.getIme());
            preparedStatement.setString(2, zaposlenik.getPrezime());
            preparedStatement.setString(3, zaposlenik.getKorisnickoIme());
            preparedStatement.setString(4, zaposlenik.getDatumRodjenja()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            preparedStatement.setString(5, nazivSlike);
            preparedStatement.executeUpdate();
            Files.copy(zaposlenik.getSlika().toPath(),Path.of("src/main/resources/Slike/"+nazivSlike));
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void azurirajZaposlenika(Zaposlenik zaposlenik,Boolean bool) throws
            BazaPodatakaException {
        String nazivSlike ="";
        if (!bool) {
            int i = 0;

            if (Files.exists(Path.of("src/main/resources/Slike/" + zaposlenik.getSlika().getName()))) {

                while (Files.exists(Path.of("src/main/resources/Slike/" +
                        zaposlenik.getSlika().getName().substring(0, zaposlenik.getSlika().getName().length() - 4) + i
                        + zaposlenik.getSlika().getName().substring(zaposlenik.getSlika().getName().length() - 4)))) {
                    i++;
                }
                nazivSlike = zaposlenik.getSlika().getName().substring(0, zaposlenik.getSlika().getName().length() - 4) + i
                        + zaposlenik.getSlika().getName().substring(zaposlenik.getSlika().getName().length() - 4);
            } else {
                nazivSlike = zaposlenik.getSlika().getName();
            }

        }

        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "UPDATE ZAPOSLENIK SET IME = ? , PREZIME = ?, KORISNICKO_IME = ? , DATUM_RODJENJA = ?," +
                                    " SLIKA = ? WHERE ID ="+zaposlenik.getId());
            preparedStatement.setString(1, zaposlenik.getIme());
            preparedStatement.setString(2, zaposlenik.getPrezime());
            preparedStatement.setString(3, zaposlenik.getKorisnickoIme());
            preparedStatement.setString(4, zaposlenik.getDatumRodjenja()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if (!bool){
                preparedStatement.setString(5, nazivSlike);
            } else {
                preparedStatement.setString(5, zaposlenik.getSlika().getName());
            }
            preparedStatement.executeUpdate();
            if (!bool){
                Files.copy(zaposlenik.getSlika().toPath(),Path.of("src/main/resources/Slike/"+nazivSlike));
            }

        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }
    public static void obrisiZaposlenika(Zaposlenik zaposlenik) throws
            IOException, SQLException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "DELETE FROM ZAPOSLENIK WHERE id=?");
            preparedStatement.setString(1, String.valueOf(zaposlenik.getId()));
            Files.delete(zaposlenik.getSlika().toPath());
            preparedStatement.executeUpdate();
        }
    }

    public static List<Korisnik> dohvatiKorisnike(Korisnik korisnik) throws
            BazaPodatakaException {
        List<Korisnik> listaKorisnika = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder(
                    "SELECT * FROM KORISNIK WHERE 1 = 1");
            if (Optional.ofNullable(korisnik).isPresent()) {
                if (Optional.of(korisnik).map(
                        Korisnik::getId).isPresent()) {
                    sqlUpit.append(" AND ID = ").append(korisnik.getId());
                }
                if (!Optional.ofNullable(korisnik.getIme()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND IME LIKE '%").append(korisnik.getIme()).append("%'");
                }
                if (!Optional.ofNullable(korisnik.getPrezime()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND PREZIME LIKE '%").append(korisnik.getPrezime()).append("%'");
                }
                if (Optional.ofNullable(korisnik.getDatumRodjenja()).isPresent()) {
                    sqlUpit.append(" AND DATUM_RODJENJA LIKE '%")
                            .append(korisnik.getDatumRodjenja().format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                            .append("%'");
                }
                if (!Optional.ofNullable(korisnik.getKorisnickoIme()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND KORISNICKO_IME LIKE '%").append(korisnik.getKorisnickoIme()).append("%'");
                }
                if (!Optional.ofNullable(korisnik.getAdresa()).map(
                        String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND ADRESA LIKE '%").append(korisnik.getAdresa()).append("%'");
                }
            }
            Statement upit = veza.createStatement();
            ResultSet resultSet = upit.executeQuery(sqlUpit.toString());
            while (resultSet.next()) {
                String lozinka = null;
                Long id = resultSet.getLong("id");
                String korisnickoIme = resultSet.getString("korisnicko_ime");
                String ime = resultSet.getString("ime");
                String prezime = resultSet.getString("prezime");
                LocalDate datumRodjenja = LocalDate.
                        parse(resultSet.getString("datum_rodjenja"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String adresa = resultSet.getString("adresa");
                if (korisnickoIme != null){
                    if (!korisnickoIme.isBlank()) {
                        lozinka = Login.vratiLozinku(korisnickoIme);
                    }
                }
                Korisnik noviKorisnik = new KorisnikBuilder().setId(id).setIme(ime)
                        .setPrezime(prezime).setKorisnickoIme(korisnickoIme).setLozinka(lozinka)
                        .setDatumRodjenja(datumRodjenja).setAdresa(adresa).createKorisnik();
                listaKorisnika.add(noviKorisnik);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaKorisnika;
    }

    public static List<Korisnik> vratiSveKorisnike() throws BazaPodatakaException {
        return dohvatiKorisnike(new KorisnikBuilder().setId(null).setIme(null)
                .setPrezime(null).setKorisnickoIme(null).setLozinka(null)
                .setDatumRodjenja(null).setAdresa(null).createKorisnik());
    }

    public static void spremiNovogKorisnika(Korisnik korisnik) throws
            BazaPodatakaException {

        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "INSERT INTO KORISNIK(ime, prezime, korisnicko_ime,datum_rodjenja,adresa) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, korisnik.getIme());
            preparedStatement.setString(2, korisnik.getPrezime());
            preparedStatement.setString(3, korisnik.getKorisnickoIme());
            preparedStatement.setString(4, korisnik.getDatumRodjenja()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            preparedStatement.setString(5, korisnik.getAdresa());
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void azurirajKorisnika(Korisnik korisnik) throws
            BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "UPDATE KORISNIK SET IME = ? , PREZIME = ?, KORISNICKO_IME = ? , DATUM_RODJENJA = ?," +
                                    " ADRESA = ? WHERE ID ="+korisnik.getId());
            preparedStatement.setString(1, korisnik.getIme());
            preparedStatement.setString(2, korisnik.getPrezime());
            preparedStatement.setString(3, korisnik.getKorisnickoIme());
            preparedStatement.setString(4, korisnik.getDatumRodjenja()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            preparedStatement.setString(5, korisnik.getAdresa());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }
    public static void obrisiKorisnika(Korisnik korisnik) throws
            IOException, SQLException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza
                    .prepareStatement(
                            "DELETE FROM KORISNIK WHERE id=?");
            preparedStatement.setString(1, String.valueOf(korisnik.getId()));
            preparedStatement.executeUpdate();
        }
    }
}
