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
 * @author Mauricio Herrera
 */
public class CierreInventario implements Serializable {

    private int nro_inventario;
    private String cod_emp;
    private String fec_inv;
    private String fec_cierre;
    private String observacion;
    private int cod_log;

    transient Date fecha;
    transient Date fecha2;
    transient List<Log_Transaccion> logs = new ArrayList();

    public int getNro_inventario() {
        return nro_inventario;
    }

    public void setNro_inventario(int nro_inventario) {
        this.nro_inventario = nro_inventario;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public Date getFecha2() {
        return fecha2;
    }

    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }

   

    public String getFec_inv() {
        return fec_inv;
    }

    public void setFec_inv(String fec_inv) {
        this.fec_inv = fec_inv;
    }

    public String getFec_cierre() {
        return fec_cierre;
    }

    public void setFec_cierre(String fec_cierre) {
        this.fec_cierre = fec_cierre;
    }  
    

}
