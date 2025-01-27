package Utils;

import java.io.Serializable;

public class Utilizador{
    private String nome;
    private int id;
    private String email;
    private String password;
    private int admin;

    public Utilizador() {
    }

    public Utilizador(String email) {
        this.email = email;
    }

    public Utilizador(String nome, int id, String email, String password) {
        this.nome = nome;
        this.id = id;
        this.email = email;
        this.password = password;
        this.admin = 0;
    }

    public Utilizador(String nome, int id, String email, String password, int admin) {
        this.nome = nome;
        this.id = id;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public Utilizador(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
