package Entiteti;

import com.example.petshopprojekt.Run;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Serijalizacija<T> implements Serijaliziraj<T> {
    private static  final Logger logger = LoggerFactory.getLogger(Run.class);
    @Override
    public void serijaliziraj(T objekt, String datoteka) {
        List<T> lista = new ArrayList<>();
        if (ucitaj(datoteka)!=null) {
            lista.addAll(ucitaj(datoteka));
        }
        lista.add(objekt);
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(datoteka))) {
            out.writeObject(lista);
        } catch (IOException ex) {
            logger.info(ex.getMessage());

        }
    }


    @Override
    public List<T> ucitaj(String datoteka) {

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(datoteka))) {
            return (List<T>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }
}
