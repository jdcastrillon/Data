/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Ciudades;
import Modelo.Empresa;
import Modelo.Sucursal;
import Modelo.Usuarios;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.SucursalService;
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
public class SucursalesControlador implements Serializable {

    @Inject
    private SucursalService sucService;

    @Inject
    private Seleccion SelService;

    @Inject
    private Inicializacion ObjIni;

    Gson gson = new Gson();
    private Sucursal objSucursal;
    private Ciudades objCiudad;
    private List<Sucursal> listSuc = new ArrayList();
    private List<Usuarios> listUser = new ArrayList();
    private List<Empresa> ListaEmpresa = new ArrayList();

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
            getObjSucursal();
            getObjCiudad();
            listaSucursal();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(EmpresaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SucursalesControlador() {
    }

    public void listaSucursal() {
        listSuc.clear();
        ListaEmpresa.clear();
        listSuc = sucService.Lista();

        //Documentos
        JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Empresa doc = new Empresa();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                doc.setCod_emp(map.get("cod_emp").toString());
                doc.setNom_emp(map.get("nom_emp").toString());
                ListaEmpresa.add(doc);
            }
        }

        //Usuarios
        JsonArray Jelementos2 = ObjIni.listObjectos("select A.usuario,B.nombrecompleto from m_usuarios A , m_persona B\n"
                + "where A.cod_persona=B.cod_persona and A.activo='A'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Usuarios user = new Usuarios();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                user.setUsuario(map.get("usuario").toString());
                user.setNombre_completo(map.get("nombrecompleto").toString());
                listUser.add(user);
            }
        }
    }

    public void prepareNuevo() {
        System.out.println("Entro nuevo");
        setObjSucursal(null);
        setObjCiudad(null);
        getObjSucursal();
        getObjCiudad();
        this.evento = "Nuevo";
        controlEventos(evento);
    }

    public void prepareEditar() {
        System.out.println("Entro Editar");
        this.evento = "Editar";
        controlEventos(evento);
    }

    public void prepareEliminar() {
        System.out.println("Entro Editar");
        this.evento = "Eliminar";
        controlEventos(evento);
    }

    public void prepareBuscar() {
        System.out.println("Entro Buscar");
        listSuc.clear();
        this.evento = "Buscar";
        setObjSucursal(null);
        controlEventos(evento);
    }

    public void prepareCrud(Empresa objecto, int condicion) {
        System.out.println("Entro prepareCrud Condicion : " + condicion);
        System.out.println(" + " + objecto.toString());
        setObjSucursal(null);
//        Object Resulta[] = new Object[2];
//        Resulta = sucService.recuperarInfo(objecto);
//        setObjEmpresa((Empresa) Resulta[0]);
        switch (condicion) {
            case 1:
                this.evento = "Editar";
                break;
            case 2:
                this.evento = "Eliminar";
                break;
            case 3:
                this.evento = "visualizar";
                break;
            default:
                this.evento = "visualizar";
                break;
        }

        controlEventos(evento);
    }

    public void cancelarEventos() {
        this.evento = "";
        controlEventos(evento);
        setObjSucursal(null);
        getObjSucursal();
    }

    public void transaccion() {
        System.out.println("Inicio Transaccion : " + this.evento);
        System.out.println("- " + objSucursal.toString());
        if (validaciones()) {
            System.out.println("- " + objCiudad.toString());
            objSucursal.setCod_ciudad(objCiudad.getCod_ciudad());
            Object Resulta[] = new Object[1];
            String mns = "";
            switch (this.evento) {
                case "Nuevo":
                    objSucursal.setEstado(objSucursal.isEstadoB() ? "A" : "I");
                    Resulta = sucService.Transaccion(objSucursal, "insert");
                    mns = "Empresa Creado exitosamente";
                    break;
//            case "Eliminar":
//                Resulta = sucService.borrarDoc(objEmpresa);
//                mns = "Documento Eliminado exitosamente";
//                break;
//            case "Editar":
//                Resulta = sucService.editarDoc(objEmpresa);
//                mns = "Documento Editado exitosamente";
//                break;
            }
            if (Resulta[0].equals("OK")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                listaSucursal();
                prepareNuevo();
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

    public List<Ciudades> ciudades(String query) {
        System.out.println("------------ Bean ciudades------------");
        List<Ciudades> listCiudades = new ArrayList();
        listCiudades = SelService.cargaCiudad(query, 2);
        return listCiudades;
    }

    public void eliminarUser(Usuarios user) {
        objSucursal.getListUser().remove(user);
    }

    public boolean validaciones() {
        //Validaciones Sucursal
        getObjSucursal();
        String mns = "";
        if (objSucursal.getCod_sucursal().isEmpty()) {
            mns = "El codigo de la sucursal no puede ser vacio";
        } else if (objSucursal.getCod_emp().isEmpty() || objSucursal.getCod_emp() == null) {
            mns = "No se ha seleccionado una empresa";
        }
        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() > 0 ? false : true;
    }

    public Sucursal getObjSucursal() {
        if (objSucursal == null) {
            objSucursal = new Sucursal();
        }
        return objSucursal;
    }

    public void setObjSucursal(Sucursal objSucursal) {
        this.objSucursal = objSucursal;
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

    public List<Sucursal> getListSuc() {
        return listSuc;
    }

    public void setListSuc(List<Sucursal> listSuc) {
        this.listSuc = listSuc;
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

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public SucursalService getSucService() {
        return sucService;
    }

    public void setSucService(SucursalService sucService) {
        this.sucService = sucService;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public List<Empresa> getListaEmpresa() {
        return ListaEmpresa;
    }

    public void setListaEmpresa(List<Empresa> ListaEmpresa) {
        this.ListaEmpresa = ListaEmpresa;
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

    public List<Usuarios> getListUser() {
        return listUser;
    }

    public void setListUser(List<Usuarios> listUser) {
        this.listUser = listUser;
    }

}
