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
public class DashboardSK_Depostio implements Serializable {

    private String cod_emp;
    private String cod_tit;
    private String cod_deposito;
    private String nom_deposito;
    private String cod_estado;
    private String cod_articulo;
    private String nom_articulo;
    private int cantidad;

    public DashboardSK_Depostio() {
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getCod_tit() {
        return cod_tit;
    }

    public void setCod_tit(String cod_tit) {
        this.cod_tit = cod_tit;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(String cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public String getNom_articulo() {
        return nom_articulo;
    }

    public void setNom_articulo(String nom_articulo) {
        this.nom_articulo = nom_articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNom_deposito() {
        return nom_deposito;
    }

    public void setNom_deposito(String nom_deposito) {
        this.nom_deposito = nom_deposito;
    }
    

}
