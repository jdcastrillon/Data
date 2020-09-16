/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Compras;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class ComprasDT implements Serializable {

    private int trans;
    private int cod_articulo;
    private int stock;
    private int cantidad;
    private double imp_costo;
    private String impuesto;
    private float porc_imp;
    private double imp_impuesto;
    private double imp_neto;
    private double imp_total;
    private int linea;

    transient private String codigo;
    transient private String nom_articulo;

    public ComprasDT() {
    }

    public ComprasDT(int cod_articulo, String codigo, String nom_articulo) {
        this.cod_articulo = cod_articulo;
        this.codigo = codigo;
        this.nom_articulo = nom_articulo;
    }

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public int getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(int cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getImp_costo() {
        return imp_costo;
    }

    public void setImp_costo(double imp_costo) {
        this.imp_costo = imp_costo;
    }

    public String getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(String impuesto) {
        this.impuesto = impuesto;
    }

    public float getPorc_imp() {
        return porc_imp;
    }

    public void setPorc_imp(float porc_imp) {
        this.porc_imp = porc_imp;
    }

    public double getImp_impuesto() {
        return imp_impuesto;
    }

    public void setImp_impuesto(double imp_impuesto) {
        this.imp_impuesto = imp_impuesto;
    }

    public double getImp_neto() {
        return imp_neto;
    }

    public void setImp_neto(double imp_neto) {
        this.imp_neto = imp_neto;
    }

    public double getImp_total() {
        return imp_total;
    }

    public void setImp_total(double imp_total) {
        this.imp_total = imp_total;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public String getNom_articulo() {
        return nom_articulo;
    }

    public void setNom_articulo(String nom_articulo) {
        this.nom_articulo = nom_articulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
