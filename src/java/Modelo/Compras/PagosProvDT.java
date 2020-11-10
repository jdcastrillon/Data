/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Compras;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class PagosProvDT implements Serializable {

    private int trans;
    private String factura;
    private double imp_saldo;
    private double imp_pago;
    private int signo;
    private int linea;

    transient private String fec_doc;
    transient private boolean pagar;

    public PagosProvDT() {
    }

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public double getImp_saldo() {
        return imp_saldo;
    }

    public void setImp_saldo(double imp_saldo) {
        this.imp_saldo = imp_saldo;
    }

    public double getImp_pago() {
        return imp_pago;
    }

    public void setImp_pago(double imp_pago) {
        this.imp_pago = imp_pago;
    }

    public int getSigno() {
        return signo;
    }

    public void setSigno(int signo) {
        this.signo = signo;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public boolean isPagar() {
        return pagar;
    }

    public void setPagar(boolean pagar) {
        this.pagar = pagar;
    }

    @Override
    public String toString() {
        return "PagosProvDT{" + "trans=" + trans + ", factura=" + factura + ", imp_saldo=" + imp_saldo + ", imp_pago=" + imp_pago + ", signo=" + signo + ", linea=" + linea + ", fec_doc=" + fec_doc + ", pagar=" + pagar + '}';
    }

}
