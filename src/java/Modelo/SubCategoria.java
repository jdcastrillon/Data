/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author LENOVO
 */
public class SubCategoria {

    private String cod_categoria;
    private String cod_subcategoria;
    private String nom_subcategoria;

    public SubCategoria() {
    }

    public String getCod_categoria() {
        return cod_categoria;
    }

    public void setCod_categoria(String cod_categoria) {
        this.cod_categoria = cod_categoria;
    }

    public String getCod_subcategoria() {
        return cod_subcategoria;
    }

    public void setCod_subcategoria(String cod_subcategoria) {
        this.cod_subcategoria = cod_subcategoria;
    }

    public String getNom_subcategoria() {
        return nom_subcategoria;
    }

    public void setNom_subcategoria(String nom_subcategoria) {
        this.nom_subcategoria = nom_subcategoria;
    }


}
