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
public class Ubicaciones implements Serializable {

    private String cod_ubicacion;
    private String nom_ubicacion;
    private String cod_emp;
    private String cod_deposito;
    private String pasillo;
    private String columna;
    private String nivel;
    private String activo;
    private int cod_log;

    transient private boolean activoUbi;
    transient List<Log_Transaccion> logs = new ArrayList();

    public Ubicaciones() {
    }

    public String getCod_ubicacion() {
        return cod_ubicacion;
    }

    public void setCod_ubicacion(String cod_ubicacion) {
        this.cod_ubicacion = cod_ubicacion;
    }

    public String getNom_ubicacion() {
        return nom_ubicacion;
    }

    public void setNom_ubicacion(String nom_ubicacion) {
        this.nom_ubicacion = nom_ubicacion;
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

    public String getPasillo() {
        return pasillo==null?"":pasillo;
    }

    public void setPasillo(String pasillo) {
        this.pasillo = pasillo;
    }

    public String getColumna() {
        return columna==null?"":columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getNivel() {
        return nivel==null?"":nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
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

    public boolean isActivoUbi() {
        return activoUbi;
    }

    public void setActivoUbi(boolean activoUbi) {
        this.activoUbi = activoUbi;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }
    
    

}
