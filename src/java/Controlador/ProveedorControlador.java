package Controlador;

import Modelo.Ciudades;
import Modelo.Compras.*;
import Modelo.Documentos;
import Servicios.ProveedorService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class ProveedorControlador {

    @Inject
    private ProveedorService ProveService;

    @Inject
    private Inicializacion ObjIni;
//
//    @Inject
//    private Validaciones ObjVal;
//
    @Inject
    private Seleccion SelService;

    private Proveedores objProveedor;
    private Ciudades objCiudad;
    private Prove_Vendedor objVendedor;
    private Provee_Bancos objCuentas;

    private List<Proveedores> listaProveedor = new ArrayList();
    private List<Documentos> listDoc = new ArrayList();
    private List<Bancos> listBancos = new ArrayList();
    private List<Pagos> ListPagos = new ArrayList();

    //Campos Para Botonera
    Object acciones[] = new Object[6];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private boolean datos;
    private String evento;
    private String valorBusqueda;

    @PostConstruct
    public void init() {
        try {
            getObjProveedor();
            lista(1);
            this.evento = "inicio";
            this.datos = true;
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ProveedorControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listaProveedor.clear();
        listBancos.clear();
        listDoc.clear();
        ListPagos.clear();
        listaProveedor = ProveService.Lista();

        //Documentos
        JsonArray Jelementos = ObjIni.listObjectos("select cod_tipodoc,nombredocumento from m_tipodocumentos");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Documentos doc = new Documentos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                doc.setCod_tipodoc(map.get("cod_tipodoc").toString());
                doc.setNombredocumento(map.get("nombredocumento").toString());
                listDoc.add(doc);
            }
        }

        //Bancos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_banco,descripcion from m_bancos");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                Bancos obj = new Bancos(new BigDecimal(map.get("cod_banco").toString()).intValue(),
                        map.get("descripcion").toString());
//                obj.setActivo(map.get("activo").toString());
                listBancos.add(obj);
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
    }

    public void prepareNuevo() {
        setObjProveedor(null);
        getObjProveedor();
        this.objProveedor.setActivoB(true);
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
        listaProveedor.clear();
        this.evento = "Buscar";
        setObjProveedor(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Proveedores objecto, int condicion) {
        setObjProveedor(null);
        setObjCiudad(null);
        Object Resulta[] = new Object[2];
        Resulta = ProveService.recuperarInfo(objecto);
        setObjProveedor((Proveedores) Resulta[0]);
        setObjCiudad((Ciudades) Resulta[1]);
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
        setObjProveedor(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        objProveedor.setCod_ciudad(objCiudad.getCod_ciudad());
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = ProveService.Transaccion(objProveedor, "Nuevo");
                    mns = "Proveedor Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = ProveService.Transaccion(objProveedor, "Borrar");
                    mns = "Proveedor Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = ProveService.Transaccion(objProveedor, "Editar");
                    mns = "Proveedor Editado exitosamente";
                    break;
                case "Reporte": {
//                    try {
//                        Resulta = SelService.PDFDescargar2("reporte");
//                    } catch (IOException ex) {
//                        System.out.println("Error reporte");
//                        Logger.getLogger(ProveedorControlador.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = ProveService.buscarDoc(objProveedor);
//                    break;//                case "Buscar":
//                    Resulta = ProveService.buscarDoc(objProveedor);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listaProveedor.clear();
                    listaProveedor = (List<Proveedores>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjProveedor(null);
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
        getObjProveedor();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objProveedor.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public List<Ciudades> ciudades(String query) {
        System.out.println("------------ Bean ciudades------------");
        List<Ciudades> listCiudades = new ArrayList();
        listCiudades = SelService.cargaCiudad(query, 2);
        return listCiudades;
    }

    //************************************************ESTANDAR
    public void agregarVenedor() {
        getObjVendedor();
        System.out.println("Entro agregar vendedor");
        System.out.println("- " + objVendedor.toString());
        objProveedor.getList_vend().add(objVendedor);
        setObjVendedor(null);
        getObjVendedor();
    }

    public void agregarCuentas() {
        getObjCuentas();
        System.out.println("Entro agregar cuenta");
        System.out.println("- " + objCuentas.toString());
        for (Bancos listBanco : listBancos) {
            if (objCuentas.getCod_banco() == listBanco.getCod_banco()) {
                objCuentas.setNom_banco(listBanco.getDescripcion());
            }
        }
        objProveedor.getList_Bancos().add(objCuentas);
        setObjCuentas(null);
        getObjCuentas();
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listaProveedor.clear();
        listaProveedor = ProveService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listaProveedor.clear();
        listaProveedor = ProveService.Lista();
        this.valorBusqueda = "";
    }

    public void quitarVendedor(Prove_Vendedor obj) {
        System.out.println("Entro quitar Tel : " + obj.toString());
        objProveedor.getList_vend().remove(obj);
    }

    public void quitarCuenta(Provee_Bancos obj) {
        System.out.println("Entro quitar Tel : " + obj.toString());
        objProveedor.getList_Bancos().remove(obj);
    }

    public ProveedorService getProveService() {
        return ProveService;
    }

    public void setProveService(ProveedorService ProveService) {
        this.ProveService = ProveService;
    }

    public Proveedores getObjProveedor() {
        if (objProveedor == null) {
            objProveedor = new Proveedores();
        }
        return objProveedor;
    }

    public void setObjProveedor(Proveedores objProveedor) {
        this.objProveedor = objProveedor;
    }

    public List<Proveedores> getListaProveedor() {
        return listaProveedor;
    }

    public void setListaProveedor(List<Proveedores> listaProveedor) {
        this.listaProveedor = listaProveedor;
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

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public List<Documentos> getListDoc() {
        return listDoc;
    }

    public void setListDoc(List<Documentos> listDoc) {
        this.listDoc = listDoc;
    }

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public Ciudades getObjCiudad() {
        if (objCiudad == null) {
            objCiudad = new Ciudades();
        }
        return objCiudad;
    }

    public void setObjCiudad(Ciudades objCiudad) {
        this.objCiudad = objCiudad;
    }

    public boolean isDatos() {
        return datos;
    }

    public void setDatos(boolean datos) {
        this.datos = datos;
    }

    public Provee_Bancos getObjCuentas() {
        if (objCuentas == null) {
            objCuentas = new Provee_Bancos();
        }
        return objCuentas;
    }

    public void setObjCuentas(Provee_Bancos objCuentas) {
        this.objCuentas = objCuentas;
    }

    public List<Bancos> getListBancos() {
        return listBancos;
    }

    public void setListBancos(List<Bancos> listBancos) {
        this.listBancos = listBancos;
    }

    public Prove_Vendedor getObjVendedor() {
        if (objVendedor == null) {
            objVendedor = new Prove_Vendedor();
        }
        return objVendedor;
    }

    public void setObjVendedor(Prove_Vendedor objVendedor) {
        this.objVendedor = objVendedor;
    }

    public List<Pagos> getListPagos() {
        return ListPagos;
    }

    public void setListPagos(List<Pagos> ListPagos) {
        this.ListPagos = ListPagos;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

}
