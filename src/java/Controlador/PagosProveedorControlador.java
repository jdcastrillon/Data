package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Compras.*;
import Modelo.Empresa;
import Servicios.PagosProveedorService;
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
public class PagosProveedorControlador {

    @Inject
    private PagosProveedorService pagos_Service;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private PagosProv objPagos;
    private List<PagosProv> ListaPagos = new ArrayList();
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
        
    private List<Empresa> listEmpresas = new ArrayList();    
    private List<Proveedores> listProveedor = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
       

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
            getObjPagos();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PagosProveedorControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        ListaPagos.clear();
        ListaPagos = pagos_Service.Lista();

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
            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_provedor,cod_documento,razon_social from m_proveedores limit 1");

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
        setObjPagos(null);
        getObjPagos();
        listEmpresas.clear();
        listArticulos.clear();
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

       
        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        objPagos.setCod_emp(listEmpresas.get(0).getCod_emp());
//        objPagos.setCod_provedor(listProveedor.get(0).getCod_provedor());
        objPagos.setD_fec_doc(new Date());

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
        ListaPagos.clear();
        this.evento = "Buscar";
        setObjPagos(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(PagosProv objecto, int condicion) {
        setObjPagos(null);
        Object Resulta[] = new Object[2];
        Resulta = pagos_Service.recuperarInfo(objecto);
        setObjPagos((PagosProv) Resulta[0]);
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
        setObjPagos(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = pagos_Service.Transaccion(objPagos, "Nuevo");
                    mns = "O.C Realizada exitosamente";
                    break;
                case "Eliminar":
                    Resulta = pagos_Service.Transaccion(objPagos, "Borrar");
                    mns = "O.C Eliminada exitosamente";
                    break;
                case "Editar":
                    Resulta = pagos_Service.Transaccion(objPagos, "Editar");
                    mns = "O.C Editado exitosamente";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    ListaPagos.clear();
                    ListaPagos = (List<PagosProv>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjPagos(null);
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
        getObjPagos();
        String mns = "";

        if (this.nuevo == false) {
            //Validaciones
            if (objPagos.getCod_provedor() == 0) {
                mns = "Debe Selecionar un Proveedor";
            }
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objPagos.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }


    public List<Articulos> articulos(String query) {
        getObjArticulo();
        System.out.println("------------ Bean articulos------------");
        System.out.println("Articulo : " + objArticulo.toString());

        listArticulos = SelService.cargaArticulo(query, 2);
        return listArticulos;
    }

    public void cargaArticulo() {
//        System.out.println("Carga Estado : " + objArticulo.toString());
//        Object Resulta[] = new Object[3];
//        PagosProvDT obj = new PagosProvDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
//        Resulta = ObjIni.PagosProv_Art(objPagos.getCod_emp(), objArticulo.getCod_articulo(), "S", objPagos.getCod_deposito(), objPagos.getCod_provedor());
//        obj.setPorc_imp((int) Resulta[2]);
//        obj.setStock((int) Resulta[0]);
//        obj.setImp_costo((double) Resulta[1]);
//
//        if (buscarElemento(obj) == false) {
//            objPagos.getPagosProvDt().add(0, obj);
//        }
//        setObjArticulo(null);
    }

    public void eliminarArticuloGrilla(PagosProvDT obj) {
//        if (buscarElemento(obj)) {
//            objPagos.getPagosProvDt().remove(obj);
//        }

    }

    public boolean buscarElemento(PagosProvDT obj) {
        boolean valor = false;
//        for (PagosProvDT obj2 : objPagos.getPagosProvDt()) {
//            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
//                valor = true;
//                break;
//            }
//        }
        return valor;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listArticulos.clear();
        ListaPagos = pagos_Service.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        ListaPagos.clear();
        ListaPagos = pagos_Service.Lista();
        this.valorBusqueda = "";
    }

    public void datosBean() {
        System.out.println("Datos Bean signo " );
    }

    public PagosProveedorService getPagos_Service() {
        return pagos_Service;
    }

    public void setPagos_Service(PagosProveedorService pagos_Service) {
        this.pagos_Service = pagos_Service;
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

    public PagosProv getObjPagos() {
        if(objPagos==null){
            objPagos=new PagosProv();
        }
        return objPagos;
    }

    public void setObjPagos(PagosProv objPagos) {
        this.objPagos = objPagos;
    }

    public List<PagosProv> getListaPagos() {
        return ListaPagos;
    }

    public void setListaPagos(List<PagosProv> ListaPagos) {
        this.ListaPagos = ListaPagos;
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

    public Articulos getObjArticulo() {
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

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

  
}
