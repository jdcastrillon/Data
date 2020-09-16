/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Compras;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Proveedores implements Serializable {

    private int cod_provedor;
    private String cod_tipodoc;
    private String cod_documento;
    private String razon_social;
    private String telefono;
    private String email;
    private int cod_ciudad;
    private String direccion;
    private String activo;
    private int cod_log;
    private int cod_fpago;
    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<Provee_Bancos> list_Bancos = new ArrayList();
    transient List<Prove_Vendedor> list_vend = new ArrayList();
    transient boolean activoB;

    public Proveedores() {
    }

    public int getCod_provedor() {
        return cod_provedor;
    }

    public void setCod_provedor(int cod_provedor) {
        this.cod_provedor = cod_provedor;
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

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
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

    public List<Provee_Bancos> getList_Bancos() {
        return list_Bancos;
    }

    public void setList_Bancos(List<Provee_Bancos> list_Bancos) {
        this.list_Bancos = list_Bancos;
    }

    public int getCod_fpago() {
        return cod_fpago;
    }

    public void setCod_fpago(int cod_fpago) {
        this.cod_fpago = cod_fpago;
    }

    public List<Prove_Vendedor> getList_vend() {
        return list_vend;
    }

    public void setList_vend(List<Prove_Vendedor> list_vend) {
        this.list_vend = list_vend;
    }

    @Override
    public String toString() {
        return "Proveedores{" + "cod_provedor=" + cod_provedor + ", cod_tipodoc=" + cod_tipodoc + ", cod_documento=" + cod_documento + ", razon_social=" + razon_social + ", telefono=" + telefono + ", email=" + email + ", cod_ciudad=" + cod_ciudad + ", direccion=" + direccion + ", activo=" + activo + ", cod_log=" + cod_log + ", cod_fpago=" + cod_fpago + ", logs=" + logs + ", list_Bancos=" + list_Bancos + ", list_vend=" + list_vend + ", activoB=" + activoB + '}';
    }

}
