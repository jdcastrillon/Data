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
public class AjusteStock implements Serializable {

    private int trans;
    private String cod_emp;
    private String fec_doc;
    private String cod_docum;
    private int nro_docum;
    private String cod_estado;
    private String cod_deposito;
    private String cod_propietario;
    private int cod_motivo;
    private String observacion;
    private int signo;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<AjusteStockDT> DetalleArt = new ArrayList();
    transient private Date fecha;

    public AjusteStock() {
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

    public String getCod_docum() {
        return cod_docum;
    }

    public void setCod_docum(String cod_docum) {
        this.cod_docum = cod_docum;
    }

    public int getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(int nro_docum) {
        this.nro_docum = nro_docum;
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getCod_propietario() {
        return cod_propietario;
    }

    public void setCod_propietario(String cod_propietario) {
        this.cod_propietario = cod_propietario;
    }

    public int getCod_motivo() {
        return cod_motivo;
    }

    public void setCod_motivo(int cod_motivo) {
        this.cod_motivo = cod_motivo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getSigno() {
        return signo;
    }

    public void setSigno(int signo) {
        this.signo = signo;
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

    public List<AjusteStockDT> getDetalleArt() {
        return DetalleArt;
    }

    public void setDetalleArt(List<AjusteStockDT> DetalleArt) {
        this.DetalleArt = DetalleArt;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "AjusteStock{" + "trans=" + trans + ", cod_emp=" + cod_emp + ", fec_doc=" + fec_doc + ", cod_docum=" + cod_docum + ", nro_docum=" + nro_docum + ", cod_estado=" + cod_estado + ", cod_deposito=" + cod_deposito + ", cod_propietario=" + cod_propietario + ", cod_motivo=" + cod_motivo + ", observacion=" + observacion + ", signo=" + signo + ", cod_log=" + cod_log + ", logs=" + logs + ", DetalleArt=" + DetalleArt + ", fecha=" + fecha + '}';
    }

}
