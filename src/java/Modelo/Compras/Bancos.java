/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Compras;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Bancos implements Serializable {

    private int cod_banco;
    private String descripcion;
    private String activo;
    private int cod_pais;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient private boolean B_activo;

    public Bancos() {
    }

    public Bancos(int cod_banco, String descripcion) {
        this.cod_banco = cod_banco;
        this.descripcion = descripcion;
    }

    public int getCod_banco() {
        return cod_banco;
    }

    public void setCod_banco(int cod_banco) {
        this.cod_banco = cod_banco;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public int getCod_pais() {
        return cod_pais;
    }

    public void setCod_pais(int cod_pais) {
        this.cod_pais = cod_pais;
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

    public boolean isB_activo() {
        return B_activo;
    }

    public void setB_activo(boolean B_activo) {
        this.B_activo = B_activo;
    }

    @Override
    public String toString() {
        return "Bancos{" + "cod_banco=" + cod_banco + ", descripcion=" + descripcion + ", activo=" + activo + ", cod_pais=" + cod_pais + ", cod_log=" + cod_log + ", logs=" + logs + ", B_activo=" + B_activo + '}';
    }

}
