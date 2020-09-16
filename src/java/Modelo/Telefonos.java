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
public class Telefonos implements Serializable {

    private String tipo;
    private String numero;
    private String tabla;
    private BigDecimal cod_persona;
    private BigDecimal cod_log;

    public Telefonos() {
    }

    public Telefonos(String tipo, String numero) {
        this.tipo = tipo;
        this.numero = numero;
    }

    public Telefonos(String tipo, String numero, String tabla, BigDecimal cod_persona, BigDecimal cod_log) {
        this.tipo = tipo;
        this.numero = numero;
        this.tabla = tabla;
        this.cod_persona = cod_persona;
        this.cod_log = cod_log;
    }
    
    

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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
        return "Telefonos{" + "tipo=" + tipo + ", numero=" + numero + '}';
    }

}
