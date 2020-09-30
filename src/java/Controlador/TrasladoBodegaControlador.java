package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Servicios.Sistema.*;
import Servicios.TrasladoBodegaService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JsonDataSource;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class TrasladoBodegaControlador {

    @Inject
    private TrasladoBodegaService TrasladoService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private TrasladoBodega objTraldoBodega;
    private List<TrasladoBodega> listTranslado = new ArrayList();

    private List<Empresa> listEmpresas = new ArrayList();
    private List<Estados> listEstados = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Ubicaciones> listUbicaciones = new ArrayList();

    private TrasladoBodegaDT objTraldoBodegaDT;
    private Articulos objArticulo;

    Object acciones[] = new Object[6];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private String evento;
    private String valorBusqueda;
    private boolean executeReport = false;

    @PostConstruct
    public void init() {
        try {
            getObjTraldoBodega();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TrasladoBodegaControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listTranslado.clear();
        listTranslado = TrasladoService.Lista();

        if (condicion == 1) {
            listEmpresas.clear();
            listDepositos.clear();
            listEstados.clear();

            //Empresas
            listEmpresas.clear();
            JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
            for (JsonElement jsonElement : Jelementos) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Empresa emp = new Empresa();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    emp.setCod_emp(map.get("cod_emp").toString());
                    emp.setNom_emp(map.get("nom_emp").toString());
                    listEmpresas.add(emp);
                }
            }

            //Depositos
            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
            for (JsonElement jsonElement : Jelementos2) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Depositos obj = new Depositos();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setNom_deposito(map.get("nom_deposito").toString());
                    listDepositos.add(obj);
                }
            }
        }
    }

    public void prepareNuevo() {
        setObjTraldoBodega(null);
        getObjTraldoBodega();
        listEmpresas.clear();
        listDepositos.clear();
        listEstados.clear();
        listArticulos.clear();

        //Numerador
        objTraldoBodega.setNro_docum(ObjIni.numerador("stockajuste"));

        //Empresas
        JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Empresa emp = new Empresa();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                emp.setCod_emp(map.get("cod_emp").toString());
                emp.setNom_emp(map.get("nom_emp").toString());
                listEmpresas.add(emp);
            }
        }

        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos obj = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setNom_deposito(map.get("nom_deposito").toString());
                listDepositos.add(obj);
            }
        }

        //Estados
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_estado,nom_estado from m_estados where cod_categoria='Stock' and cod_deposito='Deposito' and activo='S'");
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Estados obj = new Estados();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_estado(map.get("cod_estado").toString());
                obj.setNom_estado(map.get("nom_estado").toString());
                listEstados.add(obj);
            }
        }

        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        System.out.println("Size Depositod : " + listDepositos.size());
        objTraldoBodega.setCod_emp(listEmpresas.get(0).getCod_emp());
        objTraldoBodega.setCod_deposito(listDepositos.get(0).getCod_deposito());

        this.objTraldoBodega.setCod_estado("Disponible");
        this.objTraldoBodega.setCod_estado2("EnvioTrasB");
        this.evento = "Nuevo";
        objTraldoBodega.setFecha(new Date());
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
        listTranslado.clear();
        this.evento = "Buscar";
        setObjTraldoBodega(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(TrasladoBodega objecto, int condicion) {
        setObjTraldoBodega(null);
        Object Resulta[] = new Object[2];
        Resulta = TrasladoService.recuperarInfo(objecto);
        setObjTraldoBodega((TrasladoBodega) Resulta[0]);
//        lista(2);
        setExecuteReport(true);
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
        setObjTraldoBodega(null);
    }

    public void transaccion() {

        Object Resulta[] = new Object[3];
        String mns = "";
        setExecuteReport(false);
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = TrasladoService.Transaccion(objTraldoBodega, "Nuevo");
                    mns = "Traslado Correctamente";
                    break;
                case "Eliminar":
                    Resulta = TrasladoService.Transaccion(objTraldoBodega, "Borrar");
                    mns = "Traslado Eliminado";
                    break;
                case "Editar":
                    Resulta = TrasladoService.Transaccion(objTraldoBodega, "Editar");
                    mns = "Traslado Editado";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("Blank_A4_1");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(TrasladoBodegaControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = TrasladoService.buscarDoc(objTraldoBodega);
//                    break;//                case "Buscar":
//                    Resulta = TrasladoService.buscarDoc(objTraldoBodega);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listTranslado.clear();
                    listTranslado = (List<TrasladoBodega>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjTraldoBodega(null);
                    getObjTraldoBodega();
                    if (evento.equalsIgnoreCase("Nuevo")) {
                        objTraldoBodega.setTrans((int) Resulta[2]);
                        System.out.println("Trans : " + objTraldoBodega.getTrans());
                        setExecuteReport(true);
                    }
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
        getObjTraldoBodega();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            if (objTraldoBodega.getCod_deposito2().equalsIgnoreCase("0")) {
                mns = "Se debe seleccionar bodega destino";
            }
            if (objTraldoBodega.getDetalleArt().size() == 0) {
                mns = "Se ddebe seleccionar al menos 1 articulo";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void prepareReporte(boolean reporte) throws JRException, IOException {

        System.out.println("reporte = " + reporte);
        if (reporte) {
            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/TrasladoBod.jasper"));
//            String datos = "[{\"trans\":86,\"cod_emp\":\"Data\",\"nom_emp\":\"DataPyme\",\"fec_doc\":\"2020-05-15\",\"nro_docum\":109,\"cod_estado\":\"Stock\",\"cod_deposito\":\"1\",\"nom_deposito\":\"Bodega Principal\",\"observacion\":\"Sistema\",\"case\":\"Ingreso\",\"cod_articulo\":100,\"nom_articulo\":\"Invictus\",\"cantidad\":100}]";
            String rawJsonData = SelService.ConsultaIreport("select D.nom_emp,A.fec_Doc,E.nom_deposito,F.nom_deposito as nom_deposito2,A.observacion,\n"
                    + "C.codigo as cod_articulo,C.nom_articulo,B.cant_enviada as cantidad\n"
                    + "from t_trasladobodega A inner join td_trasladobodega B\n"
                    + "on a.trans=b.trans left join m_articulos C \n"
                    + "on B.cod_articulo=C.cod_articulo left join m_empresa D on A.cod_emp=D.cod_emp\n"
                    + "left join m_depositos E on A.cod_deposito=E.cod_deposito\n"
                    + "left join m_depositos F on A.cod_deposito2=F.cod_deposito\n"
                    + "where A.trans=" + objTraldoBodega.getTrans());
            System.out.println("*************************");
            System.out.println("" + rawJsonData);
            System.out.println("" + rawJsonData.replace("\\", "").replace("\"[", "[").replace("]\"", "]"));

//            Reporte_AjusteStock[] stock = gson.fromJson(rawJsonData.replace("\\", ""), Reporte_AjusteStock[].class);
            //System.out.println(rawJsonData);
            String json = new Gson().toJson(rawJsonData.replace("\\", "").replace("\"[", "[").replace("]\"", "]"));

            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.replace("\\", "").replace("\"[", "[").replace("]\"", "]").getBytes());
//            System.out.println(json);
            JsonDataSource ds = new JsonDataSource(jsonDataStream);

            byte[] jp = JasperRunManager.runReportToPdf(jasper.getPath(), null, ds);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setContentLength(jp.length);
            try (ServletOutputStream outStream = response.getOutputStream()) {
                outStream.write(jp, 0, jp.length);
                outStream.flush();
                outStream.close();
            }
            FacesContext.getCurrentInstance().responseComplete();
        }
        setExecuteReport(true);
    }

    public void cargarDepositos() {
        System.out.println("Cargando cargarDepositos " + objTraldoBodega.getCod_emp());
        listDepositos.clear();
        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + objTraldoBodega.getCod_emp() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos obj = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setNom_deposito(map.get("nom_deposito").toString());
                listDepositos.add(obj);
            }
        }
        cargarUbicaciones();
    }

    public void cargarUbicaciones() {
        System.out.println("Cargando cargarDepositos " + objTraldoBodega.toString());
        if (objTraldoBodega.getCod_emp() == null) {
            System.out.println("Objecto Null");
        }
        listUbicaciones.clear();
        //Ubicaciones
//        System.out.println("Variable 1 : " + variables[0].toString());
//        System.out.println("Variable 1 : " + variables[1].toString());
        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_ubicacion,nom_ubicacion from m_ubicaciones where cod_emp='" + objTraldoBodega.getCod_emp() + "' "
                + "and cod_deposito='" + objTraldoBodega.getCod_deposito() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos4) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Ubicaciones obj = new Ubicaciones();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_ubicacion(map.get("cod_ubicacion").toString());
                obj.setNom_ubicacion(map.get("nom_ubicacion").toString());
                listUbicaciones.add(obj);
            }
        }
    }

    public List<Articulos> articulos(String query) {
        getObjArticulo();
        System.out.println("------------ Bean articulos------------");
        System.out.println("Articulo : " + objArticulo.toString());

        listArticulos = SelService.cargaArticulo(query, 2);
        return listArticulos;
    }

    public void cargaArticulo() {
        System.out.println("Carga Estado : " + objArticulo.toString());
        TrasladoBodegaDT obj = new TrasladoBodegaDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
        obj.setStock(ObjIni.StockDisponible(objTraldoBodega.getCod_emp(), objArticulo.getCod_articulo(), "S", objTraldoBodega.getCod_deposito()));
        if (buscarElemento(obj) == false) {
            objTraldoBodega.getDetalleArt().add(obj);
        }
        setObjArticulo(null);
    }

    public void eliminarArticuloGrilla(TrasladoBodegaDT obj) {
        if (buscarElemento(obj)) {
            objTraldoBodega.getDetalleArt().remove(obj);
        }

    }

    public boolean buscarElemento(TrasladoBodegaDT obj) {
        boolean valor = false;
        for (TrasladoBodegaDT obj2 : objTraldoBodega.getDetalleArt()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void reporte() {
        this.executeReport = true;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listTranslado.clear();
        listTranslado = TrasladoService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listTranslado.clear();
        listTranslado = TrasladoService.Lista();
        this.valorBusqueda = "";
    }

    public TrasladoBodegaService getTrasladoService() {
        return TrasladoService;
    }

    public void setTrasladoService(TrasladoBodegaService TrasladoService) {
        this.TrasladoService = TrasladoService;
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

    public TrasladoBodega getObjTraldoBodega() {
        if (objTraldoBodega == null) {
            objTraldoBodega = new TrasladoBodega();
        }
        return objTraldoBodega;
    }

    public void setObjTraldoBodega(TrasladoBodega objTraldoBodega) {
        this.objTraldoBodega = objTraldoBodega;
    }

    public List<TrasladoBodega> getListTranslado() {
        return listTranslado;
    }

    public void setListTranslado(List<TrasladoBodega> listTranslado) {
        this.listTranslado = listTranslado;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public List<Estados> getListEstados() {
        return listEstados;
    }

    public void setListEstados(List<Estados> listEstados) {
        this.listEstados = listEstados;
    }

    public List<Depositos> getListDepositos() {
        return listDepositos;
    }

    public void setListDepositos(List<Depositos> listDepositos) {
        this.listDepositos = listDepositos;
    }

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
    }

    public List<Ubicaciones> getListUbicaciones() {
        return listUbicaciones;
    }

    public void setListUbicaciones(List<Ubicaciones> listUbicaciones) {
        this.listUbicaciones = listUbicaciones;
    }

    public TrasladoBodegaDT getObjTraldoBodegaDT() {
        return objTraldoBodegaDT;
    }

    public void setObjTraldoBodegaDT(TrasladoBodegaDT objTraldoBodegaDT) {
        this.objTraldoBodegaDT = objTraldoBodegaDT;
    }

    public Articulos getObjArticulo() {
        if (objArticulo == null) {
            objArticulo = new Articulos();
        }
        return objArticulo;
    }

    public void setObjArticulo(Articulos objArticulo) {
        this.objArticulo = objArticulo;
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

    public boolean isExecuteReport() {
        return executeReport;
    }

    public void setExecuteReport(boolean executeReport) {
        this.executeReport = executeReport;
    }

}
