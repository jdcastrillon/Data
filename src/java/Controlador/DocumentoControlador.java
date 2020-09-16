package Controlador;

import Modelo.Documentos;
import Servicios.DocumentoService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Validaciones;
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

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class DocumentoControlador {

    @Inject
    private DocumentoService docService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    private Documentos objDoc;
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
            getObjDoc();
            listaDocumentos();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DocumentoControlador() {
    }

    public void listaDocumentos() {
        listDoc.clear();
        listDoc = docService.listdoc();
    }

    public void prepareNuevoDocumento() {
        setObjDoc(null);
        getObjDoc();
        this.evento = "Nuevo";
        controlEventos(evento);
    }

    public void prepareEditDocumento() {
        this.evento = "Editar";
        controlEventos(evento);
    }

    public void prepareEliminarDoc() {
        this.evento = "Eliminar";
        controlEventos(evento);
    }

    public void prepareBuscarDoc() {
        listDoc.clear();
        this.evento = "Buscar";
        setObjDoc(null);
        controlEventos(evento);
    }

    public void prepareCrud(Documentos objecto, int condicion) {
        setObjDoc(null);
        Object Resulta[] = new Object[2];
        Resulta = docService.recuperarInfo(objecto);
        setObjDoc((Documentos) Resulta[0]);
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
            listaDocumentos();
        }
        this.evento = "inicio";
        controlEventos(evento);
        setObjDoc(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = docService.Transaccion(objDoc, "Nuevo");
                    mns = "Documento Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = docService.Transaccion(objDoc, "Borrar");
                    mns = "Documento Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = docService.Transaccion(objDoc, "Editar");
                    mns = "Documento Editado exitosamente";
                    break;
                case "Buscar":
                    Resulta = docService.buscarDoc(objDoc);
                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listDoc.clear();
                    listDoc = (List<Documentos>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    listaDocumentos();
                    prepareNuevoDocumento();
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
        getObjDoc();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objDoc.getCod_tipodoc() + "'")) {
                mns = "El codigo del documento ya existe";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public DocumentoService getDocService() {
        return docService;
    }

    public void setDocService(DocumentoService docService) {
        this.docService = docService;
    }

    public Documentos getObjDoc() {
        if (objDoc == null) {
            objDoc = new Documentos();
        }
        return objDoc;
    }

    public void setObjDoc(Documentos objDoc) {
        this.objDoc = objDoc;
    }

    public List<Documentos> getListDoc() {
        return listDoc;
    }

    public void setListDoc(List<Documentos> listDoc) {
        this.listDoc = listDoc;
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
    
    

}
