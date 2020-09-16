/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Correos;
import Modelo.Documentos;
import Modelo.Personas;
import Modelo.Telefonos;
import Modelo.Usuarios;
import Servicios.Sistema.Inicializacion;
import Servicios.UserXPersonaService;
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
public class PersonaControlador implements Serializable {

    @Inject
    private UserXPersonaService userXPersonaService;

    @Inject
    private Inicializacion ObjIni;

    private Usuarios objUser;
    private Personas objPersona;
    private Telefonos objTelefono;
    private Correos objCorreos;

    private List<Usuarios> listUser = new ArrayList();
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
            getObjUser();
            getObjTelefono();
            getObjPersona();
            getObjCorreos();

            listaUser();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PersonaControlador() {
    }

    public void listaUser() {
        listUser.clear();
        listDoc.clear();
        listUser = userXPersonaService.Lista();

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
    }

    public void prepareNuevo() {
        eventos(1);
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
        listDoc.clear();
        this.evento = "Buscar";
        eventos(1);
        controlEventos(evento);
    }

    public void prepareCrud(Usuarios objecto, int condicion) {
        System.out.println("Entro prepareCrud Condicion : " + condicion);
        eventos(1);
        setObjUser(objecto);
        Object Resulta[] = new Object[2];
        Resulta = userXPersonaService.recuperarInfo(objecto, objPersona);
        setObjPersona((Personas) Resulta[0]);
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
            listaUser();
        }
        this.evento = "inicio";
        controlEventos(evento);
        eventos(2);
    }

    public void eventos(int cond) {
        if (cond == 1 || cond == 2) {
            setObjUser(null);
            setObjPersona(null);
            setObjCorreos(null);
            setObjTelefono(null);
        }

        if (cond == 1) {
            getObjUser();
            getObjPersona();
            getObjCorreos();
            getObjTelefono();
        }
    }

    public void transaccion() {
        System.out.println("Inicio Transaccion : " + this.evento);
        Object Resulta[] = new Object[1];
        String mns = "";
        switch (this.evento) {
            case "Nuevo":
                Resulta = userXPersonaService.Transaccion(objUser, objPersona, "Nuevo");
                mns = "Usuario Creado exitosamente";
                break;
            case "Eliminar":
                Resulta = userXPersonaService.Transaccion(objUser, objPersona, "Borrar");
                mns = "Documento Eliminado exitosamente";
                break;
            case "Editar":
                Resulta = userXPersonaService.Transaccion(objUser, objPersona, "Editar");
                mns = "Usuario Editado exitosamente";
                break;
        }
        System.out.println("Paso switch");
        if (Resulta[0].equals("OK")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
            listaUser();
            prepareNuevo();
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

    //************************************************ESTANDAR
    public void agregarTelefono() {
        System.out.println("Entro agregar telefono");
        System.out.println("- " + objTelefono.toString());
        objPersona.getListTele().add(objTelefono);
        setObjTelefono(null);
        getObjTelefono();
    }

    public void agregarCorreo() {
        System.out.println("Entro agregar Correo");
        System.out.println("- " + objCorreos.toString());
        objPersona.getListCorreos().add(objCorreos);
        setObjCorreos(null);
        getObjCorreos();
    }

    public void quitarTelefono(Telefonos obj) {
        System.out.println("Entro quitar Tel : " + obj.toString());
        objPersona.getListTele().remove(obj);
    }

    public void quitarCorreo(Correos obj) {
        System.out.println("Entro quitar Correos : " + obj.toString());
        objPersona.getListCorreos().remove(obj);
    }

    public UserXPersonaService getUserXPersonaService() {
        if (userXPersonaService == null) {
            userXPersonaService.getEstado();
        }
        return userXPersonaService;
    }

    public void setUserXPersonaService(UserXPersonaService userXPersonaService) {
        this.userXPersonaService = userXPersonaService;
    }

    public Usuarios getObjUser() {
        if (objUser == null) {
            objUser = new Usuarios();
        }
        return objUser;
    }

    public void setObjUser(Usuarios objUser) {
        this.objUser = objUser;
    }

    public List<Usuarios> getListUser() {
        return listUser;
    }

    public void setListUser(List<Usuarios> listUser) {
        this.listUser = listUser;
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

    public Personas getObjPersona() {
        if (objPersona == null) {
            objPersona = new Personas();
        }
        return objPersona;
    }

    public void setObjPersona(Personas objPersona) {
        this.objPersona = objPersona;
    }

    public List<Documentos> getListDoc() {
        return listDoc;
    }

    public void setListDoc(List<Documentos> listDoc) {
        this.listDoc = listDoc;
    }

    public Telefonos getObjTelefono() {
        if (objTelefono == null) {
            objTelefono = new Telefonos();
        }
        return objTelefono;
    }

    public void setObjTelefono(Telefonos objTelefono) {
        this.objTelefono = objTelefono;
    }

    public Correos getObjCorreos() {
        if (objCorreos == null) {
            objCorreos = new Correos();
        }
        return objCorreos;
    }

    public void setObjCorreos(Correos objCorreos) {
        this.objCorreos = objCorreos;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
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
