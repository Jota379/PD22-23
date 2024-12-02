package Utils;

public class Evento{
    public String designacao;
    public String local;
    public String data;
    public String hora_ini;
    public String hora_fim;

    public String getDesignacao() {
        return designacao;
    }

    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora_ini() {
        return hora_ini;
    }

    public void setHora_ini(String hora_ini) {
        this.hora_ini = hora_ini;
    }

    public String getHora_fim() {
        return hora_fim;
    }

    public void setHora_fim(String hora_fim) {
        this.hora_fim = hora_fim;
    }

    public Evento(String designacao, String local, String data, String hora_ini, String hora_fim) {
        this.designacao = designacao;
        this.local = local;
        this.data = data;
        this.hora_ini = hora_ini;
        this.hora_fim = hora_fim;
    }
}
