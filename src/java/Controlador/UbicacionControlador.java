package Controlador;

import Modelo.Bodega.Depositos;
import Modelo.Bodega.Ubicaciones;
import Modelo.Empresa;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Validaciones;
import Servicios.UbicacionService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
public class UbicacionControlador {

    @Inject
    private UbicacionService ubiService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Ubicaciones ObjUbicaciones;
    private List<Ubicaciones> listUbicaciones = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();

    private Depositos ObjDepo;
    private List<Depositos> ListDepositos = new ArrayList();

    //Campos Para Botonera
    Object acciones[] = new Object[5];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private String evento;
    private String valorBusqueda;

    @PostConstruct
    public void init() {
        try {
            getObjUbicaciones();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UbicacionControlador() {
    }

    public void lista(int condicion) {
        listUbicaciones.clear();
        listUbicaciones = ubiService.Lista();

        if (condicion == 1) {
            listEmpresas.clear();
            ListDepositos.clear();

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

            //Depositos
            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
            for (JsonElement jsonElement : Jelementos2) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Depositos obj = new Depositos();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setNom_deposito(map.get("nom_deposito").toString());
                    ListDepositos.add(obj);
                }
            }

        }
    }

    public void prepareNuevo() {
        setObjUbicaciones(null);
        getObjUbicaciones();

        listEmpresas.clear();
        ListDepositos.clear();

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

        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos obj = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setNom_deposito(map.get("nom_deposito").toString());
                ListDepositos.add(obj);
            }
        }

        this.evento = "Nuevo";
        ObjUbicaciones.setActivoUbi(true);
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
        listUbicaciones.clear();
        this.evento = "Buscar";
        setObjUbicaciones(null);
        controlEventos(evento);
    }
//

    public void prepareCrud(Ubicaciones objecto, int condicion) {
        setObjUbicaciones(null);
        Object Resulta[] = new Object[2];
        Resulta = ubiService.recuperarInfo(objecto);
        setObjUbicaciones((Ubicaciones) Resulta[0]);
        //Depositos
//        ListDepositos.clear();
//        setObjUbicaciones((Ubicaciones) Resulta[0]);
//        setObjDepo((Depositos) Resulta[1]);
//        ListDepositos.add(ObjDepo);
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
        setObjUbicaciones(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = ubiService.Transaccion(ObjUbicaciones, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = ubiService.Transaccion(ObjUbicaciones, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = ubiService.Transaccion(ObjUbicaciones, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
//                case "Buscar":
//                    Resulta = ubiService.buscarDoc(objDeposito);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listUbicaciones.clear();
                    listUbicaciones = (List<Ubicaciones>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjUbicaciones(null);
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
    }

    public boolean validaciones() {
        getObjUbicaciones();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objDeposito.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void generarUbicacion() {
        System.out.println("Generar Ubicacion");
        ObjUbicaciones.setCod_ubicacion(ObjUbicaciones.getPasillo() + ObjUbicaciones.getColumna() + ObjUbicaciones.getNivel());
    }

    public void cargarDepositos() {
        System.out.println("Cargando sucursales " + ObjUbicaciones.getCod_emp());
        ListDepositos.clear();
        //Depositos
        JsonArray Jelementos = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where activo='S' and cod_emp='" + ObjUbicaciones.getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos sucu = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                sucu.setCod_deposito(map.get("cod_deposito").toString());
                sucu.setNom_deposito(map.get("nom_deposito").toString());
                ListDepositos.add(sucu);
            }
        }
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listUbicaciones.clear();
        listUbicaciones = ubiService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listUbicaciones.clear();
        listUbicaciones = ubiService.Lista();
        this.valorBusqueda = "";
    }

    public boolean isAceptar() {
        return aceptar;
    }

    public void setAceptar(boolean aceptar) {
        this.aceptar = aceptar;
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

    public Validaciones getObjVal() {
        return ObjVal;
    }

    public void setObjVal(Validaciones ObjVal) {
        this.ObjVal = ObjVal;
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

    public Object[] getAcciones() {
        return acciones;
    }

    public void setAcciones(Object[] acciones) {
        this.acciones = acciones;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public UbicacionService getUbiService() {
        return ubiService;
    }

    public void setUbiService(UbicacionService ubiService) {
        this.ubiService = ubiService;
    }

    public Ubicaciones getObjUbicaciones() {
        if (ObjUbicaciones == null) {
            ObjUbicaciones = new Ubicaciones();
        }
        return ObjUbicaciones;
    }

    public void setObjUbicaciones(Ubicaciones ObjUbicaciones) {
        this.ObjUbicaciones = ObjUbicaciones;
    }

    public List<Ubicaciones> getListUbicaciones() {
        return listUbicaciones;
    }

    public void setListUbicaciones(List<Ubicaciones> listUbicaciones) {
        this.listUbicaciones = listUbicaciones;
    }

    public Depositos getObjDepo() {
        if (ObjDepo == null) {
            ObjDepo = new Depositos();
        }
        return ObjDepo;
    }

    public void setObjDepo(Depositos ObjDepo) {
        this.ObjDepo = ObjDepo;
    }

    public List<Depositos> getListDepositos() {
        return ListDepositos;
    }

    public void setListDepositos(List<Depositos> ListDepositos) {
        this.ListDepositos = ListDepositos;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

}
