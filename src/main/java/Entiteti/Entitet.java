package Entiteti;

import java.io.Serializable;

public abstract class Entitet implements Serializable {
    private Long id;

    public Entitet(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
