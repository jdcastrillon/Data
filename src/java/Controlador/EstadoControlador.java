package Controlador;

import Modelo.Bodega.Categoria;
import Modelo.Bodega.Estados;
import Servicios.EstadoService;
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
public class EstadoControlador {

    @Inject
    private EstadoService estadoService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Estados objEstado;
    private List<Estados> listEstados = new ArrayList();
    private List<Categoria> listCategotias = new ArrayList();

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
            getObjEstado();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EstadoControlador() {
    }

    public void lista(int condicion) {
        listEstados.clear();
        listEstados = estadoService.Lista();

        if (condicion == 1) {
            listCategotias.clear();
            //Categorias
            JsonArray Jelementos = ObjIni.listObjectos("select cod_categoria,nom_categoria from m_categoria where modulo='bodega'");
            for (JsonElement jsonElement : Jelementos) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Categoria cate = new Categoria();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    cate.setCod_categoria(map.get("cod_categoria").toString());
                    cate.setNom_categoria(map.get("nom_categoria").toString());
                    listCategotias.add(cate);
                }
            }
        }

    }

    public void prepareNuevo() {
        setObjEstado(null);
        getObjEstado();
        listCategotias.clear();
        //Categorias
        JsonArray Jelementos = ObjIni.listObjectos("select cod_categoria,nom_categoria from m_categoria where modulo='bodega'");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Categoria cate = new Categoria();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                cate.setCod_categoria(map.get("cod_categoria").toString());
                cate.setNom_categoria(map.get("nom_categoria").toString());
                listCategotias.add(cate);
            }
        }

        this.evento = "Nuevo";
        objEstado.setActivoE(true);
        objEstado.setNegativos(true);
        objEstado.setUbicacionE(false);
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
        listEstados.clear();
        this.evento = "Buscar";
        setObjEstado(null);
        controlEventos(evento);
    }

    public void prepareCrud(Estados objecto, int condicion) {
        setObjEstado(null);
        Object Resulta[] = new Object[2];
        Resulta = estadoService.recuperarInfo(objecto);
        setObjEstado((Estados) Resulta[0]);
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
        setObjEstado(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        System.out.println("-- : " + objEstado.toString());
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = estadoService.Transaccion(objEstado, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = estadoService.Transaccion(objEstado, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = estadoService.Transaccion(objEstado, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listEstados.clear();
                    listEstados = (List<Estados>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjEstado(null);
                    this.evento = "inicio";
                    controlEventos(evento);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", (String) Resulta[1]));
            }
        }
    }

    public boolean validaciones() {
        getObjEstado();
        String mns = "";
        System.out.println("Articulo " + objEstado.toString());
        if (this.nuevo == false) {
            //Validaciones
            if (ObjVal.ValPrimaryKey("select count(*) from m_depositos where cod_deposito='" + objEstado.getCod_estado().trim() + "'")) {
                mns = "El codigo del deposito ya existe";
            } else if (objEstado.getCod_estado().isEmpty()) {
                mns = "Codigo del estado Obligatorio";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void controlEventos(String accion) {
        acciones = ObjIni.eventos(accion);
        this.aceptar = (boolean) acciones[0];
        this.editar = (boolean) acciones[1];
        this.eliminar = (boolean) acciones[2];
        this.nuevo = (boolean) acciones[3];
        this.buscar = (boolean) acciones[4];
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listEstados.clear();
        listEstados = estadoService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listEstados.clear();
        listEstados = estadoService.Lista();
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

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public EstadoService getEstadoService() {
        return estadoService;
    }

    public void setEstadoService(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    public Estados getObjEstado() {
        if (objEstado == null) {
            objEstado = new Estados();
        }

        return objEstado;
    }

    public void setObjEstado(Estados objEstado) {
        this.objEstado = objEstado;
    }

    public List<Estados> getListEstados() {
        return listEstados;
    }

    public void setListEstados(List<Estados> listEstados) {
        this.listEstados = listEstados;
    }

    public List<Categoria> getListCategotias() {
        return listCategotias;
    }

    public void setListCategotias(List<Categoria> listCategotias) {
        this.listCategotias = listCategotias;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

}
