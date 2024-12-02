package Utils;

import java.io.Serializable;

public class Codigo implements Serializable {
    public int id;
    public String evento;
    public int validade;
    public String t;

    public Codigo(int id, String evento, int validade, String t) {
        this.id = id;
        this.evento = evento;
        this.validade = validade;
        this.t = t;
    }
}
