/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Ciudades;
import Modelo.Documentos;
import Modelo.Empresa;
import Servicios.EmpresaServicios;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
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
public class EmpresaControlador implements Serializable {

    @Inject
    private EmpresaServicios empService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Seleccion ObjSel;

    private Empresa objEmpresa;
    private Ciudades objCiudad;
    Gson gson = new Gson();
    private List<Empresa> ListaEmpresa = new ArrayList();
    private List<Documentos> listDoc = new ArrayList();

    //Campos Para Botonera
    Object acciones[] = new Object[5];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private String evento;

    @PostConstruct
    public void init() {
        try {
            getObjEmpresa();
            getObjCiudad();
            listaEmpresas();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(EmpresaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EmpresaControlador() {
    }

    public void listaEmpresas() {
        ListaEmpresa.clear();
        listDoc.clear();
        ListaEmpresa = empService.Lista();

        //Documentos
        JsonArray Jelementos = ObjIni.listObjectos("select cod_tipodoc,nombredocumento from m_tipodocumentos");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Documentos doc = new Documentos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                doc.setCod_tipodoc(map.get("cod_tipodoc").toString());
                doc.setNombredocumento(map.get("nombredocumento").toString());
                listDoc.add(doc);
            }
        }
    }

    public void prepareNuevoEmpresa() {
        setObjEmpresa(null);
        setObjCiudad(null);
        getObjEmpresa();
        getObjCiudad();
        this.evento = "Nuevo";
        controlEventos(evento);
    }

    public void prepareEditEmpresa() {
        this.evento = "Editar";
        controlEventos(evento);
    }

    public void prepareEliminarEmpresa() {
        this.evento = "Eliminar";
        controlEventos(evento);
    }

    public void prepareBuscarEmpresa() {
        listDoc.clear();
        this.evento = "Buscar";
        setObjEmpresa(null);
        setObjCiudad(null);
        controlEventos(evento);
    }

    public void prepareCrud(Empresa objecto, int condicion) {
        System.out.println("Entro prepareCrud Condicion : " + condicion);
        System.out.println(" + " + objecto.toString());
        setObjEmpresa(null);
        Object Resulta[] = new Object[2];
        Resulta = empService.recuperarInfo(objecto);
        setObjEmpresa((Empresa) Resulta[0]);
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
            listaEmpresas();
        }
        this.evento = "inicio";
        controlEventos(evento);
        setObjEmpresa(null);
        setObjCiudad(null);
    }

    public void transaccion() {
        System.out.println("Inicio Transaccion : " + this.evento);
        System.out.println("- " + objEmpresa.toString());
        System.out.println("- " + objCiudad.toString());
        objEmpresa.setCod_ciudad(objCiudad.getCod_ciudad());
        Object Resulta[] = new Object[1];
        String mns = "";
        switch (this.evento) {
            case "Nuevo":
                Resulta = empService.Transaccion(objEmpresa, "Nuevo");
                mns = "Empresa Creado exitosamente";
                break;
            case "Eliminar":
                Resulta = empService.Transaccion(objEmpresa, "Borrar");
                mns = "Documento Eliminado exitosamente";
                break;
            case "Editar":
                Resulta = empService.Transaccion(objEmpresa, "Editar");
                mns = "Documento Editado exitosamente";
                break;
        }
        if (Resulta[0].equals("OK")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
            listaEmpresas();
            prepareNuevoEmpresa();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", (String) Resulta[1]));
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

    public List<Ciudades> ciudades(String query) {
        System.out.println("------------ Bean ciudades------------");
        List<Ciudades> listCiudades = new ArrayList();
        listCiudades = ObjSel.cargaCiudad(query, 2);
        return listCiudades;
    }

    public EmpresaServicios getEmpService() {
        return empService;
    }

    public void setEmpService(EmpresaServicios empService) {
        this.empService = empService;
    }

    public Empresa getObjEmpresa() {
        if (objEmpresa == null) {
            objEmpresa = new Empresa();
        }
        return objEmpresa;
    }

    public void setObjEmpresa(Empresa objEmpresa) {
        this.objEmpresa = objEmpresa;
    }

    public List<Empresa> getListaEmpresa() {
        return ListaEmpresa;
    }

    public void setListaEmpresa(List<Empresa> ListaEmpresa) {
        this.ListaEmpresa = ListaEmpresa;
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

    public List<Documentos> getListDoc() {
        return listDoc;
    }

    public void setListDoc(List<Documentos> listDoc) {
        this.listDoc = listDoc;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public Ciudades getObjCiudad() {
        System.out.println("Instancio ciudad");
        if (objCiudad == null) {
            objCiudad = new Ciudades();
        }
        return objCiudad;
    }

    public void setObjCiudad(Ciudades objCiudad) {
        System.out.println("agrego ciudad : " + objCiudad);
        this.objCiudad = objCiudad;
    }

    public Seleccion getObjSel() {
        return ObjSel;
    }

    public void setObjSel(Seleccion ObjSel) {
        this.ObjSel = ObjSel;
    }

    public Object[] getAcciones() {
        return acciones;
    }

    public void setAcciones(Object[] acciones) {
        this.acciones = acciones;
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

}
