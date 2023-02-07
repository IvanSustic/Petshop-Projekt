package Entiteti;

import java.util.List;

public interface Serijaliziraj< T> {
    void serijaliziraj(T objekt ,String datoteka);
    List<T> ucitaj(String datoteka);

}
