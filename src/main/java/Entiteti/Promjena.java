package Entiteti;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Promjena<T,K> implements Serializable {
    private final T pocetni;
    private final T promijenjen;
    private final K rola;
    private final LocalDateTime vrijeme;

    public Promjena(T pocetni, T promijenjen, K rola) {
        this.pocetni = pocetni;
        this.promijenjen = promijenjen;
        this.rola = rola;
        this.vrijeme = LocalDateTime.now();
    }

    public T getPocetni() {
        return pocetni;
    }

    public T getPromijenjen() {
        return promijenjen;
    }

    public K getRola() {
        return rola;
    }

    public LocalDateTime getVrijeme() {
        return vrijeme;
    }

}
