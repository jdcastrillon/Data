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

public class Pagos implements Serializable {

    private int cod_pago;
    private int dias;
    private String descripcion;
    private String activo;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient private boolean B_activo;

    public Pagos() {
    }

    public Pagos(int cod_pago, String descripcion) {
        this.cod_pago = cod_pago;
        this.descripcion = descripcion;
    }

    public int getCod_pago() {
        return cod_pago;
    }

    public void setCod_pago(int cod_pago) {
        this.cod_pago = cod_pago;
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

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

}
