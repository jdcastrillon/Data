/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Compras;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class PagosProv implements Serializable {

    private int trans;
    private String cod_emp;
    private int cod_provedor;
    private String cod_docum;
    private String fec_doc;
    private String observacion;
    private double importe;
    private int signo;
    private int cod_log;

    transient private Date D_fec_doc;
    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<PagosProvDT> Facturas = new ArrayList();

    public PagosProv() {
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

    public int getCod_provedor() {
        return cod_provedor;
    }

    public void setCod_provedor(int cod_provedor) {
        this.cod_provedor = cod_provedor;
    }

    public String getCod_docum() {
        return cod_docum;
    }

    public void setCod_docum(String cod_docum) {
        this.cod_docum = cod_docum;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
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

    public Date getD_fec_doc() {
        return D_fec_doc;
    }

    public void setD_fec_doc(Date D_fec_doc) {
        this.D_fec_doc = D_fec_doc;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public List<PagosProvDT> getFacturas() {
        return Facturas;
    }

    public void setFacturas(List<PagosProvDT> Facturas) {
        this.Facturas = Facturas;
    }

}
