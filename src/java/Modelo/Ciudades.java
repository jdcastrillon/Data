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
public class Ciudades implements Serializable {

    private int cod_ciudad;
    private String dpto;
    private String pais;
    private String ciudadDpto;

    public Ciudades() {
    }

    public Ciudades(int cod_ciudad, String ciudadDpto) {
        this.cod_ciudad = cod_ciudad;
        this.ciudadDpto = ciudadDpto;
    }

    public int getCod_ciudad() {
        return cod_ciudad;
    }

    public void setCod_ciudad(int cod_ciudad) {
        this.cod_ciudad = cod_ciudad;
    }

    public String getDpto() {
        return dpto;
    }

    public void setDpto(String dpto) {
        this.dpto = dpto;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudadDpto() {
        return ciudadDpto;
    }

    public void setCiudadDpto(String ciudadDpto) {
        this.ciudadDpto = ciudadDpto;
    }

    @Override
    public String toString() {
        return "Ciudades{" + "cod_ciudad=" + cod_ciudad + ", dpto=" + dpto + ", pais=" + pais + ", ciudadDpto=" + ciudadDpto + '}';
    }

}
