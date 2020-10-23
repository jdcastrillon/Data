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
public class Compras implements Serializable {

    private int trans;
    private String cod_emp;
    private int cod_provedor;
    private String cod_docum;
    private int nro_docum;
    private int cod_fpago;
    private String fec_doc;
    private String fec_entrega;
    private String observaciones;
    private int cod_log;
    private double imp_neto;
    private double imp_impuesto;
    private double imp_descuento;
    private double imp_total;
    private String cod_deposito;
    private String factura;

    transient private Date D_fec_doc;
    transient private Date D_fec_entrega;
    transient private String razon_social;
    transient private int porcentaje;
    transient List<ComprasDT> comprasDt = new ArrayList();
    transient List<Log_Transaccion> logs = new ArrayList();

    public Compras() {
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

    public int getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(int nro_docum) {
        this.nro_docum = nro_docum;
    }

    public int getCod_fpago() {
        return cod_fpago;
    }

    public void setCod_fpago(int cod_fpago) {
        this.cod_fpago = cod_fpago;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public String getFec_entrega() {
        return fec_entrega;
    }

    public void setFec_entrega(String fec_entrega) {
        this.fec_entrega = fec_entrega;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public Date getD_fec_entrega() {
        return D_fec_entrega;
    }

    public void setD_fec_entrega(Date D_fec_entrega) {
        this.D_fec_entrega = D_fec_entrega;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public List<ComprasDT> getComprasDt() {
        return comprasDt;
    }

    public void setComprasDt(List<ComprasDT> comprasDt) {
        this.comprasDt = comprasDt;
    }

    public double getImp_neto() {
        return imp_neto;
    }

    public void setImp_neto(double imp_neto) {
        this.imp_neto = imp_neto;
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

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    @Override
    public String toString() {
        return "Compras{" + "trans=" + trans + ", cod_emp=" + cod_emp + ", cod_provedor=" + cod_provedor + ", cod_docum=" + cod_docum + ", nro_docum=" + nro_docum + ", cod_fpago=" + cod_fpago + ", fec_doc=" + fec_doc + ", fec_entrega=" + fec_entrega + ", observaciones=" + observaciones + ", cod_log=" + cod_log + ", D_fec_doc=" + D_fec_doc + ", D_fec_entrega=" + D_fec_entrega + ", logs=" + logs + '}';
    }

}
