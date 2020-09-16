/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Empresa;
import Modelo.Negocio;
import Servicios.Sistema.Inicializacion;
import Servicios.NegocioService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

@ManagedBean
@ViewScoped
public class NegocioControlador implements Serializable {

    @Inject
    private NegocioService negService;

    @Inject
    private Inicializacion ObjIni;

    private Negocio objNegocio;
    List<Negocio> listNegocio = new ArrayList();
    private List<Empresa> ListaEmpresa = new ArrayList();

    //Campos Para Botonera
    Object acciones[] = new Object[5];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private String evento;

    public NegocioControlador() {
    }

    @PostConstruct
    public void init() {
        try {
            getObjNegocio();
            listaNegocios();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(EmpresaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listaNegocios() {
        listNegocio.clear();
        ListaEmpresa.clear();
        listNegocio = negService.Lista();

        //Empresa
        JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Empresa doc = new Empresa();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_emp : " + map.get("cod_emp"));
                doc.setCod_emp(map.get("cod_emp").toString());
                doc.setNom_emp(map.get("nom_emp").toString());
                ListaEmpresa.add(doc);
            }
        }
    }

    public void prepareNuevo() {
        System.out.println("Entro nuevo");
        setObjNegocio(null);
        getObjNegocio();
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
        listNegocio.clear();
        this.evento = "Buscar";
        setObjNegocio(null);
        controlEventos(evento);
    }

    public void prepareCrud(Empresa objecto, int condicion) {
        System.out.println("Entro prepareCrud Condicion : " + condicion);
        System.out.println(" + " + objecto.toString());
        setObjNegocio(null);
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
        setObjNegocio(null);
        getObjNegocio();
    }

    public void transaccion() {
        System.out.println("Inicio Transaccion : " + this.evento);
        System.out.println("- " + objNegocio.toString());
        if (validaciones()) {
            Object Resulta[] = new Object[1];
            String mns = "";
            switch (this.evento) {
                case "Nuevo":
                    objNegocio.setEstado(objNegocio.isEstadoB() ? "A" : "I");
                    Resulta = negService.Transaccion(objNegocio, "insert");
                    mns = "Negocio Creado exitosamente";
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
                listaNegocios();
                prepareNuevo();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", (String) Resulta[1]));
            }
        }
    }

    public boolean validaciones() {
        //Validaciones Sucursal
        getObjNegocio();
        String mns = "";
        if (objNegocio.getCod_negocio().isEmpty()) {
            mns = "El codigo de la sucursal no puede ser vacio";
        } else if (objNegocio.getCod_emp().isEmpty() || objNegocio.getCod_emp() == null) {
            mns = "No se ha seleccionado una empresa";
        }
        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() > 0 ? false : true;
    }

    public void controlEventos(String accion) {
        acciones = ObjIni.eventos(accion);
        this.aceptar = (boolean) acciones[0];
        this.editar = (boolean) acciones[1];
        this.eliminar = (boolean) acciones[2];
        this.nuevo = (boolean) acciones[3];
        this.buscar = (boolean) acciones[4];
    }

    public NegocioService getNegService() {
        return negService;
    }

    public void setNegService(NegocioService negService) {
        this.negService = negService;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Negocio getObjNegocio() {
        if (objNegocio == null) {
            objNegocio = new Negocio();
        }
        return objNegocio;
    }

    public void setObjNegocio(Negocio objNegocio) {
        this.objNegocio = objNegocio;
    }

    public List<Negocio> getListNegocio() {
        return listNegocio;
    }

    public void setListNegocio(List<Negocio> listNegocio) {
        this.listNegocio = listNegocio;
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

    public List<Empresa> getListaEmpresa() {
        return ListaEmpresa;
    }

    public void setListaEmpresa(List<Empresa> ListaEmpresa) {
        this.ListaEmpresa = ListaEmpresa;
    }

}
