package Entiteti;

import java.util.List;

public class KupljeniProizvodi {

    private boolean pristup = false;

    public synchronized List<Promjena<Proizvod, Korisnik>> dohvati() {
        while (pristup) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread Interrupted");
            }
        }

        pristup = true;


        notifyAll();

        return new Serijalizacija<Promjena<Proizvod, Korisnik>>().ucitaj("Datoteke/kupljeniProizvodi.dat");

    }

    public synchronized void poslaji(List<Proizvod> proizvodiUKosarici,Korisnik korisnik) {
        while (!pristup) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread Interrupted");
            }
        }
        pristup = false;
        proizvodiUKosarici.forEach(proizvod -> {
            Serijalizacija<Promjena<Proizvod, Korisnik>> promjenaSerijalizacija = new Serijalizacija<>();
            Promjena<Proizvod, Korisnik> promjena = new PromjenaBuilder().setPocetni(null)
                    .setPromijenjen(proizvod).setRola(korisnik).createPromjena();

            promjenaSerijalizacija.serijaliziraj(promjena, "Datoteke/kupljeniProizvodi.dat");

        });
        notifyAll();
    }
}
