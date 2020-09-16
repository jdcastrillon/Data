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
public class Estados implements Serializable {

    private String cod_estado;
    private String nom_estado;
    private String cod_categoria;
    private String activo;
    private String ubicacion; //Maneja ubicaciones
    private String Uni_negativas; //Permite Unidades Negativas 
    private String cod_deposito;
    private String cod_propietario;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient private boolean activoE;
    transient private boolean ubicacionE;
    transient private boolean Negativos;

    public Estados() {
    }

    public String getCod_estado() {
        return cod_estado;
    }

    public void setCod_estado(String cod_estado) {
        this.cod_estado = cod_estado;
    }

    public String getNom_estado() {
        return nom_estado;
    }

    public void setNom_estado(String nom_estado) {
        this.nom_estado = nom_estado;
    }

    public String getCod_categoria() {
        return cod_categoria;
    }

    public void setCod_categoria(String cod_categoria) {
        this.cod_categoria = cod_categoria;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUni_negativas() {
        return Uni_negativas;
    }

    public void setUni_negativas(String Uni_negativas) {
        this.Uni_negativas = Uni_negativas;
    }

    public String getCod_deposito() {
        return cod_deposito;
    }

    public void setCod_deposito(String cod_deposito) {
        this.cod_deposito = cod_deposito;
    }

    public String getCod_propietario() {
        return cod_propietario;
    }

    public void setCod_propietario(String cod_propietario) {
        this.cod_propietario = cod_propietario;
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

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public boolean isActivoE() {
        return activoE;
    }

    public void setActivoE(boolean activoE) {
        this.activoE = activoE;
    }

    public boolean isUbicacionE() {
        return ubicacionE;
    }

    public void setUbicacionE(boolean ubicacionE) {
        this.ubicacionE = ubicacionE;
    }

    public boolean isNegativos() {
        return Negativos;
    }

    public void setNegativos(boolean Negativos) {
        this.Negativos = Negativos;
    }

    @Override
    public String toString() {
        return "Estados{" + "cod_estado=" + cod_estado + ", nom_estado=" + nom_estado + ", cod_categoria=" + cod_categoria + ", activo=" + activo + ", ubicacion=" + ubicacion + ", Uni_negativas=" + Uni_negativas + ", cod_deposito=" + cod_deposito + ", cod_propietario=" + cod_propietario + ", cod_log=" + cod_log + ", logs=" + logs + ", activoE=" + activoE + ", ubicacionE=" + ubicacionE + ", Negativos=" + Negativos + '}';
    }

}
