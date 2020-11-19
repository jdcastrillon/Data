package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Compras.*;
import Modelo.Empresa;
import Servicios.ComprasService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class ComprasControlador {

    @Inject
    private ComprasService Compra_service;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Compras objCompra;
    private List<Compras> ListCompra = new ArrayList();
    NumberFormat formatter = NumberFormat.getCurrencyInstance();

    private List<Empresa> listEmpresas = new ArrayList();
    private List<Impuestos> listImpuestos = new ArrayList();
    private List<Proveedores> listProveedor = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Pagos> ListPagos = new ArrayList();

    private Articulos objArticulo;

    Object acciones[] = new Object[6];
    Object totales[] = new Object[8];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private String evento;
    private String valorBusqueda;

    @PostConstruct
    public void init() {
        try {
            getObjCompra();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
            resetTotales();
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ComprasControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        ListCompra.clear();
        ListCompra = Compra_service.Lista("OC");

        if (condicion == 1) {
            listEmpresas.clear();
            listProveedor.clear();

            //Empresas
            JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
            for (JsonElement jsonElement : Jelementos) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Empresa emp = new Empresa();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    emp.setCod_emp(map.get("cod_emp").toString());
                    emp.setNom_emp(map.get("nom_emp").toString());
                    listEmpresas.add(emp);
                }
            }

            //Proveedores
            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_provedor,cod_documento,razon_social from m_proveedores");

            for (JsonElement jsonElement : Jelementos2) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Proveedores obj = new Proveedores();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                    obj.setCod_documento(map.get("cod_documento").toString());
                    obj.setRazon_social(map.get("razon_social").toString());
                    listProveedor.add(obj);
                }
            }

        }
    }

    public void prepareNuevo() {
        setObjCompra(null);
        getObjCompra();
        listEmpresas.clear();
        listArticulos.clear();
        listProveedor.clear();
        listImpuestos.clear();

        //Numerador
        objCompra.setNro_docum(ObjIni.numerador("compra"));

        //Empresas
        JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Empresa emp = new Empresa();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                emp.setCod_emp(map.get("cod_emp").toString());
                emp.setNom_emp(map.get("nom_emp").toString());
                listEmpresas.add(emp);
            }
        }

        //Proveedores
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_provedor,cod_documento,razon_social from m_proveedores");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Proveedores obj = new Proveedores();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                obj.setCod_documento(map.get("cod_documento").toString());
                obj.setRazon_social(map.get("razon_social").toString());
                listProveedor.add(obj);
            }
        }

        //Impuestos
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_impuesto,porc_imp from m_impuestos where cod_impuesto='IVA' and cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");

        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Impuestos obj = new Impuestos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_impuesto(map.get("cod_impuesto").toString());
                obj.setPorc_imp(new BigDecimal(map.get("porc_imp").toString()).intValue());
                listImpuestos.add(obj);
            }
        }

        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        objCompra.setCod_emp(listEmpresas.get(0).getCod_emp());
        objCompra.setPorcentaje(listImpuestos.get(0).getPorc_imp());
//        objCompra.setCod_provedor(listProveedor.get(0).getCod_provedor());
        objCompra.setD_fec_doc(new Date());
        resetTotales();

        this.evento = "Nuevo";
        controlEventos(evento);
    }

    public void prepareEdit() {
        this.evento = "Editar";
        controlEventos(evento);
    }

    public void prepareEliminar() {
        this.evento = "Eliminar";
        controlEventos(evento);
    }

    public void prepareBuscar() {
        ListCompra.clear();
        this.evento = "Buscar";
        setObjCompra(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Compras objecto, int condicion) {
        setObjCompra(null);
        Object Resulta[] = new Object[2];
        Resulta = Compra_service.recuperarInfo(objecto);
        setObjCompra((Compras) Resulta[0]);
        lista(2);
        //Condiciones
        switch (condicion) {
            case 1:
                this.evento = "Editar";
                break;
            case 2:
                this.evento = "Eliminar";
                break;
            case 3:
                this.evento = "Ver";
                break;
            default:
                this.evento = "Ver";
                break;
        }
        controlEventos(evento);
    }

    public void cancelarEventos() {
        if (this.evento.equalsIgnoreCase("Buscar")) {
            lista(2);
        }
        this.evento = "inicio";
        controlEventos(evento);
        setObjCompra(null);
        resetTotales();
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = Compra_service.Transaccion(objCompra, "Nuevo", "OC");
                    mns = "O.C Realizada exitosamente";
                    break;
                case "Eliminar":
                    Resulta = Compra_service.Transaccion(objCompra, "Borrar", "OC");
                    mns = "O.C Eliminada exitosamente";
                    break;
                case "Editar":
                    Resulta = Compra_service.Transaccion(objCompra, "Editar", "OC");
                    mns = "O.C Editado exitosamente";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    ListCompra.clear();
                    ListCompra = (List<Compras>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjCompra(null);
                    this.evento = "inicio";
                    controlEventos(evento);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", (String) Resulta[1]));
            }
        }
    }

    public void controlEventos(String accion) {
        acciones = ObjIni.eventos(accion);
        this.aceptar = (boolean) acciones[0];
        this.editar = (boolean) acciones[1];
        this.eliminar = (boolean) acciones[2];
        this.nuevo = (boolean) acciones[3];
        this.buscar = (boolean) acciones[4];
        this.reporte = (boolean) acciones[5];

    }

    public boolean validaciones() {
        getObjCompra();
        String mns = "";

        if (this.nuevo == false) {
            //Validaciones
            if (objCompra.getCod_provedor() == 0) {
                mns = "Debe Selecionar un Proveedor";
            }
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objCompra.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }


    public void cargarPagos() {
        System.out.println("Cargando cargarDepositos " + objCompra.getCod_provedor());
        ListPagos.clear();
        //Pagos
        JsonArray Jelementos3 = ObjIni.listObjectos("SELECT A.cod_pago, descripcion FROM public.m_pagos A INNER JOIN \n"
                + "m_proveedores B on A.cod_pago=B.cod_fpago\n"
                + "where A.activo='S' and B.cod_provedor=" + objCompra.getCod_provedor());
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                Pagos obj = new Pagos(new BigDecimal(map.get("cod_pago").toString()).intValue(),
                        map.get("descripcion").toString());
                ListPagos.add(obj);
            }
        }
    }

    public List<Articulos> articulos(String query) {
        getObjArticulo();
        System.out.println("------------ Bean articulos------------");
        System.out.println("Articulo : " + objArticulo.toString());

        listArticulos = SelService.cargaArticulo(query, 2);
        return listArticulos;
    }

    public void cargaArticulo() {
        System.out.println("Carga Estado : " + objArticulo.toString());
        Object Resulta[] = new Object[3];
        ComprasDT obj = new ComprasDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
        Resulta = ObjIni.Compras_Art(objCompra.getCod_emp(), objArticulo.getCod_articulo(), "N", "", objCompra.getCod_provedor());
        obj.setPorc_imp((int) Resulta[2]);
        obj.setStock((int) Resulta[0]);
        obj.setImp_costo((double) Resulta[1]);

        if (buscarElemento(obj) == false) {
            objCompra.getComprasDt().add(0, obj);
        }
        setObjArticulo(null);
        totalesFooter();
    }

    public void eliminarArticuloGrilla(ComprasDT obj) {
        if (buscarElemento(obj)) {
            objCompra.getComprasDt().remove(obj);
        }

    }

    public boolean buscarElemento(ComprasDT obj) {
        boolean valor = false;
        for (ComprasDT obj2 : objCompra.getComprasDt()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void totalesGrilla(ComprasDT obj) {
        System.out.println("TotalGrilla");
        System.out.println("Impuesto IVA " + obj.getPorc_imp());
        for (ComprasDT comprasDT : objCompra.getComprasDt()) {
            if (obj.getCod_articulo() == comprasDT.getCod_articulo()) {
                System.out.println("Objecto" + comprasDT.toString());
                comprasDT.setImp_neto(comprasDT.getImp_costo() * comprasDT.getCantidad());
                comprasDT.setImp_total(comprasDT.getImp_neto() + (comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100)));
                comprasDT.setImp_impuesto(comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100));
                System.out.println("Neto : " + comprasDT.getImp_neto());
                System.out.println("Neto : " + comprasDT.getPorc_imp());
                break;
            }
        }
        totalesFooter();
    }

    public void totalesFooter() {
        System.out.println("******************** TOTALES FOOTER *****************************");
        int stock = 0;
        double costo = 0;
        int cantidad = 0;
        double neto = 0;
        double total = 0;
        double impuesto = 0;
        for (ComprasDT comprasDT : objCompra.getComprasDt()) {
            stock = stock + comprasDT.getStock();
            costo = costo + (comprasDT.getImp_costo());
            cantidad = cantidad + comprasDT.getCantidad();
            neto = neto + comprasDT.getImp_neto();
            total = total + comprasDT.getImp_total();
            impuesto = impuesto + comprasDT.getImp_impuesto();
        }
        totales[0] = stock;
        totales[1] = formatter.format(costo);
        totales[2] = cantidad;
        totales[3] = formatter.format(neto);
        totales[4] = formatter.format(total);
        totales[5] = formatter.format(costo * cantidad);
        totales[6] = formatter.format(impuesto);

        objCompra.setImp_neto(costo);
        objCompra.setImp_impuesto(impuesto);
        objCompra.setImp_total(total);

    }

    public void resetTotales() {
        //Totales
        totales[0] = 0;
        totales[1] = 0;
        totales[2] = 0;
        totales[3] = 0;
        totales[4] = 0;
        totales[5] = 0;
        totales[6] = 0;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listArticulos.clear();
        ListCompra = Compra_service.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        ListCompra.clear();
        ListCompra = Compra_service.Lista("OC");
        this.valorBusqueda = "";
    }

    public ComprasService getCompra_service() {
        return Compra_service;
    }

    public void setCompra_service(ComprasService Compra_service) {
        this.Compra_service = Compra_service;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Validaciones getObjVal() {
        return ObjVal;
    }

    public void setObjVal(Validaciones ObjVal) {
        this.ObjVal = ObjVal;
    }

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public Compras getObjCompra() {
        if (objCompra == null) {
            objCompra = new Compras();
        }
        return objCompra;
    }

    public void setObjCompra(Compras objCompra) {
        this.objCompra = objCompra;
    }

    public List<Compras> getListCompra() {
        return ListCompra;
    }

    public void setListCompra(List<Compras> ListCompra) {
        this.ListCompra = ListCompra;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
    }

    public Articulos getObjArticulo() {
        if (objArticulo == null) {
            objArticulo = new Articulos();
        }
        return objArticulo;
    }

    public void setObjArticulo(Articulos objArticulo) {
        this.objArticulo = objArticulo;
    }

    public Object[] getAcciones() {
        return acciones;
    }

    public void setAcciones(Object[] acciones) {
        this.acciones = acciones;
    }

    public boolean isAceptar() {
        return aceptar;
    }

    public void setAceptar(boolean aceptar) {
        this.aceptar = aceptar;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public boolean isEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public boolean isBuscar() {
        return buscar;
    }

    public void setBuscar(boolean buscar) {
        this.buscar = buscar;
    }

    public boolean isReporte() {
        return reporte;
    }

    public void setReporte(boolean reporte) {
        this.reporte = reporte;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public List<Proveedores> getListProveedor() {
        return listProveedor;
    }

    public void setListProveedor(List<Proveedores> listProveedor) {
        this.listProveedor = listProveedor;
    }

    public Object[] getTotales() {
        return totales;
    }

    public void setTotales(Object[] totales) {
        this.totales = totales;
    }

    public List<Pagos> getListPagos() {
        return ListPagos;
    }

    public void setListPagos(List<Pagos> ListPagos) {
        this.ListPagos = ListPagos;
    }

    public NumberFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

    public List<Impuestos> getListImpuestos() {
        return listImpuestos;
    }

    public void setListImpuestos(List<Impuestos> listImpuestos) {
        this.listImpuestos = listImpuestos;
    }

}
