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
public class Conteo implements Serializable {

    private int nro_conteo;
    private int nro_inventario;
    private String cod_categoria;
    private String cod_subcategoria;
    private String cod_deposito;
    private int cod_log;
    private String fec_doc;

    transient Date fecha;
    transient List<ProConteoDT> DetalleCont = new ArrayList();
    transient List<Log_Transaccion> logs = new ArrayList();
    private transient String cod_emp;

    public Conteo() {
    }   

    public int getNro_conteo() {
        return nro_conteo;
    }

    public void setNro_conteo(int nro_conteo) {
        this.nro_conteo = nro_conteo;
    }

    public int getNro_inventario() {
        return nro_inventario;
    }

    public void setNro_inventario(int nro_inventario) {
        this.nro_inventario = nro_inventario;
    }

    public String getCod_categoria() {
        return cod_categoria;
    }

    public void setCod_categoria(String cod_categoria) {
        this.cod_categoria = cod_categoria;
    }

    public String getCod_subcategoria() {
        return cod_subcategoria;
    }

    public void setCod_subcategoria(String cod_subcategoria) {
        this.cod_subcategoria = cod_subcategoria;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<ProConteoDT> getDetalleCont() {
        return DetalleCont;
    }

    public void setDetalleCont(List<ProConteoDT> DetalleCont) {
        this.DetalleCont = DetalleCont;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

}
