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
    private String cambia_codigo;
    private String codigonew;
    private int cod_articulo2;
    private String nom_articulo;

    transient private String codigo;
    transient private String nuevoCodigo;


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

    public String getCambia_codigo() {
        return cambia_codigo;
    }

    public void setCambia_codigo(String cambia_codigo) {
        this.cambia_codigo = cambia_codigo;
    }

    public String getCodigonew() {
        return codigonew;
    }

    public void setCodigonew(String codigonew) {
        this.codigonew = codigonew;
    }

    public int getCod_articulo2() {
        return cod_articulo2;
    }

    public void setCod_articulo2(int cod_articulo2) {
        this.cod_articulo2 = cod_articulo2;
    }

    public String getNuevoCodigo() {
        return nuevoCodigo;
    }

    public void setNuevoCodigo(String nuevoCodigo) {
        this.nuevoCodigo = nuevoCodigo;
    }

    @Override
    public String toString() {
        return "ComprasDT{" + "trans=" + trans + ", cod_articulo=" + cod_articulo + ", stock=" + stock + ", cantidad=" + cantidad + ", imp_costo=" + imp_costo + ", impuesto=" + impuesto + ", porc_imp=" + porc_imp + ", imp_impuesto=" + imp_impuesto + ", imp_neto=" + imp_neto + ", imp_total=" + imp_total + ", linea=" + linea + ", codigo=" + codigo + ", nom_articulo=" + nom_articulo + '}';
    }

}
