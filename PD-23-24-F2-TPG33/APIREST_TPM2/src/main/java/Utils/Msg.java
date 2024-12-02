package Utils;

import java.io.Serializable;

public class Msg implements Serializable {
    public static final long serialVersionUID = 1010;
    public int portoReg;//porto do registry
    public String serviçoRMI;
    public int versao;

    public Msg() {
        versao = -1;
    }

    public Msg(int portoReg, String serviçoRMI, int versao) {
        this.portoReg = portoReg;
        this.serviçoRMI = serviçoRMI;
        this.versao = versao;
    }

    public int getPortoReg() {
        return portoReg;
    }

    public String getServiçoRMI() {
        return serviçoRMI;
    }

    public int getVersao() {
        return versao;
    }

    public void setVersao(int versao) {
        this.versao = versao;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
