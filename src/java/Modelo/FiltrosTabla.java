/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class FiltrosTabla implements Serializable {

    private int codigoBusqueda;
    private String nombreBusqueda;

    public FiltrosTabla() {
    }

    public FiltrosTabla(int codigoBusqueda, String nombreBusqueda) {
        this.codigoBusqueda = codigoBusqueda;
        this.nombreBusqueda = nombreBusqueda;
    }
    
    

    public int getCodigoBusqueda() {
        return codigoBusqueda;
    }

    public void setCodigoBusqueda(int codigoBusqueda) {
        this.codigoBusqueda = codigoBusqueda;
    }

    public String getNombreBusqueda() {
        return nombreBusqueda;
    }

    public void setNombreBusqueda(String nombreBusqueda) {
        this.nombreBusqueda = nombreBusqueda;
    }

    @Override
    public String toString() {
        return "FiltrosTabla{" + "codigoBusqueda=" + codigoBusqueda + ", nombreBusqueda=" + nombreBusqueda + '}';
    }

}
