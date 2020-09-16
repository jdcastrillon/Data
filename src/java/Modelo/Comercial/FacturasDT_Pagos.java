/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Comercial;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class FacturasDT_Pagos implements Serializable {

    private int trans;
    private String tipoPago;
    private double importe;

    public FacturasDT_Pagos() {
    }

    public FacturasDT_Pagos(String tipoPago, double importe) {
        this.tipoPago = tipoPago;
        this.importe = importe;
    }

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

}
