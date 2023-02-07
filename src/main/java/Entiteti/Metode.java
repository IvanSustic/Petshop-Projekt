package Entiteti;

import com.example.petshopprojekt.Run;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Metode {

    static String hashirajLozinku(String lozinka){
        StringBuilder hashiranaLozinka;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(lozinka.getBytes());
            byte[] bytes = md.digest();
            BigInteger no = new BigInteger(1, bytes);
            hashiranaLozinka = new StringBuilder(no.toString(16));
            while (hashiranaLozinka.length() < 32) {
                hashiranaLozinka.insert(0, "0");
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashiranaLozinka.toString();
    }

    static File odaberiSliku(){
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
                ,new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
        );

        return fileChooser.showOpenDialog(Run.getMainStage());

    }

    static Vrsta vratiVrstu(String opis){
        return switch (opis) {
            case "Pas" -> Vrsta.PAS;
            case "Mačka" -> Vrsta.MACKA;
            case "Gmaz" -> Vrsta.GMAZ;
            case "Zamorac" -> Vrsta.ZAMORAC;
            case "Ptica" -> Vrsta.PTICA;
            default -> null;
        };
    }

    static Spol vratiSpol(String opis){
        return switch (opis) {
            case "Žensko" -> Spol.F;
            case "Muško" -> Spol.M;
            default -> null;
        };
    }

    static boolean provjeriMail(String mail){
        String emailRegex ="^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPat.matcher(mail);
        return  matcher.find();
    }

    static boolean provjeriBroj(String tekst){
        return tekst.matches("[0-9]+");

    }

    static boolean provjeriDouble(String tekst){
       return tekst.matches("\\d*\\.?\\d+");
    }

}
