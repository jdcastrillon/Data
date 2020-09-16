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
public class RecepcionTraslado implements Serializable {

    private int trans;
    private String cod_emp;
    private String fec_doc;
    private int nro_docum;
    private int nro_doca;
    private String cod_deposito;
    private String cod_estado;
    private String cod_deposito2;
    private String cod_estado2;
    private String observacion;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<TrasladoBodegaDT> DetalleArt = new ArrayList();
    transient private Date fecha;

    transient String nom_deposito;
    transient String nom_deposito2;
    transient String nom_estado;
    transient String nom_estado2;

    public RecepcionTraslado() {
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

    public int getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(int nro_docum) {
        this.nro_docum = nro_docum;
    }

    public int getNro_doca() {
        return nro_doca;
    }

    public void setNro_doca(int nro_doca) {
        this.nro_doca = nro_doca;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getCod_deposito2() {
        return cod_deposito2;
    }

    public void setCod_deposito2(String cod_deposito2) {
        this.cod_deposito2 = cod_deposito2;
    }

    public String getCod_estado2() {
        return cod_estado2;
    }

    public void setCod_estado2(String cod_estado2) {
        this.cod_estado2 = cod_estado2;
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

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public List<TrasladoBodegaDT> getDetalleArt() {
        return DetalleArt;
    }

    public void setDetalleArt(List<TrasladoBodegaDT> DetalleArt) {
        this.DetalleArt = DetalleArt;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNom_deposito() {
        return nom_deposito;
    }

    public void setNom_deposito(String nom_deposito) {
        this.nom_deposito = nom_deposito;
    }

    public String getNom_deposito2() {
        return nom_deposito2;
    }

    public void setNom_deposito2(String nom_deposito2) {
        this.nom_deposito2 = nom_deposito2;
    }

    public String getNom_estado() {
        return nom_estado;
    }

    public void setNom_estado(String nom_estado) {
        this.nom_estado = nom_estado;
    }

    public String getNom_estado2() {
        return nom_estado2;
    }

    public void setNom_estado2(String nom_estado2) {
        this.nom_estado2 = nom_estado2;
    }

}
