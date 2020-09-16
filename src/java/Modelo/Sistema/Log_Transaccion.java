/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Sistema;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class Log_Transaccion implements Serializable {

    private int cod_proceso;
    private int cod_log;
    private String proceso;
    private String usuario;
    private String operacion;
    private String fecha;

    public Log_Transaccion() {
    }

    public Log_Transaccion(int cod_proceso, int cod_log, String proceso, String usuario, String operacion, String fecha) {
        this.cod_proceso = cod_proceso;
        this.cod_log = cod_log;
        this.proceso = proceso;
        this.usuario = usuario;
        this.operacion = operacion;
        this.fecha = fecha;
    }


    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCod_log() {
        return cod_log;
    }

    public void setCod_log(int cod_log) {
        this.cod_log = cod_log;
    }

    public int getCod_proceso() {
        return cod_proceso;
    }

    public void setCod_proceso(int cod_proceso) {
        this.cod_proceso = cod_proceso;
    }

    @Override
    public String toString() {
        return "Log_Transaccion{" + "proceso=" + proceso + ", usuario=" + usuario + ", operacion=" + operacion + ", fecha=" + fecha + '}';
    }

}
