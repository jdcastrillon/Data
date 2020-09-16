package Controlador;

import Modelo.Ciudades;
import Modelo.Bodega.Depositos;
import Modelo.Empresa;
import Modelo.Sucursal;
import Servicios.DepostivosService;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Validaciones;
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
public class DepositoControlador {

    @Inject
    private DepostivosService depositoService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Depositos objDeposito;
    private List<Depositos> listDeposito = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Sucursal> listSucursal = new ArrayList();

    private Ciudades objCiudad;

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
            getObjDeposito();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DepositoControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listDeposito.clear();
        listDeposito = depositoService.Lista();

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
        setObjDeposito(null);
        setObjCiudad(null);
        getObjDeposito();
        getObjCiudad();

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

        objDeposito.setCod_emp(listEmpresas.get(0).getCod_emp());

        //Sucursales
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_sucursal,nom_sucursal from m_sucursales where estado='A' and cod_emp='" + objDeposito.getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Sucursal sucu = new Sucursal();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                sucu.setCod_sucursal(map.get("cod_sucursal").toString());
                sucu.setNom_sucursal(map.get("nom_sucursal").toString());
                listSucursal.add(sucu);
            }
        }

        this.evento = "Nuevo";
        objDeposito.setActDepo(false);
        objDeposito.setDepositoP(false);
        objDeposito.setTieneUbi(false);
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
        listDeposito.clear();
        this.evento = "Buscar";
        setObjDeposito(null);
        controlEventos(evento);
    }

    public void prepareCrud(Depositos objecto, int condicion) {
        setObjDeposito(null);
        Object Resulta[] = new Object[2];
        Resulta = depositoService.recuperarInfo(objecto);
        setObjDeposito((Depositos) Resulta[0]);
        setObjCiudad((Ciudades) Resulta[1]);
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
        setObjDeposito(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    objDeposito.setCod_ciudad(objCiudad.getCod_ciudad());
                    Resulta = depositoService.Transaccion(objDeposito, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = depositoService.Transaccion(objDeposito, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = depositoService.Transaccion(objDeposito, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listDeposito.clear();
                    listDeposito = (List<Depositos>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjDeposito(null);
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
        getObjDeposito();
        String mns = "";
        System.out.println("Articulo " + objDeposito.toString());
        if (this.nuevo == false) {
            //Validaciones
            if (ObjVal.ValPrimaryKey("select count(*) from m_depositos where cod_deposito='" + objDeposito.getCod_deposito().trim() + "'")) {
                mns = "El codigo del deposito ya existe";
            } else if (objDeposito.getCod_deposito().isEmpty()) {
                mns = "Codigo del deposito Obligatorio";
            } else if (objDeposito.getNom_deposito().isEmpty()) {
                mns = "Nombre del deposito Obligatorio";
            } else if (objDeposito.getCod_emp().isEmpty()) {
                mns = "Empresa del deposito Obligatorio";
            } else if (objDeposito.getCod_sucursal().isEmpty()) {
                mns = "Sucursal del deposito Obligatorio";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }


    public void cargarSucursales() {
        System.out.println("Cargando sucursales " + objDeposito.getCod_emp());
        listSucursal.clear();
        //Sucursales
        JsonArray Jelementos = ObjIni.listObjectos("select cod_sucursal,nom_sucursal from m_sucursales where estado='A' and cod_emp='" + objDeposito.getCod_emp() + "'");
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

    public List<Ciudades> ciudades(String query) {
        System.out.println("------------ Bean ciudades------------");
        List<Ciudades> listCiudades = new ArrayList();
        listCiudades = SelService.cargaCiudad(query, 2);
        return listCiudades;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listDeposito.clear();
        listDeposito = depositoService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listDeposito.clear();
        listDeposito = depositoService.Lista();
        this.valorBusqueda = "";
    }

    public DepostivosService getDepositoService() {
        return depositoService;
    }

    public void setDepositoService(DepostivosService depositoService) {
        this.depositoService = depositoService;
    }

    public Depositos getObjDeposito() {
        if (objDeposito == null) {
            objDeposito = new Depositos();
        }
        return objDeposito;
    }

    public void setObjDeposito(Depositos objDeposito) {
        this.objDeposito = objDeposito;
    }

    public List<Depositos> getListDeposito() {
        return listDeposito;
    }

    public void setListDeposito(List<Depositos> listDeposito) {
        this.listDeposito = listDeposito;
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

    public List<Sucursal> getListSucursal() {
        return listSucursal;
    }

    public void setListSucursal(List<Sucursal> listSucursal) {
        this.listSucursal = listSucursal;
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

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

}
