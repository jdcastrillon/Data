/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class ProConteoDT implements Serializable {

    private int nro_art_conteo;
    private int nro_conteo;
    private int cod_articulo;
    private int linea;

    transient private String nom_articulo;

    public ProConteoDT() {
    }

    public ProConteoDT(int nro_art_conteo, int nro_conteo, int cod_articulo, String nom_articulo) {
        this.nro_art_conteo = nro_art_conteo;
        this.cod_articulo = cod_articulo;
        this.nro_conteo = nro_conteo;
        this.nom_articulo = nom_articulo;
    }

    public int getNro_art_conteo() {
        return nro_art_conteo;
    }

    public void setNro_art_conteo(int nro_art_conteo) {
        this.nro_art_conteo = nro_art_conteo;
    }

    public int getNro_conteo() {
        return nro_conteo;
    }

    public void setNro_conteo(int nro_conteo) {
        this.nro_conteo = nro_conteo;
    }

    public int getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(int cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public String getNom_articulo() {
        return nom_articulo;
    }

    public void setNom_articulo(String nom_articulo) {
        this.nom_articulo = nom_articulo;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

}
