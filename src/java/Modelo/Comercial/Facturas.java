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
public class Facturas implements Serializable {

    private int trans;
    private int cod_caja;
    private int cod_cliente;
    private String cod_docum;
    private String serie_docum;
    private int nro_docum;
    private String cod_doca;
    private String serie_doca;
    private int nro_doca;
    private String fec_doc;
    private String fec_venc;
    private int cod_fpago;
    private String observacion;
    private String estado;
    private int cod_moneda;
    private double imp_neto;
    private String signo;
    private String cod_impu;
    private double imp_impuesto;
    private double imp_descuento;
    private double imp_total;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<FacturasDT> facturasDT = new ArrayList();

    transient Date B_fec_doc;

    public Facturas() {
    }

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public int getCod_caja() {
        return cod_caja;
    }

    public void setCod_caja(int cod_caja) {
        this.cod_caja = cod_caja;
    }

    public int getCod_cliente() {
        return cod_cliente;
    }

    public void setCod_cliente(int cod_cliente) {
        this.cod_cliente = cod_cliente;
    }

    public String getCod_docum() {
        return cod_docum;
    }

    public void setCod_docum(String cod_docum) {
        this.cod_docum = cod_docum;
    }

    public String getSerie_docum() {
        return serie_docum;
    }

    public void setSerie_docum(String serie_docum) {
        this.serie_docum = serie_docum;
    }

    public int getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(int nro_docum) {
        this.nro_docum = nro_docum;
    }

    public String getCod_doca() {
        return cod_doca;
    }

    public void setCod_doca(String cod_doca) {
        this.cod_doca = cod_doca;
    }

    public String getSerie_doca() {
        return serie_doca;
    }

    public void setSerie_doca(String serie_doca) {
        this.serie_doca = serie_doca;
    }

    public int getNro_doca() {
        return nro_doca;
    }

    public void setNro_doca(int nro_doca) {
        this.nro_doca = nro_doca;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public String getFec_venc() {
        return fec_venc;
    }

    public void setFec_venc(String fec_venc) {
        this.fec_venc = fec_venc;
    }

    public int getCod_fpago() {
        return cod_fpago;
    }

    public void setCod_fpago(int cod_fpago) {
        this.cod_fpago = cod_fpago;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCod_moneda() {
        return cod_moneda;
    }

    public void setCod_moneda(int cod_moneda) {
        this.cod_moneda = cod_moneda;
    }

    public double getImp_neto() {
        return imp_neto;
    }

    public void setImp_neto(double imp_neto) {
        this.imp_neto = imp_neto;
    }

    public String getSigno() {
        return signo;
    }

    public void setSigno(String signo) {
        this.signo = signo;
    }

    public String getCod_impu() {
        return cod_impu;
    }

    public void setCod_impu(String cod_impu) {
        this.cod_impu = cod_impu;
    }

    public double getImp_impuesto() {
        return imp_impuesto;
    }

    public void setImp_impuesto(double imp_impuesto) {
        this.imp_impuesto = imp_impuesto;
    }

    public double getImp_descuento() {
        return imp_descuento;
    }

    public void setImp_descuento(double imp_descuento) {
        this.imp_descuento = imp_descuento;
    }

    public double getImp_total() {
        return imp_total;
    }

    public void setImp_total(double imp_total) {
        this.imp_total = imp_total;
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

    public List<FacturasDT> getFacturasDT() {
        return facturasDT;
    }

    public void setFacturasDT(List<FacturasDT> facturasDT) {
        this.facturasDT = facturasDT;
    }

}
