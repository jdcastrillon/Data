/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import Modelo.Sistema.Log_Transaccion;
import Modelo.SubCategoria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Categoria implements Serializable {

    private String cod_categoria;
    private String nom_categoria;
    private String modulo;
    private int cod_log;

    transient List<Log_Transaccion> logs = new ArrayList();
    transient List<SubCategoria> listSubCategoria=new ArrayList();

    public Categoria() {
    }

    public String getCod_categoria() {
        return cod_categoria;
    }

    public void setCod_categoria(String cod_categoria) {
        this.cod_categoria = cod_categoria;
    }

    public String getNom_categoria() {
        return nom_categoria;
    }

    public void setNom_categoria(String nom_categoria) {
        this.nom_categoria = nom_categoria;
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

    public List<SubCategoria> getListSubCategoria() {
        return listSubCategoria;
    }

    public void setListSubCategoria(List<SubCategoria> listSubCategoria) {
        this.listSubCategoria = listSubCategoria;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
    

}
