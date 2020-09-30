/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo.Bodega;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LENOVO * Clase para reporte
 */
public class Stock_Traslados {

    private String cod_emp;
    private String deposito;
    private String deposito2;
    private String cod_articulo;
    private String nom_articulo;
    private String estado;
    private String cod_unidad;
    private int cant_enviada;
    private int cant_recibidad;
    private int pendientes;
    private int nro_traslado;
    private String AplicaFechas;

    private String cod_categoria;
    private String cod_subcategoria;

    private Date fec_ini;
    private Date fec_fin;
    private String fec_ini2;
    private String fec_fin2;

    private List<Articulos> listArticulos = new ArrayList();

    public Stock_Traslados() {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Date getFec_ini() {
        return fec_ini;
    }

    public void setFec_ini(Date fec_ini) {
        this.fec_ini = fec_ini;
    }

    public Date getFec_fin() {
        return fec_fin;
    }

    public void setFec_fin(Date fec_fin) {
        this.fec_fin = fec_fin;
    }

    public String getDeposito2() {
        return deposito2;
    }

    public void setDeposito2(String deposito2) {
        this.deposito2 = deposito2;
    }

    public int getCant_enviada() {
        return cant_enviada;
    }

    public void setCant_enviada(int cant_enviada) {
        this.cant_enviada = cant_enviada;
    }

    public int getCant_recibidad() {
        return cant_recibidad;
    }

    public void setCant_recibidad(int cant_recibidad) {
        this.cant_recibidad = cant_recibidad;
    }

    public int getPendientes() {
        return pendientes;
    }

    public void setPendientes(int pendientes) {
        this.pendientes = pendientes;
    }

    public int getNro_traslado() {
        return nro_traslado;
    }

    public void setNro_traslado(int nro_traslado) {
        this.nro_traslado = nro_traslado;
    }

    public String getFec_ini2() {
        return fec_ini2;
    }

    public void setFec_ini2(String fec_ini2) {
        this.fec_ini2 = fec_ini2;
    }

    public String getFec_fin2() {
        return fec_fin2;
    }

    public void setFec_fin2(String fec_fin2) {
        this.fec_fin2 = fec_fin2;
    }

    public String getCod_unidad() {
        return cod_unidad;
    }

    public void setCod_unidad(String cod_unidad) {
        this.cod_unidad = cod_unidad;
    }

    public String getAplicaFechas() {
        return AplicaFechas;
    }

    public void setAplicaFechas(String AplicaFechas) {
        this.AplicaFechas = AplicaFechas;
    }

}
