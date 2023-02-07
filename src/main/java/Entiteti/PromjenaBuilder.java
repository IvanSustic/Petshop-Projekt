package Entiteti;

public class PromjenaBuilder<T, K> {
    private T pocetni;
    private T promijenjen;
    private K rola;

    public PromjenaBuilder setPocetni(T pocetni) {
        this.pocetni = pocetni;
        return this;
    }

    public PromjenaBuilder setPromijenjen(T promijenjen) {
        this.promijenjen = promijenjen;
        return this;
    }

    public PromjenaBuilder setRola(K rola) {
        this.rola = rola;
        return this;
    }

    public Promjena createPromjena() {
        return new Promjena(pocetni, promijenjen, rola);
    }
}