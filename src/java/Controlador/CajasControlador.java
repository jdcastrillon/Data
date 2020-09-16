package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Ciudades;
import Modelo.Comercial.Cajas;
import Modelo.Comercial.Cajas;
import Modelo.Correos;
import Modelo.Bodega.Depositos;
import Modelo.Documentos;
import Modelo.Empresa;
import Modelo.Bodega.Estados;
import Modelo.Sucursal;
import Modelo.Telefonos;
import Modelo.Bodega.Ubicaciones;
import ModeloService.iniciarSesion;
import Servicios.CajaService;
import Servicios.ProveedorService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.math.BigDecimal;
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
public class CajasControlador {

    @Inject
    private CajaService cajaService;

    @Inject
    private Inicializacion ObjIni;
//
//    @Inject
//    private Validaciones ObjVal;
//
    @Inject
    private Seleccion SelService;

    private Cajas objCaja;

    private List<Cajas> listCaja = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Sucursal> listSucursal = new ArrayList();

    Object acciones[] = new Object[6];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private boolean datos;
    private String evento;

    @PostConstruct
    public void init() {
        try {
            getObjCaja();
            lista(1);
            this.evento = "inicio";
            this.datos = true;
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CajasControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listCaja.clear();
        listCaja = cajaService.Lista();

        if (condicion == 1) {
            listEmpresas.clear();
            listSucursal.clear();
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
        }

    }

    public void prepareNuevo() {
        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");
        setObjCaja(null);
        getObjCaja();
        this.evento = "Nuevo";
        this.objCaja.setUsuario(login.getUsuario());
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
        listCaja.clear();
        this.evento = "Buscar";
        setObjCaja(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Cajas objecto, int condicion) {
        setObjCaja(null);
        Object Resulta[] = new Object[2];
        Resulta = cajaService.recuperarInfo(objecto);
        setObjCaja((Cajas) Resulta[0]);
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
        setObjCaja(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = cajaService.Transaccion(objCaja, "Nuevo");
                    mns = "Proveedor Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = cajaService.Transaccion(objCaja, "Borrar");
                    mns = "Proveedor Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = cajaService.Transaccion(objCaja, "Editar");
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
//                    Resulta = cajaService.buscarDoc(objCaja);
//                    break;//                case "Buscar":
//                    Resulta = cajaService.buscarDoc(objCaja);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listCaja.clear();
                    listCaja = (List<Cajas>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjCaja(null);
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
        getObjCaja();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objCaja.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void cargarSucursales() {
        System.out.println("Cargando sucursales " + objCaja.getCod_emp());
        listSucursal.clear();
        //Sucursales
        JsonArray Jelementos = ObjIni.listObjectos("select cod_sucursal,nom_sucursal from m_sucursales where estado='A' and cod_emp='" + objCaja.getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Sucursal sucu = new Sucursal();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                sucu.setCod_sucursal(map.get("cod_sucursal").toString());
                sucu.setNom_sucursal(map.get("nom_sucursal").toString());
                listSucursal.add(sucu);
            }
        }
    }

    public CajaService getCajaService() {
        return cajaService;
    }

    public void setCajaService(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public Cajas getObjCaja() {
        if (objCaja == null) {
            objCaja = new Cajas();
        }
        return objCaja;
    }

    public void setObjCaja(Cajas objCaja) {
        this.objCaja = objCaja;
    }

    public List<Cajas> getListCaja() {
        return listCaja;
    }

    public void setListCaja(List<Cajas> listCaja) {
        this.listCaja = listCaja;
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

    public boolean isDatos() {
        return datos;
    }

    public void setDatos(boolean datos) {
        this.datos = datos;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public List<Sucursal> getListSucursal() {
        return listSucursal;
    }

    public void setListSucursal(List<Sucursal> listSucursal) {
        this.listSucursal = listSucursal;
    }

}
