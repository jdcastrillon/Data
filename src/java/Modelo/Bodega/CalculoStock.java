/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class CalculoStock implements Serializable {

    private String cod_calculo;
    private String nom_calculo;
    private String activo;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<CalculoStockDT> List_estados = new ArrayList();
    transient private boolean activoC;

    public CalculoStock() {
    }

    public String getCod_calculo() {
        return cod_calculo;
    }

    public void setCod_calculo(String cod_calculo) {
        this.cod_calculo = cod_calculo;
    }

    public String getNom_calculo() {
        return nom_calculo;
    }

    public void setNom_calculo(String nom_calculo) {
        this.nom_calculo = nom_calculo;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public int getCod_log() {
        return cod_log;
    }

    public void setCod_log(int cod_log) {
        this.cod_log = cod_log;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public boolean isActivoC() {
        return activoC;
    }

    public void setActivoC(boolean activoC) {
        this.activoC = activoC;
    }

    public List<CalculoStockDT> getList_estados() {
        return List_estados;
    }

    public void setList_estados(List<CalculoStockDT> List_estados) {
        this.List_estados = List_estados;
    }

}
