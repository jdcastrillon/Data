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
public class Inventario implements Serializable {

    private int nro_inventario;
    private String cod_emp;
    private String fec_doc;
    private String observacion;
    private int cod_log;

    transient Date fecha;
    transient List<Log_Transaccion> logs = new ArrayList();
    transient int nro_conteo;

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
    

    public int getNro_conteo() {
        return nro_conteo;
    }

    public void setNro_conteo(int nro_conteo) {
        this.nro_conteo = nro_conteo;
    }

}
