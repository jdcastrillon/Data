/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Comercial;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Clientes implements Serializable {

    private int cod_cliente;
    private String cod_tipodoc;
    private String cod_documento;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private int cod_ciudad;
    private String direccion;
    private int barrio;
    private String activo;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient boolean activoB;
    transient String doc_nombre;

    public Clientes() {
    }

    public int getCod_cliente() {
        return cod_cliente;
    }

    public void setCod_cliente(int cod_cliente) {
        this.cod_cliente = cod_cliente;
    }

    public String getCod_tipodoc() {
        return cod_tipodoc;
    }

    public void setCod_tipodoc(String cod_tipodoc) {
        this.cod_tipodoc = cod_tipodoc;
    }

    public String getCod_documento() {
        return cod_documento;
    }

    public void setCod_documento(String cod_documento) {
        this.cod_documento = cod_documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCod_ciudad() {
        return cod_ciudad;
    }

    public void setCod_ciudad(int cod_ciudad) {
        this.cod_ciudad = cod_ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getBarrio() {
        return barrio;
    }

    public void setBarrio(int barrio) {
        this.barrio = barrio;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
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

    public boolean isActivoB() {
        return activoB;
    }

    public void setActivoB(boolean activoB) {
        this.activoB = activoB;
    }

    public String getDoc_nombre() {
        return doc_nombre;
    }

    public void setDoc_nombre(String doc_nombre) {
        this.doc_nombre = doc_nombre;
    }



}
