/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Comercial;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class FacturasDT implements Serializable {

    private int trans;
    private int cod_articulo;
    private String cod_deposito;
    private String cod_ubicacion;
    private int cod_unidad;
    private int cantidad;
    private double imp_fact;
    private double imp_neto;
    private int signo;
    private String cod_impu;
    private double porc_imp;
    private double imp_impuesto;
    private double imp_descuento;
    private double imp_total;
    private int linea;

    transient private String codigo;
    transient private String nom_articulo;
    transient private int stock;

    public FacturasDT() {
    }

    public FacturasDT(int cod_articulo, String codigo, String nom_articulo, int cantidad) {
        this.cod_articulo = cod_articulo;
        this.codigo = codigo;
        this.nom_articulo = nom_articulo;
        this.cantidad = cantidad;
        this.imp_fact=1500;
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

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getCod_ubicacion() {
        return cod_ubicacion;
    }

    public void setCod_ubicacion(String cod_ubicacion) {
        this.cod_ubicacion = cod_ubicacion;
    }

    public int getCod_unidad() {
        return cod_unidad;
    }

    public void setCod_unidad(int cod_unidad) {
        this.cod_unidad = cod_unidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getImp_fact() {
        return imp_fact;
    }

    public void setImp_fact(double imp_fact) {
        this.imp_fact = imp_fact;
    }

    public double getImp_neto() {
        return imp_neto;
    }

    public void setImp_neto(double imp_neto) {
        this.imp_neto = imp_neto;
    }

    public int getSigno() {
        return signo;
    }

    public void setSigno(int signo) {
        this.signo = signo;
    }

    public String getCod_impu() {
        return cod_impu;
    }

    public void setCod_impu(String cod_impu) {
        this.cod_impu = cod_impu;
    }

    public double getPorc_imp() {
        return porc_imp;
    }

    public void setPorc_imp(double porc_imp) {
        this.porc_imp = porc_imp;
    }

    public double getImp_impuesto() {
        return imp_impuesto;
    }

    public void setImp_impuesto(double imp_impuesto) {
        this.imp_impuesto = imp_impuesto;
    }

    public double getImp_descuento() {
        return imp_descuento;
    }

    public void setImp_descuento(double imp_descuento) {
        this.imp_descuento = imp_descuento;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNom_articulo() {
        return nom_articulo;
    }

    public void setNom_articulo(String nom_articulo) {
        this.nom_articulo = nom_articulo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
