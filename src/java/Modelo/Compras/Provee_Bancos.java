/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Compras;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class Provee_Bancos implements Serializable {

    private int cod_proveedor;
    private int cod_banco;
    private String cod_cta;
    private String tipo_cta;

    private transient String nom_banco;

    public Provee_Bancos() {
    }

    public int getCod_proveedor() {
        return cod_proveedor;
    }

    public void setCod_proveedor(int cod_proveedor) {
        this.cod_proveedor = cod_proveedor;
    }

    public int getCod_banco() {
        return cod_banco;
    }

    public void setCod_banco(int cod_banco) {
        this.cod_banco = cod_banco;
    }

    public String getCod_cta() {
        return cod_cta;
    }

    public void setCod_cta(String cod_cta) {
        this.cod_cta = cod_cta;
    }

    public String getTipo_cta() {
        return tipo_cta;
    }

    public void setTipo_cta(String tipo_cta) {
        this.tipo_cta = tipo_cta;
    }

    public String getNom_banco() {
        return nom_banco;
    }

    public void setNom_banco(String nom_banco) {
        this.nom_banco = nom_banco;
    }


}
