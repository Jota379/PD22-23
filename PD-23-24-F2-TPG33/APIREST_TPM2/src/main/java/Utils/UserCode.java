package Utils;

import java.io.Serializable;

public class UserCode implements Serializable {
    public String email;
    public int codigo;

    public UserCode(String email, int codigo) {
        this.email = email;
        this.codigo = codigo;
    }
}
