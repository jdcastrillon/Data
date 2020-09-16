package Controlador;

import Modelo.Compras.Bancos;
import Modelo.Paises;
import Servicios.BancosService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.math.BigDecimal;
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
public class BancosControlador {

    @Inject
    private BancosService BancoService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Bancos objBancos;
    private List<Bancos> listBancos = new ArrayList();
    private List<Paises> listPais = new ArrayList();

    //Variables de combo
//    Object variables[] = new Object[4];
    //Campos Para Botonera
    Object acciones[] = new Object[6];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private String evento;
    private String valorBusqueda;

    @PostConstruct
    public void init() {
        try {
            getObjBancos();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BancosControlador() {
    }

    public void lista(int condicion) {
        listBancos.clear();
        listPais.clear();
        listBancos = BancoService.Lista();

        //Documentos
        JsonArray Jelementos = ObjIni.listObjectos("select cod_pais,nombre_pais from m_pais");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Paises obj = new Paises();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_pais(new BigDecimal(map.get("cod_pais").toString()).intValue());
                obj.setNombre_pais(map.get("nombre_pais").toString());
                listPais.add(obj);
            }
        }

    }

    public void prepareNuevo() {
        setObjBancos(null);
        getObjBancos();
        this.evento = "Nuevo";
        this.objBancos.setB_activo(true);
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
        listBancos.clear();
        this.evento = "Buscar";
        setObjBancos(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Bancos objecto, int condicion) {
        setObjBancos(null);
        Object Resulta[] = new Object[2];
        Resulta = BancoService.recuperarInfo(objecto);
        setObjBancos((Bancos) Resulta[0]);
        lista(2);
        //Condiciones
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
        setObjBancos(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = BancoService.Transaccion(objBancos, "Nuevo");
                    mns = "Bancos Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = BancoService.Transaccion(objBancos, "Borrar");
                    mns = "Bancos Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = BancoService.Transaccion(objBancos, "Editar");
                    mns = "Bancos Editado exitosamente";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("reporte");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(BancosControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = BancoService.buscarDoc(objBancos);
//                    break;//                case "Buscar":
//                    Resulta = BancoService.buscarDoc(objBancos);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listBancos.clear();
                    listBancos = (List<Bancos>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjBancos(null);
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
        this.reporte = (boolean) acciones[5];

    }

    public boolean validaciones() {
        getObjBancos();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objBancos.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listBancos.clear();
        listBancos = BancoService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listBancos.clear();
        listBancos = BancoService.Lista();
        this.valorBusqueda = "";
    }

    public List<Paises> getListPais() {
        return listPais;
    }

    public void setListPais(List<Paises> listPais) {
        this.listPais = listPais;
    }

    public BancosService getBancoService() {
        return BancoService;
    }

    public void setBancoService(BancosService BancoService) {
        this.BancoService = BancoService;
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

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public Bancos getObjBancos() {
        if (objBancos == null) {
            objBancos = new Bancos();
        }
        return objBancos;
    }

    public void setObjBancos(Bancos objBancos) {
        this.objBancos = objBancos;
    }

    public List<Bancos> getListBancos() {
        return listBancos;
    }

    public void setListBancos(List<Bancos> listBancos) {
        this.listBancos = listBancos;
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

    public boolean isReporte() {
        return reporte;
    }

    public void setReporte(boolean reporte) {
        this.reporte = reporte;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

}
