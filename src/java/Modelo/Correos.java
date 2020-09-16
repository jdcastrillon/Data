/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author LENOVO
 */
public class Correos implements Serializable {

    private String tipo;
    private String correo;
    private String tabla;
    private BigDecimal cod_persona;
    private BigDecimal cod_log;

    public Correos() {
    }

    public Correos(String tipo, String correo) {
        this.tipo = tipo;
        this.correo = correo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public BigDecimal getCod_persona() {
        return cod_persona;
    }

    public void setCod_persona(BigDecimal cod_persona) {
        this.cod_persona = cod_persona;
    }

    public BigDecimal getCod_log() {
        return cod_log;
    }

    public void setCod_log(BigDecimal cod_log) {
        this.cod_log = cod_log;
    }

    @Override
    public String toString() {
        return "Correos{" + "tipo=" + tipo + ", correo=" + correo + '}';
    }

}
