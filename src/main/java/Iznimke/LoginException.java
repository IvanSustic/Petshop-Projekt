package Iznimke;

import java.io.IOException;

public class LoginException extends IOException {
    public LoginException() {
        super("Pogreška kod dohvačanja lozinki.");
    }
}
