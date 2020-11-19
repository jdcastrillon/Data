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
public class Articulos implements Serializable {

    private int cod_articulo;
    private String codigo;
    private String nom_articulo;
    private String cod_unidad;
    private String categoria;
    private String subcategoria;
    private String cod_negocio;
    private String activo_stock;
    private int stock_min;
    private int stock_max;
    private String alerta_Stock;
    private String activo_comercial;
    private String activo_compras;
    private double porc_imp;
    private int cod_log;

    transient private boolean autogenerar;
    transient private boolean activoStock;
    transient private boolean aletarStock;
    transient private boolean activoComercial;
    transient private String nom_negocio;
    transient private String nom_categoria;
    transient private String nom_unidad;
    transient private String nom_subcategoria;
    transient private String codigo2;
    transient private String nom_articulo2;

    transient List<Log_Transaccion> logs = new ArrayList();

    public Articulos() {
    }

    public Articulos(int cod_articulo, String codigo, String nom_articulo) {
        this.cod_articulo = cod_articulo;
        this.codigo = codigo;
        this.nom_articulo = nom_articulo;
    }

    public int getCod_articulo() {
        return cod_articulo;
    }

    public void setCod_articulo(int cod_articulo) {
        this.cod_articulo = cod_articulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNom_articulo() {
        return nom_articulo;
    }

    public void setNom_articulo(String nom_articulo) {
        this.nom_articulo = nom_articulo;
    }

    public String getCod_unidad() {
        return cod_unidad;
    }

    public void setCod_unidad(String cod_unidad) {
        this.cod_unidad = cod_unidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCod_negocio() {
        return cod_negocio;
    }

    public void setCod_negocio(String cod_negocio) {
        this.cod_negocio = cod_negocio;
    }

    public String getActivo_stock() {
        return activo_stock;
    }

    public void setActivo_stock(String activo_stock) {
        this.activo_stock = activo_stock;
    }

    public int getStock_min() {
        return stock_min;
    }

    public void setStock_min(int stock_min) {
        this.stock_min = stock_min;
    }

    public int getStock_max() {
        return stock_max;
    }

    public void setStock_max(int stock_max) {
        this.stock_max = stock_max;
    }

    public String getAlerta_Stock() {
        return alerta_Stock;
    }

    public void setAlerta_Stock(String alerta_Stock) {
        this.alerta_Stock = alerta_Stock;
    }

    public String getActivo_comercial() {
        return activo_comercial;
    }

    public void setActivo_comercial(String activo_comercial) {
        this.activo_comercial = activo_comercial;
    }

    public String getActivo_compras() {
        return activo_compras;
    }

    public void setActivo_compras(String activo_compras) {
        this.activo_compras = activo_compras;
    }

    public int getCod_log() {
        return cod_log;
    }

    public void setCod_log(int cod_log) {
        this.cod_log = cod_log;
    }

    public boolean isAutogenerar() {
        return autogenerar;
    }

    public void setAutogenerar(boolean autogenerar) {
        this.autogenerar = autogenerar;
    }

    public boolean isActivoStock() {
        return activoStock;
    }

    public void setActivoStock(boolean activoStock) {
        this.activoStock = activoStock;
    }

    public boolean isAletarStock() {
        return aletarStock;
    }

    public void setAletarStock(boolean aletarStock) {
        this.aletarStock = aletarStock;
    }

    public boolean isActivoComercial() {
        return activoComercial;
    }

    public void setActivoComercial(boolean activoComercial) {
        this.activoComercial = activoComercial;
    }

    public String getNom_negocio() {
        return nom_negocio;
    }

    public void setNom_negocio(String nom_negocio) {
        this.nom_negocio = nom_negocio;
    }

    public String getNom_categoria() {
        return nom_categoria;
    }

    public void setNom_categoria(String nom_categoria) {
        this.nom_categoria = nom_categoria;
    }

    public String getNom_unidad() {
        return nom_unidad;
    }

    public void setNom_unidad(String nom_unidad) {
        this.nom_unidad = nom_unidad;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }

    public double getPorc_imp() {
        return porc_imp;
    }

    public void setPorc_imp(double porc_imp) {
        this.porc_imp = porc_imp;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getNom_subcategoria() {
        return nom_subcategoria;
    }

    public void setNom_subcategoria(String nom_subcategoria) {
        this.nom_subcategoria = nom_subcategoria;
    }

    public String getCodigo2() {
        return codigo2;
    }

    public void setCodigo2(String codigo2) {
        this.codigo2 = codigo2;
    }

    public String getNom_articulo2() {
        return nom_articulo2;
    }

    public void setNom_articulo2(String nom_articulo2) {
        this.nom_articulo2 = nom_articulo2;
    }

    @Override
    public String toString() {
        return "Articulos{" + "cod_articulo=" + cod_articulo + ", codigo=" + codigo + ", nom_articulo=" + nom_articulo + ", cod_unidad=" + cod_unidad + ", categoria=" + categoria + ", subcategoria=" + subcategoria + ", cod_negocio=" + cod_negocio + ", activo_stock=" + activo_stock + ", stock_min=" + stock_min + ", stock_max=" + stock_max + ", alerta_Stock=" + alerta_Stock + ", activo_comercial=" + activo_comercial + ", activo_compras=" + activo_compras + ", porc_imp=" + porc_imp + ", cod_log=" + cod_log + ", autogenerar=" + autogenerar + ", activoStock=" + activoStock + ", aletarStock=" + aletarStock + ", activoComercial=" + activoComercial + ", nom_negocio=" + nom_negocio + ", nom_categoria=" + nom_categoria + ", nom_unidad=" + nom_unidad + ", nom_subcategoria=" + nom_subcategoria + ", logs=" + logs + '}';
    }

}
