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
public class Dashboard_stock implements Serializable {

    private String cod_emp;
    private String cod_deposito;
    private String cod_estado;
    private String cod_articulo;
    private int Stock;

    public Dashboard_stock() {
    }

    public String getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(String cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int Stock) {
        this.Stock = Stock;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
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
    
    

}
