package Controlador;

import Modelo.Bodega.CalculoStock;
import Modelo.Ciudades;
import Modelo.Bodega.CalculoStock;
import Modelo.Bodega.CalculoStockDT;
import Modelo.Empresa;
import Modelo.Bodega.Estados;
import Modelo.Sucursal;
import Servicios.CalculoService;
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
public class CalculoStockControlador {
    
    @Inject
    private CalculoService CalcService;
    
    @Inject
    private Inicializacion ObjIni;
    
    @Inject
    private Validaciones ObjVal;
    
    @Inject
    private Seleccion SelService;
    
    private CalculoStock objCalculo;
    private List<CalculoStock> listCalculo = new ArrayList();
    
    private Estados objEstado;

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
            getObjCalculo();
            lista();
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public CalculoStockControlador() {
    }
    
    public void lista() {
        listCalculo.clear();
        listCalculo = CalcService.Lista();
    }
    
    public void prepareNuevo() {
        setObjCalculo(null);
        getObjCalculo();
        this.evento = "Nuevo";
        objCalculo.setActivoC(false);
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
        listCalculo.clear();
        this.evento = "Buscar";
        setObjCalculo(null);
        controlEventos(evento);
    }
    
    public void prepareCrud(CalculoStock objecto, int condicion) {
        setObjCalculo(null);
        Object Resulta[] = new Object[2];
        Resulta = CalcService.recuperarInfo(objecto);
        setObjCalculo((CalculoStock) Resulta[0]);
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
            lista();
        }
        this.evento = "inicio";
        controlEventos(evento);
        setObjCalculo(null);
    }
    
    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = CalcService.Transaccion(objCalculo, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = CalcService.Transaccion(objCalculo, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = CalcService.Transaccion(objCalculo, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
//                case "Buscar":
//                    Resulta = CalcService.buscarDoc(objCalculo);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listCalculo.clear();
                    listCalculo = (List<CalculoStock>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista();
                    prepareNuevo();
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
        getObjCalculo();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);
        
        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objCalculo.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }
        
        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }
    
    public List<Estados> estados(String query) {
        System.out.println("------------ Bean ciudades------------");
        List<Estados> listEstados = new ArrayList();
        listEstados = SelService.selEstaddos(query, 2);
        return listEstados;
    }
    
    public void cargaEstado() {
        System.out.println("Carga Estado : " + objEstado.toString());
        objCalculo.getList_estados().add(new CalculoStockDT(objEstado.getCod_estado()));
        setObjEstado(null);
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
    
    public CalculoService getCalcService() {
        return CalcService;
    }
    
    public void setCalcService(CalculoService CalcService) {
        this.CalcService = CalcService;
    }
    
    public CalculoStock getObjCalculo() {
        if (objCalculo == null) {
            objCalculo = new CalculoStock();
        }
        return objCalculo;
    }
    
    public void setObjCalculo(CalculoStock objCalculo) {
        this.objCalculo = objCalculo;
    }
    
    public List<CalculoStock> getListCalculo() {
        return listCalculo;
    }
    
    public void setListCalculo(List<CalculoStock> listCalculo) {
        this.listCalculo = listCalculo;
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
    
}
