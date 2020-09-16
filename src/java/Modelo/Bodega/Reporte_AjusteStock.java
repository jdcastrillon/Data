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
public class Reporte_AjusteStock implements Serializable {

    private int trans;
    private String cod_emp;
    private String nom_emp;
    private String fec_doc;
    private int nro_docum;
    private String cod_estado;
    private String cod_deposito;
    private String nom_deposito;
    private String observacion;
    private String operacion;
    private String cod_articulo;
    private String nom_articulo;
    private int cantidad;

    public Reporte_AjusteStock() {
    }
    

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getNom_emp() {
        return nom_emp;
    }

    public void setNom_emp(String nom_emp) {
        this.nom_emp = nom_emp;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public int getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(int nro_docum) {
        this.nro_docum = nro_docum;
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getNom_deposito() {
        return nom_deposito;
    }

    public void setNom_deposito(String nom_deposito) {
        this.nom_deposito = nom_deposito;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
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

}
