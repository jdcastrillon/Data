/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Depositos implements Serializable {

    private String cod_deposito;
    private String nom_deposito;
    private String cod_emp;
    private String cod_sucursal;
    private String principal;
    private String activo;
    private int cod_ciudad;
    private String tiene_ubicaciones;
    private int cod_log;

    transient private boolean ActDepo;
    transient private boolean DepositoP;
    transient private boolean TieneUbi;
    transient private String nom_cuidad;

    transient List<Log_Transaccion> logs = new ArrayList();

    public Depositos() {
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getNom_deposito() {
        return nom_deposito;
    }

    public void setNom_deposito(String nom_deposito) {
        this.nom_deposito = nom_deposito;
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

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public int getCod_ciudad() {
        return cod_ciudad;
    }

    public void setCod_ciudad(int cod_ciudad) {
        this.cod_ciudad = cod_ciudad;
    }

    public String getTiene_ubicaciones() {
        return tiene_ubicaciones;
    }

    public void setTiene_ubicaciones(String tiene_ubicaciones) {
        this.tiene_ubicaciones = tiene_ubicaciones;
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

    public boolean isActDepo() {
        return ActDepo;
    }

    public void setActDepo(boolean ActDepo) {
        this.ActDepo = ActDepo;
    }

    public boolean isDepositoP() {
        return DepositoP;
    }

    public void setDepositoP(boolean DepositoP) {
        this.DepositoP = DepositoP;
    }

    public boolean isTieneUbi() {
        return TieneUbi;
    }

    public void setTieneUbi(boolean TieneUbi) {
        this.TieneUbi = TieneUbi;
    }

    public String getNom_cuidad() {
        return nom_cuidad;
    }

    public void setNom_cuidad(String nom_cuidad) {
        this.nom_cuidad = nom_cuidad;
    }

    @Override
    public String toString() {
        return "Depositos{" + "cod_deposito=" + cod_deposito + ", nom_deposito=" + nom_deposito + ", cod_emp=" + cod_emp + ", cod_sucursal=" + cod_sucursal + ", principal=" + principal + ", activo=" + activo + ", cod_ciudad=" + cod_ciudad + ", tiene_ubicaciones=" + tiene_ubicaciones + ", cod_log=" + cod_log + ", ActDepo=" + ActDepo + ", DepositoP=" + DepositoP + ", TieneUbi=" + TieneUbi + ", nom_cuidad=" + nom_cuidad + ", logs=" + logs + '}';
    }
    

}
