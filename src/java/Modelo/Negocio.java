/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Negocio implements Serializable {

    private String cod_negocio;
    private String nom_negocio;
    private String cod_emp;
    private String estado;
    transient boolean estadoB;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();

    public Negocio() {
    }

    public String getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(String cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    public String getNom_negocio() {
        return nom_negocio;
    }

    public void setNom_negocio(String nom_negocio) {
        this.nom_negocio = nom_negocio;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isEstadoB() {
        return estadoB;
    }

    public void setEstadoB(boolean estadoB) {
        this.estadoB = estadoB;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public int getCod_log() {
        return cod_log;
    }

    public void setCod_log(int cod_log) {
        this.cod_log = cod_log;
    }

    @Override
    public String toString() {
        return "Negocio{" + "cod_negocio=" + cod_negocio + ", nom_negocio=" + nom_negocio + ", cod_emp=" + cod_emp + ", estado=" + estado + '}';
    }

}
