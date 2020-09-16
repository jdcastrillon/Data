/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Bodega.Categoria;
import Modelo.SubCategoria;
import Servicios.CategoriaService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Validaciones;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class CategoriaControlador implements Serializable {

    @Inject
    private CategoriaService CategoriaService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    private Categoria objCategoria;
    private SubCategoria objSubCategoria;

    List<Categoria> listCategoria = new ArrayList();

    //Campos Para Botonera
    Object acciones[] = new Object[5];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private String evento;
    private String valorBusqueda;

    public CategoriaControlador() {
    }

    @PostConstruct
    public void init() {
        try {
            getObjCategoria();
            getObjSubCategoria();
            lista();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(EmpresaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void lista() {
        listCategoria.clear();
        listCategoria = CategoriaService.Lista();

    }

    public void prepareNuevo() {
        setObjCategoria(null);
        getObjCategoria();
        this.evento = "Nuevo";
        controlEventos(evento);
    }

    public void prepareEditar() {
        this.evento = "Editar";
        controlEventos(evento);
    }

    public void prepareEditarmodal(SubCategoria obj) {
        System.out.println("Entro prepareEditarmodal");
        setObjSubCategoria(null);
        setObjSubCategoria(obj);

        objCategoria.getListSubCategoria().remove(obj);

        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('SubCate').show();");
    }

    public void prepareEliminar() {
        this.evento = "Eliminar";
        controlEventos(evento);
    }

    public void prepareBuscar() {
        listCategoria.clear();
        this.evento = "Buscar";
        setObjCategoria(null);
        controlEventos(evento);
    }

    public void controlEventos(String accion) {
        acciones = ObjIni.eventos(accion);
        this.aceptar = (boolean) acciones[0];
        this.editar = (boolean) acciones[1];
        this.eliminar = (boolean) acciones[2];
        this.nuevo = (boolean) acciones[3];
        this.buscar = (boolean) acciones[4];
    }

    public void prepareCrud(Categoria objecto, int condicion) {
        System.out.println("Entro prepareCrud Condicion : " + condicion);
        System.out.println(" + " + objecto.toString());
        setObjCategoria(null);
        Object Resulta[] = new Object[2];
        Resulta = CategoriaService.recuperarInfo(objecto);
        setObjCategoria((Categoria) Resulta[0]);
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
        this.evento = "inicio";
        controlEventos(evento);
        setObjCategoria(null);
        getObjCategoria();
        setObjSubCategoria(null);
        getObjSubCategoria();
    }

    public void transaccion() {
        System.out.println("Inicio Transaccion : " + this.evento);
        System.out.println("- " + objCategoria.toString());
        if (validaciones()) {
            Object Resulta[] = new Object[1];
            String mns = "";
            switch (this.evento) {
                case "Nuevo":
                    Resulta = CategoriaService.Transaccion(objCategoria, "Nuevo");
                    mns = "Categoria Creada exitosamente";
                    break;
                case "Eliminar":
                    Resulta = CategoriaService.Transaccion(objCategoria, "Borrar");
                    mns = "Categoria Eliminada exitosamente";
                    break;
                case "Editar":
                    Resulta = CategoriaService.Transaccion(objCategoria, "Editar");
                    mns = "Categoria Editada exitosamente";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                lista();
                prepareNuevo();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", (String) Resulta[1]));
            }
        }
    }

    public boolean validaciones() {
        getObjCategoria();
        String mns = "";
        System.out.println("Articulo " + objCategoria.toString());
        if (this.nuevo == false) {
            //Validaciones
            if (ObjVal.ValPrimaryKey("select count(*) from m_categoria where cod_categoria='" + objCategoria.getCod_categoria().trim() + "'")) {
                mns = "El codigo de la Categotia ya existe";
            } else if (objCategoria.getCod_categoria().isEmpty()) {
                mns = "Codigo de la Categotia Obligatorio";
            } else if (objCategoria.getNom_categoria().isEmpty()) {
                mns = "Nombre de la Categotia Obligatorio";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }
    
    public void validacionmns(){
        
    }

    public void agregarSubCategoria() {
        System.out.println("- " + objSubCategoria.toString());
        objCategoria.getListSubCategoria().add(0,objSubCategoria);
        setObjSubCategoria(null);
        getObjSubCategoria();
    }

    public void quitarSubCategoria(SubCategoria obj) {
        System.out.println("- " + obj.toString());
        objCategoria.getListSubCategoria().remove(obj);
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listCategoria.clear();
        listCategoria = CategoriaService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listCategoria.clear();
        listCategoria = CategoriaService.Lista();
        this.valorBusqueda = "";
    }

    public CategoriaService getCategoriaService() {
        return CategoriaService;
    }

    public void setCategoriaService(CategoriaService CategoriaService) {
        this.CategoriaService = CategoriaService;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Categoria getObjCategoria() {
        if (objCategoria == null) {
            objCategoria = new Categoria();
        }
        return objCategoria;
    }

    public void setObjCategoria(Categoria objCategoria) {
        this.objCategoria = objCategoria;
    }

    public List<Categoria> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<Categoria> listCategoria) {
        this.listCategoria = listCategoria;
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

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public SubCategoria getObjSubCategoria() {
        if (objSubCategoria == null) {
            objSubCategoria = new SubCategoria();
        }
        return objSubCategoria;
    }

    public void setObjSubCategoria(SubCategoria objSubCategoria) {
        this.objSubCategoria = objSubCategoria;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

    public Validaciones getObjVal() {
        return ObjVal;
    }

    public void setObjVal(Validaciones ObjVal) {
        this.ObjVal = ObjVal;
    }

}
