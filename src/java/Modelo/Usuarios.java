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
public class Usuarios implements Serializable {

    private String usuario;
    private transient String nombre_completo;
    private transient String cedula;
    private transient boolean seleccion;
    private BigDecimal cod_persona;
    private String passowrd;
    private String activo;
    private BigDecimal cod_log;

    private transient int base;

    public Usuarios() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
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

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public boolean isSeleccion() {
        return seleccion;
    }

    public void setSeleccion(boolean seleccion) {
        this.seleccion = seleccion;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return "Usuarios{" + "usuario=" + usuario + ", cod_persona=" + cod_persona + ", passowrd=" + passowrd + ", activo=" + activo + ", cod_log=" + cod_log + '}';
    }

}
