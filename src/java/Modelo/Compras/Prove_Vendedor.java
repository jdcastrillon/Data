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
public class Prove_Vendedor implements Serializable {

    private int cod_proveedor;
    private String cod_tipodoc;
    private String cod_documento;
    private String nombre;
    private String telefono;

    public Prove_Vendedor() {
    }

    public int getCod_proveedor() {
        return cod_proveedor;
    }

    public void setCod_proveedor(int cod_proveedor) {
        this.cod_proveedor = cod_proveedor;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    
}
