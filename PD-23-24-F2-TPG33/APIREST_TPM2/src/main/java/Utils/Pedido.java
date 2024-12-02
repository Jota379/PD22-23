package Utils;

import java.io.Serializable;

public class Pedido implements Serializable {
    public String cmd;
    public Object o;

    public Pedido(String cmd, Object o) {
        this.cmd = cmd;
        this.o = o;
    }
}
