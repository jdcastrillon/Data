/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Sucursal implements Serializable {

    private String cod_emp;
    private String cod_sucursal;
    private String nom_sucursal;
    private String direccion;
    private String telefono;
    private int cod_ciudad;
    private String estado;
    transient boolean estadoB;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient private List<Usuarios> listUser = new ArrayList();

    public Sucursal() {
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getCod_sucursal() {
        return cod_sucursal;
    }

    public void setCod_sucursal(String cod_sucursal) {
        this.cod_sucursal = cod_sucursal;
    }

    public String getNom_sucursal() {
        return nom_sucursal;
    }

    public void setNom_sucursal(String nom_sucursal) {
        this.nom_sucursal = nom_sucursal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getCod_ciudad() {
        return cod_ciudad;
    }

    public void setCod_ciudad(int cod_ciudad) {
        this.cod_ciudad = cod_ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public boolean isEstadoB() {
        return estadoB;
    }

    public void setEstadoB(boolean estadoB) {
        this.estadoB = estadoB;
    }

    public List<Usuarios> getListUser() {
        return listUser;
    }

    public void setListUser(List<Usuarios> listUser) {
        this.listUser = listUser;
    }
    
    

    @Override
    public String toString() {
        return "Sucursal{" + "cod_emp=" + cod_emp + ", cod_sucursal=" + cod_sucursal + ", nom_sucursal=" + nom_sucursal + ", direccion=" + direccion + ", telefono=" + telefono + ", cod_ciudad=" + cod_ciudad + ", estado=" + estado + ", estadoB=" + estadoB + ", cod_log=" + cod_log + ", logs=" + logs + '}';
    }

}
