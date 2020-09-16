package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Compras.Compras;
import Modelo.Compras.RecepcionDT;
import Modelo.Compras.Pagos;
import Modelo.Compras.Proveedores;
import Modelo.Compras.Recepcion;
import Modelo.Bodega.Depositos;
import Modelo.Empresa;
import Servicios.RecepcionService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
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
public class RecepcionControlador {

    @Inject
    private RecepcionService recepcion_service;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Recepcion objRecepcion;
    private List<Recepcion> ListRecepcion = new ArrayList();
    NumberFormat formatter = NumberFormat.getCurrencyInstance();

    private List<Empresa> listEmpresas = new ArrayList();
    private List<Proveedores> listProveedor = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Pagos> ListPagos = new ArrayList();
    private List<Compras> listOcompras = new ArrayList();
    private Compras CompraSelect;

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

    @PostConstruct
    public void init() {
        try {
            getObjRecepcion();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RecepcionControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        ListRecepcion.clear();
        ListRecepcion = recepcion_service.Lista();

        if (condicion == 1) {
            //Empresas
            listEmpresas.clear();
            listArticulos.clear();
            ListPagos.clear();
            listOcompras.clear();
            listDepositos.clear();

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

            //Pagos
            JsonArray Jelementos3 = ObjIni.listObjectos("SELECT cod_pago, descripcion FROM public.m_pagos where activo='S'");
            for (JsonElement jsonElement : Jelementos3) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                    Pagos obj = new Pagos(new BigDecimal(map.get("cod_pago").toString()).intValue(),
                            map.get("descripcion").toString());
                    ListPagos.add(obj);
                }
            }

            //Depositos
            JsonArray Jelementos5 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
            for (JsonElement jsonElement : Jelementos5) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Depositos obj = new Depositos();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setNom_deposito(map.get("nom_deposito").toString());
                    listDepositos.add(obj);
                }
            }
        }
    }

    public void prepareNuevo() {
        setObjRecepcion(null);
        getObjRecepcion();
        getObjArticulo();
        listEmpresas.clear();
        listArticulos.clear();
        ListPagos.clear();
        listOcompras.clear();
        listDepositos.clear();

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

        //Pagos
        JsonArray Jelementos3 = ObjIni.listObjectos("SELECT cod_pago, descripcion FROM public.m_pagos where activo='S'");
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                Pagos obj = new Pagos(new BigDecimal(map.get("cod_pago").toString()).intValue(),
                        map.get("descripcion").toString());
                ListPagos.add(obj);
            }
        }

        //Compras
        JsonArray Jelementos4 = ObjIni.listObjectos("SELECT trans,cod_emp, A.cod_provedor, cod_docum, nro_docum, A.cod_fpago, \n"
                + " fec_doc, fec_entrega, imp_neto, imp_impuesto, imp_descuento, \n"
                + " imp_total, observaciones, B.razon_social\n"
                + " FROM public.t_compras A INNER JOIN m_proveedores B ON\n"
                + "A.cod_provedor=B.cod_provedor "
                + "WHERE nro_docum not in (select nro_doca from t_recepcion where nro_doca=A.nro_docum)");
        for (JsonElement jsonElement : Jelementos4) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                Compras obj = new Compras();

                obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                obj.setCod_docum(map.get("cod_docum").toString());
                obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                obj.setCod_fpago(new BigDecimal(map.get("cod_fpago").toString()).intValue());
                obj.setFec_doc(map.get("fec_doc").toString());
                obj.setFec_doc(map.get("fec_entrega").toString());
                obj.setImp_neto(new BigDecimal(map.get("imp_neto").toString()).doubleValue());
                obj.setImp_impuesto(new BigDecimal(map.get("imp_neto").toString()).doubleValue());
                obj.setImp_total(new BigDecimal(map.get("imp_neto").toString()).doubleValue());
                obj.setObservaciones(map.get("observaciones").toString());
                obj.setRazon_social(map.get("razon_social").toString());

                listOcompras.add(obj);
            }
        }

        //Depositos
        JsonArray Jelementos5 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos5) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos obj = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setNom_deposito(map.get("nom_deposito").toString());
                listDepositos.add(obj);
            }
        }

        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        objRecepcion.setCod_emp(listEmpresas.get(0).getCod_emp());
        objRecepcion.setCod_provedor(listProveedor.get(0).getCod_provedor());
        objRecepcion.setD_fec_doc(new Date());

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
        ListRecepcion.clear();
        this.evento = "Buscar";
        setObjRecepcion(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Recepcion objecto, int condicion) {
        setObjRecepcion(null);
        Object Resulta[] = new Object[2];
        Resulta = recepcion_service.recuperarInfo(objecto);
        setObjRecepcion((Recepcion) Resulta[0]);
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
        setObjRecepcion(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = recepcion_service.Transaccion(objRecepcion, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = recepcion_service.Transaccion(objRecepcion, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = recepcion_service.Transaccion(objRecepcion, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("reporte");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(RecepcionControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = recepcion_service.buscarDoc(objRecepcion);
//                    break;//                case "Buscar":
//                    Resulta = recepcion_service.buscarDoc(objRecepcion);
//                    break;//                case "Buscar":
//                    Resulta = recepcion_service.buscarDoc(objRecepcion);
//                    break;//                case "Buscar":
//                    Resulta = recepcion_service.buscarDoc(objRecepcion);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    ListRecepcion.clear();
                    ListRecepcion = (List<Recepcion>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjRecepcion(null);
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
        getObjRecepcion();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objRecepcion.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

//    public void cargarDepositos() {
//        System.out.println("Cargando cargarDepositos " + objRecepcion.getCod_emp());
//        listDepositos.clear();
//        //Depositos
//        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + objRecepcion.getCod_emp() + "' and activo='S'");
//        for (JsonElement jsonElement : Jelementos2) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Depositos obj = new Depositos();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_deposito(map.get("cod_deposito").toString());
//                obj.setNom_deposito(map.get("nom_deposito").toString());
//                listDepositos.add(obj);
//            }
//        }
//        cargarUbicaciones();
//    }
//
//    public void cargarUbicaciones() {
//        System.out.println("Cargando cargarDepositos " + objRecepcion.toString());
//        if (objRecepcion.getCod_emp() == null) {
//            System.out.println("Objecto Null");
//        }
//        listUbicaciones.clear();
//        //Ubicaciones
////        System.out.println("Variable 1 : " + variables[0].toString());
////        System.out.println("Variable 1 : " + variables[1].toString());
//        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_ubicacion,nom_ubicacion from m_ubicaciones where cod_emp='" + objRecepcion.getCod_emp() + "' "
//                + "and cod_deposito='" + objRecepcion.getCod_deposito() + "' and activo='S'");
//        for (JsonElement jsonElement : Jelementos4) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Ubicaciones obj = new Ubicaciones();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_ubicacion(map.get("cod_ubicacion").toString());
//                obj.setNom_ubicacion(map.get("nom_ubicacion").toString());
//                listUbicaciones.add(obj);
//            }
//        }
//    }
    public List<Articulos> articulos(String query) {
        getObjArticulo();
        System.out.println("------------ Bean articulos------------");
        System.out.println("Articulo : " + objArticulo.toString());

        listArticulos = SelService.cargaArticulo(query, 2);
        return listArticulos;
    }

    public void cargaArticulo() {
        System.out.println("Carga Estado : " + objArticulo.toString());
        RecepcionDT obj = new RecepcionDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
        obj.setStock(ObjIni.StockDisponible(objRecepcion.getCod_emp(), objArticulo.getCod_articulo(), "N", ""));
        if (buscarElemento(obj) == false) {
            objRecepcion.getRecepcionDT().add(0, obj);
        }
        setObjArticulo(null);
        totalesFooter();
    }

    public void eliminarArticuloGrilla(RecepcionDT obj) {
        System.out.println("*********************** eliminarArticuloGrilla");
        if (buscarElemento(obj)) {
            objRecepcion.getRecepcionDT().remove(obj);
        }

    }

    public boolean buscarElemento(RecepcionDT obj) {
        boolean valor = false;
        for (RecepcionDT obj2 : objRecepcion.getRecepcionDT()) {
            System.out.println(" " + obj.getCod_articulo() + " = " + obj2.getCod_articulo());
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void totalesGrilla(RecepcionDT obj) {
        System.out.println("TotalGrilla");

        for (RecepcionDT comprasDT : objRecepcion.getRecepcionDT()) {
            if (obj.getCod_articulo() == comprasDT.getCod_articulo()) {
                comprasDT.setImp_neto(comprasDT.getImp_costo() * comprasDT.getCantidad());
                comprasDT.setImp_total(comprasDT.getImp_neto() + (comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100)));
                comprasDT.setImp_impuesto(comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100));
                System.out.println("Neto : " + comprasDT.getImp_neto());
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
        for (RecepcionDT comprasDT : objRecepcion.getRecepcionDT()) {
            stock = stock + comprasDT.getStock();
            costo = costo + comprasDT.getImp_costo();
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

        objRecepcion.setImp_neto(costo);
        objRecepcion.setImp_impuesto(impuesto);
        objRecepcion.setImp_total(total);

    }

    public void cargarOrdenCompra() {
        System.out.println("Orden de compra : " + CompraSelect.toString());
        objRecepcion.setCod_emp(CompraSelect.getCod_emp());
        objRecepcion.setCod_provedor(CompraSelect.getCod_provedor());
        objRecepcion.setCod_fpago(CompraSelect.getCod_fpago());
        objRecepcion.setNro_doca(CompraSelect.getNro_docum());
        objRecepcion.setCod_doca(CompraSelect.getCod_docum());
        objRecepcion.setObservaciones(CompraSelect.getObservaciones());
        objRecepcion.getRecepcionDT().clear();
        //Articulos
        JsonArray Jelementos3 = ObjIni.listObjectos("SELECT a.cod_articulo, stock, cantidad, imp_costo, impuesto, a.porc_imp, \n"
                + "imp_impuesto, imp_neto, imp_total, linea,B.codigo,b.nom_articulo\n"
                + "FROM public.td_compras A INNER JOIN m_articulos B ON a.cod_articulo=b.cod_articulo\n"
                + "where trans=" + CompraSelect.getTrans());
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                RecepcionDT obj = new RecepcionDT();

                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                obj.setStock(0);
                obj.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                obj.setImp_costo(new BigDecimal(map.get("imp_costo").toString()).intValue());
                obj.setImpuesto(map.get("impuesto").toString());
                obj.setPorc_imp(new BigDecimal(map.get("porc_imp").toString()).intValue());
                obj.setImp_impuesto(new BigDecimal(map.get("imp_impuesto").toString()).intValue());
                obj.setImp_neto(new BigDecimal(map.get("imp_neto").toString()).intValue());
                obj.setImp_total(new BigDecimal(map.get("imp_total").toString()).intValue());
                obj.setLinea(new BigDecimal(map.get("linea").toString()).intValue());
                obj.setCodigo(map.get("codigo").toString());
                obj.setNom_articulo(map.get("nom_articulo").toString());

                obj.setCodigonew(obj.getCodigo());

                objRecepcion.getRecepcionDT().add(obj);
            }
        }
        totalesFooter();
    }

    public RecepcionService getRecepcion_service() {
        return recepcion_service;
    }

    public void setRecepcion_service(RecepcionService recepcion_service) {
        this.recepcion_service = recepcion_service;
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

    public Recepcion getObjRecepcion() {
        if (objRecepcion == null) {
            objRecepcion = new Recepcion();
        }
        return objRecepcion;
    }

    public void setObjRecepcion(Recepcion objRecepcion) {
        this.objRecepcion = objRecepcion;
    }

    public List<Recepcion> getListRecepcion() {
        return ListRecepcion;
    }

    public void setListRecepcion(List<Recepcion> ListRecepcion) {
        this.ListRecepcion = ListRecepcion;
    }

    public NumberFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public List<Proveedores> getListProveedor() {
        return listProveedor;
    }

    public void setListProveedor(List<Proveedores> listProveedor) {
        this.listProveedor = listProveedor;
    }

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
    }

    public List<Pagos> getListPagos() {
        return ListPagos;
    }

    public void setListPagos(List<Pagos> ListPagos) {
        this.ListPagos = ListPagos;
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

    public Object[] getTotales() {
        return totales;
    }

    public void setTotales(Object[] totales) {
        this.totales = totales;
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

    public List<Compras> getListOcompras() {
        return listOcompras;
    }

    public void setListOcompras(List<Compras> listOcompras) {
        this.listOcompras = listOcompras;
    }

    public Compras getCompraSelect() {
        if (CompraSelect == null) {
            CompraSelect = new Compras();
        }
        return CompraSelect;
    }

    public void setCompraSelect(Compras CompraSelect) {
        this.CompraSelect = CompraSelect;
    }

    public List<Depositos> getListDepositos() {
        return listDepositos;
    }

    public void setListDepositos(List<Depositos> listDepositos) {
        this.listDepositos = listDepositos;
    }

}
