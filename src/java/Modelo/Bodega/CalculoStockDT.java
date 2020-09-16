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
public class CalculoStockDT implements Serializable {

    private String cod_calculo;
    private String cod_estado;
    private String operacion;
    private int signo;

    transient private String nom_estado;

    public CalculoStockDT() {
    }

    public CalculoStockDT(String cod_estado) {
        this.cod_estado = cod_estado;
    }
    
    

    public String getCod_calculo() {
        return cod_calculo;
    }

    public void setCod_calculo(String cod_calculo) {
        this.cod_calculo = cod_calculo;
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public int getSigno() {
        return signo;
    }

    public void setSigno(int signo) {
        this.signo = signo;
    }

    public String getNom_estado() {
        return nom_estado;
    }

    public void setNom_estado(String nom_estado) {
        this.nom_estado = nom_estado;
    }

}
