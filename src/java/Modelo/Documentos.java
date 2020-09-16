/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Documentos implements Serializable {

    private String cod_tipodoc;
    private String nombredocumento;
    private String activo;
    private String auditoria;
    private BigDecimal cod_log;

    List<Log_Transaccion> logs = new ArrayList();

    public Documentos() {
    }

    public String getCod_tipodoc() {
        return cod_tipodoc;
    }

    public void setCod_tipodoc(String cod_tipodoc) {
        this.cod_tipodoc = cod_tipodoc;
    }

    public String getNombredocumento() {
        return nombredocumento;
    }

    public void setNombredocumento(String nombredocumento) {
        this.nombredocumento = nombredocumento;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(String auditoria) {
        this.auditoria = auditoria;
    }

    public BigDecimal getCod_log() {
        return cod_log;
    }

    public void setCod_log(BigDecimal cod_log) {
        this.cod_log = cod_log;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    @Override
    public String toString() {
        return "Documentos{" + "cod_tipodoc=" + cod_tipodoc + ", nombredocumento=" + nombredocumento + ", activo=" + activo + ", auditoria=" + auditoria + ", cod_log=" + cod_log + '}';
    }

}
