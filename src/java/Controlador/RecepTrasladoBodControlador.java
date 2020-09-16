package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Servicios.Sistema.*;
import Servicios.RecepcionTrasladoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
public class RecepTrasladoBodControlador {

    @Inject
    private RecepcionTrasladoService recepTraslado;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private RecepcionTraslado objRecepTraslado;
    private TrasladoBodega objTraslado;
    private List<RecepcionTraslado> List_RecepTraslado = new ArrayList();
    private List<TrasladoBodega> listTranslado = new ArrayList();

//    private List<Empresa> listEmpresas = new ArrayList();
//    private List<Estados> listEstados = new ArrayList();
//    private List<Depositos> listDepositos = new ArrayList();
//    private List<Articulos> listArticulos = new ArrayList();
//    private List<Ubicaciones> listUbicaciones = new ArrayList();
//
//    private RecepcionTrasladoDT objRecepTrasladoDT;
//    private Articulos objArticulo;
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
            getObjRecepTraslado();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RecepTrasladoBodControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        List_RecepTraslado.clear();
        List_RecepTraslado = recepTraslado.Lista();

    }

    public void prepareNuevo() {
        setObjRecepTraslado(null);
        getObjRecepTraslado();

        //Numerador
        objRecepTraslado.setNro_docum(ObjIni.numerador("stockajuste"));

        //Recuperamos Traslados Pendientes.
        listTranslado.clear();
        JsonArray Jelementos = ObjIni.listObjectos("select A.cod_emp,fec_doc,nro_docum,trans,A.cod_deposito,cod_deposito2,A.cod_estado,cod_estado2,\n"
                + "D1.nom_deposito,D2.nom_deposito as nom_deposito2 ,E1.nom_estado,E2.nom_estado as nom_estado2 from t_trasladobodega A\n"
                + "inner join m_depositos D1  on A.cod_deposito=D1.cod_deposito\n"
                + "inner join m_depositos D2  on A.cod_deposito2=D2.cod_deposito\n"
                + "inner join m_estados E1  on A.cod_estado=E1.cod_estado\n"
                + "inner join m_estados E2  on A.cod_estado2=E2.cod_estado\n"
                + "where nro_docum not in (select nro_doca from t_receptraslado where nro_doca=A.nro_docum)\n"
                + "order by 2");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                TrasladoBodega traslado = new TrasladoBodega();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                traslado.setCod_emp(map.get("cod_emp").toString());
                traslado.setFec_doc(map.get("fec_doc").toString());
                traslado.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                traslado.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                traslado.setCod_deposito(map.get("cod_deposito").toString());
                traslado.setCod_deposito2(map.get("cod_deposito2").toString());
                traslado.setCod_estado(map.get("cod_estado").toString());
                traslado.setCod_estado2(map.get("cod_estado2").toString());
                traslado.setNom_deposito(map.get("nom_deposito").toString());
                traslado.setNom_deposito2(map.get("nom_deposito2").toString());
                traslado.setNom_estado(map.get("nom_estado").toString());
                traslado.setNom_estado2(map.get("nom_estado2").toString());

                listTranslado.add(traslado);
            }
        }

        this.evento = "Nuevo";
        objRecepTraslado.setFecha(new Date());
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
        List_RecepTraslado.clear();
        this.evento = "Buscar";
        setObjRecepTraslado(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(RecepcionTraslado objecto, int condicion) {
        setObjRecepTraslado(null);
        Object Resulta[] = new Object[2];
        Resulta = recepTraslado.recuperarInfo(objecto);
        setObjRecepTraslado((RecepcionTraslado) Resulta[0]);
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
        setObjRecepTraslado(null);
    }

    public void transaccion() {

        Object Resulta[] = new Object[2];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = recepTraslado.Transaccion(objRecepTraslado, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = recepTraslado.Transaccion(objRecepTraslado, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = recepTraslado.Transaccion(objRecepTraslado, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("Blank_A4_1");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(RecepTrasladoBodControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = recepTraslado.buscarDoc(objRecepTraslado);
//                    break;//                case "Buscar":
//                    Resulta = recepTraslado.buscarDoc(objRecepTraslado);
//                    break;//                case "Buscar":
//                    Resulta = recepTraslado.buscarDoc(objRecepTraslado);
//                    break;//                case "Buscar":
//                    Resulta = recepTraslado.buscarDoc(objRecepTraslado);
//                    break;

            }
            if (Resulta[0] == null) {
                Resulta[0] = "";
            }
            if (Resulta[0].equals("OK")) {
                if (Resulta[1] == null) {
                    Resulta[1] = "";
                }
                if (evento.equalsIgnoreCase("Buscar")) {
                    List_RecepTraslado.clear();
                    List_RecepTraslado = (List<RecepcionTraslado>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjRecepTraslado(null);
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
        getObjRecepTraslado();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objRecepTraslado.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void eliminarArticuloGrilla(TrasladoBodegaDT obj) {
        if (buscarElemento(obj)) {
            objRecepTraslado.getDetalleArt().remove(obj);
        }

    }

    public boolean buscarElemento(TrasladoBodegaDT obj) {
        boolean valor = false;
        for (TrasladoBodegaDT obj2 : objRecepTraslado.getDetalleArt()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        List_RecepTraslado.clear();
        List_RecepTraslado = recepTraslado.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        List_RecepTraslado.clear();
        List_RecepTraslado = recepTraslado.Lista();
        this.valorBusqueda = "";
    }

    public void cargarNroTraslado() {
        getObjTraslado();
        System.out.println("Orden de compra : " + objTraslado.toString());
        objRecepTraslado.setCod_emp(objTraslado.getCod_emp());
        objRecepTraslado.setCod_deposito(objTraslado.getCod_deposito());
        objRecepTraslado.setCod_deposito2(objTraslado.getCod_deposito2());
        objRecepTraslado.setCod_estado(objTraslado.getCod_estado());
        objRecepTraslado.setCod_estado2(objTraslado.getCod_estado2());
        objRecepTraslado.setNom_deposito(objTraslado.getNom_deposito());
        objRecepTraslado.setNom_deposito2(objTraslado.getNom_deposito2());
        objRecepTraslado.setNom_estado(objTraslado.getNom_estado());
        objRecepTraslado.setNom_estado2(objTraslado.getNom_estado2());
        objRecepTraslado.setNro_doca(objTraslado.getNro_docum());
        objRecepTraslado.setNro_docum(objTraslado.getNro_docum());
        objRecepTraslado.getDetalleArt().clear();
        //Articulos
        JsonArray Jelementos3 = ObjIni.listObjectos("select B.cod_Articulo,B.codigo,B.nom_articulo,A.cantidad,A.linea from td_trasladobodega A inner join m_Articulos B on A.cod_articulo=B.cod_Articulo\n"
                + "and A.Trans=" + objTraslado.getTrans() + " order by A.linea");
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                TrasladoBodegaDT obj = new TrasladoBodegaDT();

                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                obj.setStock(0);
                obj.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                obj.setLinea(new BigDecimal(map.get("linea").toString()).intValue());
                obj.setCodigo(map.get("codigo").toString());
                obj.setNom_articulo(map.get("nom_articulo").toString());

                objRecepTraslado.getDetalleArt().add(obj);
            }
        }
    }

    public RecepcionTrasladoService getRecepTraslado() {
        return recepTraslado;
    }

    public void setRecepTraslado(RecepcionTrasladoService recepTraslado) {
        this.recepTraslado = recepTraslado;
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

    public RecepcionTraslado getObjRecepTraslado() {
        if (objRecepTraslado == null) {
            objRecepTraslado = new RecepcionTraslado();
        }
        return objRecepTraslado;
    }

    public void setObjRecepTraslado(RecepcionTraslado objRecepTraslado) {
        this.objRecepTraslado = objRecepTraslado;
    }

    public List<RecepcionTraslado> getList_RecepTraslado() {
        return List_RecepTraslado;
    }

    public void setList_RecepTraslado(List<RecepcionTraslado> List_RecepTraslado) {
        this.List_RecepTraslado = List_RecepTraslado;
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

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

    public List<TrasladoBodega> getListTranslado() {
        return listTranslado;
    }

    public void setListTranslado(List<TrasladoBodega> listTranslado) {
        this.listTranslado = listTranslado;
    }

    public TrasladoBodega getObjTraslado() {
        if (objTraslado == null) {
            objTraslado = new TrasladoBodega();
        }
        return objTraslado;
    }

    public void setObjTraslado(TrasladoBodega objTraslado) {
        this.objTraslado = objTraslado;
    }

}
