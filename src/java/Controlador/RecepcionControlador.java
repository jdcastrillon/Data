package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Compras.Compras;
import Modelo.Compras.RecepcionDT;
import Modelo.Compras.Pagos;
import Modelo.Compras.Recepcion;
import Modelo.Bodega.Depositos;
import Modelo.Compras.Impuestos;
import Modelo.Empresa;
import Servicios.RecepcionService;
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
    private List<Articulos> listArticulos = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Pagos> ListPagos = new ArrayList();
    private List<Compras> listOcompras = new ArrayList();
    private List<Impuestos> listImpuestos = new ArrayList();
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
        resetTotales();
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
        listImpuestos.clear();

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
                + "WHERE nro_docum not in (select nro_doca from t_recepcion where nro_doca=A.nro_docum) and nro_docum>0 ");
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

        //Impuestos
        JsonArray Jelementos6 = ObjIni.listObjectos("select cod_impuesto,porc_imp from m_impuestos where cod_impuesto='IVA' and cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");

        for (JsonElement jsonElement : Jelementos6) {
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
        objRecepcion.setCod_emp(listEmpresas.get(0).getCod_emp());
        objRecepcion.setD_fec_doc(new Date());
        objRecepcion.setCod_deposito("0");
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
                    articulosNuevos();
                    Resulta = recepcion_service.Transaccion(objRecepcion, "Nuevo");
                    mns = "Recepcion Exitosa";
                    break;
                case "Eliminar":
                    Resulta = recepcion_service.Transaccion(objRecepcion, "Borrar");
                    mns = "Recepcion Eliminada";
                    break;
                case "Editar":
                    articulosNuevos();
                    Resulta = recepcion_service.Transaccion(objRecepcion, "Editar");
                    mns = "Recepcion Editada";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                lista(2);
                setObjRecepcion(null);
                this.evento = "inicio";
                controlEventos(evento);
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

        if (objRecepcion.getCod_provedor() == 0) {
            mns = "Debe Selecionar un Proveedor";
        }
        if (objRecepcion.getFactura().equalsIgnoreCase("0")) {
            mns = "Debe Digitar la Factura";
        }
        
        if (objRecepcion.getCod_deposito().equalsIgnoreCase("0")) {
            mns = "Debe Selecionar una Bodega";
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void articulosNuevos() {
        for (RecepcionDT comprasDT : objRecepcion.getRecepcionDT()) {
            comprasDT.setCambia_codigo("N");
            if (!comprasDT.getCodigo().equalsIgnoreCase(comprasDT.getCodigonew())) {
                comprasDT.setCambia_codigo("S");
                comprasDT.setCod_articulo2(ObjIni.codigo_articulo_nuevo(comprasDT.getCodigonew()));
            }
        }
    }

    public void nombre_articulo(int cod_articulo, String codigo) {
        String nombre = "";
        boolean existeCodigo = false;
        JsonArray Jelementos = ObjIni.listObjectos("select nom_articulo from m_articulos where codigo='" + codigo + "'");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                nombre = (map.get("nom_articulo").toString());
                existeCodigo = true;
            }
        }
        System.out.println("Codigo : " + cod_articulo);
        System.out.println("Nom_articulo : " + nombre);
        if (existeCodigo) {
            for (RecepcionDT recepcionDT : objRecepcion.getRecepcionDT()) {
                System.out.println(recepcionDT.getCod_articulo() + "=" + cod_articulo);
                if (recepcionDT.getCod_articulo() == cod_articulo) {
                    recepcionDT.setNom_articulo(nombre);
                    System.out.println("Actualizo ");
                }
            }
        }
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
        obj.setCodigonew(objArticulo.getCodigo());
//        obj.setStock(ObjIni.StockDisponible(objRecepcion.getCod_emp(), objArticulo.getCod_articulo(), "N", ""));
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
        objRecepcion.setNom_proveedor(CompraSelect.getRazon_social());
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

    public void datosBean() {
        System.out.println("Datos Bean signo " + objRecepcion.getFactura());
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

    public List<Impuestos> getListImpuestos() {
        return listImpuestos;
    }

    public void setListImpuestos(List<Impuestos> listImpuestos) {
        this.listImpuestos = listImpuestos;
    }

}
