/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

/**
 *
 * @author LENOVO
 */
public class m_unidaStock {

    private String cod_unidad;
    private String nom_unidad;

    public m_unidaStock() {
    }

    public m_unidaStock(String cod_unidad, String nom_unidad) {
        this.cod_unidad = cod_unidad;
        this.nom_unidad = nom_unidad;
    }

    public String getCod_unidad() {
        return cod_unidad;
    }

    public void setCod_unidad(String cod_unidad) {
        this.cod_unidad = cod_unidad;
    }

    public String getNom_unidad() {
        return nom_unidad;
    }

    public void setNom_unidad(String nom_unidad) {
        this.nom_unidad = nom_unidad;
    }

}
