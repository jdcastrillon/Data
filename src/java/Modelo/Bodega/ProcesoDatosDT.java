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

    private int nro_detalle_pro;
    private int nro_proceso;
    private int cod_articulo;
    private String nombre;
    private int stock;
    private int cantidad;
    private int ajuste;
    private int linea;


    

    public ProcesoDatosDT() {
    }

    public ProcesoDatosDT(int nro_proceso, int cod_articulo, String nombre, int stock, int cantidad, int ajuste) {
        this.nro_proceso = nro_proceso;
        this.cod_articulo = cod_articulo;
        this.nombre = nombre;
        this.stock = stock;
        this.cantidad = cantidad;
        this.ajuste = ajuste;
    }
      


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public int getAjuste() {
        return ajuste;
    }

    public void setAjuste(int ajuste) {
        this.ajuste = ajuste;
    }

    public int getNro_detalle_pro() {
        return nro_detalle_pro;
    }

    public void setNro_detalle_pro(int nro_detalle_pro) {
        this.nro_detalle_pro = nro_detalle_pro;
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

  
}
