/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Comercial;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Cajas implements Serializable {

    private int cod_caja;
    private String cod_emp;
    private String cod_sucursal;
    private String usuario;
    private String fec_doc;
    private double imp_base;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient Date B_fec_doc;

    public Cajas() {
    }

    public int getCod_caja() {
        return cod_caja;
    }

    public void setCod_caja(int cod_caja) {
        this.cod_caja = cod_caja;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getCod_sucursal() {
        return cod_sucursal;
    }

    public void setCod_sucursal(String cod_sucursal) {
        this.cod_sucursal = cod_sucursal;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public double getImp_base() {
        return imp_base;
    }

    public void setImp_base(double imp_base) {
        this.imp_base = imp_base;
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

    public Date getB_fec_doc() {
        return B_fec_doc;
    }

    public void setB_fec_doc(Date B_fec_doc) {
        this.B_fec_doc = B_fec_doc;
    }
    
    

}
