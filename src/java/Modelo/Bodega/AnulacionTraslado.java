/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class AnulacionTraslado implements Serializable {

    private int trans;
    private String cod_emp;
    private String fec_doc;
    private int nro_docum;
    private int nro_doca;
    private String observacion;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient private Date fecha;

    public AnulacionTraslado() {
    }

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public int getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(int nro_docum) {
        this.nro_docum = nro_docum;
    }

    public int getNro_doca() {
        return nro_doca;
    }

    public void setNro_doca(int nro_doca) {
        this.nro_doca = nro_doca;
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

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    

}
