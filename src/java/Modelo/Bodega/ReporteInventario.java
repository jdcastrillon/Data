/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO Clase Para reportes
 */
public class ReporteInventario implements Serializable {

    private String cod_emp;
    private int nro_inventario;
    private int nro_proceso;
    private String deposito;
    private String cod_articulo;
    private String nom_articulo;
    private String estado;
    private String fec_doc;
    private int cantidad;
    private int conteo;
    private int ajuste;

    private String cod_categoria;
    private String cod_subcategoria;

    private List<Articulos> listArticulos = new ArrayList();

    public ReporteInventario() {
    }

    public String getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(String cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public String getNom_articulo() {
        return nom_articulo;
    }

    public void setNom_articulo(String nom_articulo) {
        this.nom_articulo = nom_articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
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

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
    }

    public int getNro_inventario() {
        return nro_inventario;
    }

    public void setNro_inventario(int nro_inventario) {
        this.nro_inventario = nro_inventario;
    }

    public int getNro_proceso() {
        return nro_proceso;
    }

    public void setNro_proceso(int nro_proceso) {
        this.nro_proceso = nro_proceso;
    }

    public int getConteo() {
        return conteo;
    }

    public void setConteo(int conteo) {
        this.conteo = conteo;
    }

    public int getAjuste() {
        return ajuste;
    }

    public void setAjuste(int ajuste) {
        this.ajuste = ajuste;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFec_doc() {
        return fec_doc;
    }

    public void setFec_doc(String fec_doc) {
        this.fec_doc = fec_doc;
    }

}
