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
public class Impuestos implements Serializable {
    
    private String cod_emp;
    private String cod_impuesto;
    private int porc_imp;
    private String activo;
    private String observacion;
    private int cod_log;

    public Impuestos() {
    }

    public String getCod_impuesto() {
        return cod_impuesto;
    }

    public void setCod_impuesto(String cod_impuesto) {
        this.cod_impuesto = cod_impuesto;
    }

    public int getPorc_imp() {
        return porc_imp;
    }

    public void setPorc_imp(int porc_imp) {
        this.porc_imp = porc_imp;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getCod_log() {
        return cod_log;
    }

    public void setCod_log(int cod_log) {
        this.cod_log = cod_log;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }
    
    

}
