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
public class Empresa implements Serializable {

    private String cod_emp;
    private String nom_emp;
    private String razon_social;
    private String cod_doc;
    private String nit;
    private String direccion;
    private int cod_ciudad;
    private String telefono;
    private String correo;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();

    public Empresa() {
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getNom_emp() {
        return nom_emp;
    }

    public void setNom_emp(String nom_emp) {
        this.nom_emp = nom_emp;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getCod_doc() {
        return cod_doc;
    }

    public void setCod_doc(String cod_doc) {
        this.cod_doc = cod_doc;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String Direccion) {
        this.direccion = Direccion;
    }

    public int getCod_ciudad() {
        return cod_ciudad;
    }

    public void setCod_ciudad(int cod_ciudad) {
        this.cod_ciudad = cod_ciudad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    @Override
    public String toString() {
        return "Empresa{" + "cod_emp=" + cod_emp + ", nom_emp=" + nom_emp + ", razon_social=" + razon_social + ", cod_doc=" + cod_doc + ", nit=" + nit + ", direccion=" + direccion + ", cod_ciudad=" + cod_ciudad + ", telefono=" + telefono + ", correo=" + correo + ", cod_log=" + cod_log + ", logs=" + logs + '}';
    }
    

}
