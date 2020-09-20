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
public class ProcesoDatos implements Serializable {

    private int nro_inventario;
    private int nro_conteo;
    private int nro_proceso;
    private String descripcion;
    private int cod_log;
    private String fec_doc;

    private transient Date fecha;
    private transient List<Log_Transaccion> logs = new ArrayList();
    private transient List<ProcesoDatosDT> DetalleProDatos = new ArrayList();
    private transient String cod_emp;

    public ProcesoDatos() {
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

    public int getNro_proceso() {
        return nro_proceso;
    }

    public void setNro_proceso(int nro_proceso) {
        this.nro_proceso = nro_proceso;
    }

    public int getCod_log() {
        return cod_log;
    }

    public void setCod_log(int cod_log) {
        this.cod_log = cod_log;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

    public List<ProcesoDatosDT> getDetalleProDatos() {
        return DetalleProDatos;
    }

    public void setDetalleProDatos(List<ProcesoDatosDT> DetalleProDatos) {
        this.DetalleProDatos = DetalleProDatos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNro_inventario() {
        return nro_inventario;
    }

    public void setNro_inventario(int nro_inventario) {
        this.nro_inventario = nro_inventario;
    }

    public int getNro_conteo() {
        return nro_conteo;
    }

    public void setNro_conteo(int nro_conteo) {
        this.nro_conteo = nro_conteo;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

}
