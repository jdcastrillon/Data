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
public class TrasladoBodegaDT implements Serializable {

    private int trans;
    private int cod_articulo;
    private String cod_ubicacion;
    private String cod_unidad;
    private int cantidad;
    private int linea;
    transient private String codigo;
    transient private String nom_articulo;
    transient private int stock;

    public TrasladoBodegaDT() {
    }

    public TrasladoBodegaDT(int cod_articulo, String codigo, String nom_articulo) {
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

    public String getCod_ubicacion() {
        return cod_ubicacion;
    }

    public void setCod_ubicacion(String cod_ubicacion) {
        this.cod_ubicacion = cod_ubicacion;
    }

    public String getCod_unidad() {
        return cod_unidad;
    }

    public void setCod_unidad(String cod_unidad) {
        this.cod_unidad = cod_unidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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
