/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TallerPrimos;

import java.io.Serializable;

/**
 *
 * @author shamuel
 */
public class DatosBusqueda implements Serializable{
    private String name;
    private int vInicial;
    private int vFInal;
    

    public DatosBusqueda(String name, int vInicial, int vFInal) {
        this.name = name;
        this.vInicial = vInicial;
        this.vFInal = vFInal;
    }

    public String getName() {
        return name;
    }

    public int getvInicial() {
        return vInicial;
    }

    public int getvFInal() {
        return vFInal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setvInicial(int vInicial) {
        this.vInicial = vInicial;
    }

    public void setvFInal(int vFInal) {
        this.vFInal = vFInal;
    }
    

    
}
