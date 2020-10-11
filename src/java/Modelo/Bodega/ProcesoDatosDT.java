/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import java.io.Serializable;

/**
 *
 * @author Mauricio Herrera
 */
public class ProcesoDatosDT implements Serializable {

    private int nro_proceso;
    private String bodega;
    private String categoria;
    private String sub_categoria;
    private int cod_articulo;
    private String nom_articulo;
    private int stock;
    private int cantidad;
    private int ajuste;
    private int linea;

    public ProcesoDatosDT() {
    }

    public ProcesoDatosDT(int nro_proceso, String bodega, String categoria, String sub_categoria, int cod_articulo, String nom_articulo, int stock, int cantidad, int ajuste) {
        this.nro_proceso = nro_proceso;
        this.bodega = bodega;
        this.categoria = categoria;
        this.sub_categoria = sub_categoria;
        this.cod_articulo = cod_articulo;
        this.nom_articulo = nom_articulo;
        this.cantidad = cantidad;
        this.stock = stock;
        this.ajuste = ajuste;
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

    public int getAjuste() {
        return ajuste;
    }

    public void setAjuste(int ajuste) {
        this.ajuste = ajuste;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getNro_proceso() {
        return nro_proceso;
    }

    public void setNro_proceso(int nro_proceso) {
        this.nro_proceso = nro_proceso;
    }

    public int getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(int cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public String getBodega() {
        return bodega;
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSub_categoria() {
        return sub_categoria;
    }

    public void setSub_categoria(String sub_categoria) {
        this.sub_categoria = sub_categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
