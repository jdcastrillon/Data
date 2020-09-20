package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Servicios.AjusteStockService;
import Servicios.Sistema.*;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class StockAjusteControlador {

    @Inject
    private AjusteStockService AJService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Gson gson = new Gson();
    private AjusteStock objAjuste;
    private List<AjusteStock> listajuste = new ArrayList();

    private List<Empresa> listEmpresas = new ArrayList();
    private List<Estados> listEstados = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Ubicaciones> listUbicaciones = new ArrayList();
    private List<Operaciones> oepraciones = new ArrayList();

    private AjusteStockDT objAjusteDT;
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

    //reporte
    private StreamedContent file;

    @PostConstruct
    public void init() {
        try {
            getObjAjuste();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StockAjusteControlador() {
        System.out.println("Inicia Controlador");
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listajuste.clear();
        listajuste = AJService.Lista();

        if (condicion == 1) {
            listEmpresas.clear();
            listDepositos.clear();
            listEstados.clear();
            oepraciones.clear();

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

            oepraciones.add(new Operaciones("Ingreso", 1));
            oepraciones.add(new Operaciones("Salida", -1));

//            //Estados
//            JsonArray Jelementos3 = ObjIni.listObjectos("select cod_estado,nom_estado from m_estados where cod_categoria='Stock' and cod_deposito='Deposito' and activo='S'");
//            for (JsonElement jsonElement : Jelementos3) {
//                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                    Estados obj = new Estados();
//                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                    obj.setCod_estado(map.get("cod_estado").toString());
//                    obj.setNom_estado(map.get("nom_estado").toString());
//                    listEstados.add(obj);
//                }
//            }
        }

    }

    public void prepareNuevo() {
        setObjAjuste(null);
        getObjAjuste();
        listEmpresas.clear();
        listDepositos.clear();
        listEstados.clear();
        listArticulos.clear();

        //Numerador
        objAjuste.setNro_docum(ObjIni.numerador("stockajuste"));

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

        //Operaciones
//        //Estados
//        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_estado,nom_estado from m_estados where cod_categoria='Stock' and cod_deposito='Deposito' and activo='S'");
//        for (JsonElement jsonElement : Jelementos3) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Estados obj = new Estados();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_estado(map.get("cod_estado").toString());
//                obj.setNom_estado(map.get("nom_estado").toString());
//                listEstados.add(obj);
//            }
//        }
        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        System.out.println("Size Depositod : " + listDepositos.size());
        objAjuste.setCod_emp(listEmpresas.get(0).getCod_emp());
        objAjuste.setCod_deposito(listDepositos.get(0).getCod_deposito());
        objAjuste.setCod_estado("Disponible");
//        objAjuste.setSigno(1);

        this.evento = "Nuevo";
        objAjuste.setFecha(new Date());
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

    public void reporte() {
        this.executeReport = true;
    }

    public void prepareReporte(boolean reporte) throws JRException, IOException {

        System.out.println("reporte = " + reporte);
        if (reporte) {
            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/AjusteStock.jasper"));
            String datos = "[{\"trans\":86,\"cod_emp\":\"Data\",\"nom_emp\":\"DataPyme\",\"fec_doc\":\"2020-05-15\",\"nro_docum\":109,\"cod_estado\":\"Stock\",\"cod_deposito\":\"1\",\"nom_deposito\":\"Bodega Principal\",\"observacion\":\"Sistema\",\"case\":\"Ingreso\",\"cod_articulo\":100,\"nom_articulo\":\"Invictus\",\"cantidad\":100}]";
            String rawJsonData = SelService.ConsultaIreport("select A.trans,A.cod_emp,C.nom_emp,CAST(a.fec_doc AS varchar) fec_doc ,a.nro_docum,A.cod_estado,A.cod_deposito,D.nom_deposito,\n"
                    + "case when LENGTH(a.observacion)>0 then a.observacion else 'N/A' end observacion,\n"
                    + "CAST(case when A.signo=1 then 'Ingreso' else 'Salida' end  as varchar)operacion,\n"
                    + "E.codigo as \"cod_articulo\",E.nom_articulo,B.cantidad\n"
                    + "from t_ajuststock A INNER JOIN	 td_ajuststock b ON a.trans=b.Trans\n"
                    + "INNER JOIN m_empresa C on A.cod_emp=C.cod_emp\n"
                    + "INNER JOIN m_depositos D on A.cod_deposito=D.cod_deposito\n"
                    + "INNER JOIN m_Articulos E on B.cod_articulo=E.cod_Articulo\n"
                    + "where A.trans=" + objAjuste.getTrans());
            System.out.println("*************************");
            System.out.println("" + rawJsonData);
            System.out.println("" + rawJsonData.replace("\\", "").replace("\"[", "[").replace("]\"", "]"));
            System.out.println("" + datos);
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

    public void prepareCrud(AjusteStock objecto, int condicion) {
        setObjAjuste(null);
        Object Resulta[] = new Object[2];
        Resulta = AJService.recuperarInfo(objecto);
        setObjAjuste((AjusteStock) Resulta[0]);
        lista(2);
        //Condiciones
        setExecuteReport(true);
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
            case 4:
                this.evento = "Reporte";
                this.executeReport = true;
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
        setObjAjuste(null);
    }

    public void transaccion() throws IOException {
        System.out.println("****************************************");
        System.out.println("Objecto : " + objAjuste.toString());
        Object Resulta[] = new Object[3];
        String mns = "";
        setExecuteReport(false);
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = AJService.Transaccion(objAjuste, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = AJService.Transaccion(objAjuste, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = AJService.Transaccion(objAjuste, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listajuste.clear();
                    listajuste = (List<AjusteStock>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjAjuste(null);
                    getObjAjuste();
                    if (evento.equalsIgnoreCase("Nuevo")) {
                        objAjuste.setTrans((int) Resulta[2]);
                        System.out.println("Trans : " + objAjuste.getTrans());
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
        getObjAjuste();
        String mns = "";

        if (this.nuevo == false) {
            //Validaciones
            if (objAjuste.getCod_deposito().equalsIgnoreCase("0")) {
                mns = "Debe seleccionar una bodega";
            } else if (objAjuste.getDetalleArt().isEmpty()) {
                mns = "Debe Agregar al menos un articulo";
            } else if (objAjuste.getSigno() == 0) {
                mns = "Debe Seleccionar una operacion";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void cargarDepositos() {
        System.out.println("Cargando cargarDepositos " + objAjuste.getCod_emp());
        listDepositos.clear();
        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + objAjuste.getCod_emp() + "' and activo='S'");
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
        System.out.println("Cargando cargarDepositos " + objAjuste.toString());
        if (objAjuste.getCod_emp() == null) {
            System.out.println("Objecto Null");
        }
        listUbicaciones.clear();
        //Ubicaciones
        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_ubicacion,nom_ubicacion from m_ubicaciones where cod_emp='" + objAjuste.getCod_emp() + "' "
                + "and cod_deposito='" + objAjuste.getCod_deposito() + "' and activo='S'");
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
        AjusteStockDT obj = new AjusteStockDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
        if (buscarElemento(obj) == false) {
            objAjuste.getDetalleArt().add(0, obj);
        }
        setObjArticulo(null);
    }

    public void eliminarArticuloGrilla(AjusteStockDT obj) {
        if (buscarElemento(obj)) {
            objAjuste.getDetalleArt().remove(obj);
        }

    }

    public boolean buscarElemento(AjusteStockDT obj) {
        boolean valor = false;
        for (AjusteStockDT obj2 : objAjuste.getDetalleArt()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listajuste.clear();
        listajuste = AJService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listajuste.clear();
        listajuste = AJService.Lista();
        this.valorBusqueda = "";
    }

    public void downloadReporte() throws JRException, IOException {
        System.out.println("Entro a descargar reporte");
        Object Resulta[] = new Object[2];
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/Bodega.jasper"));

        System.out.println("Ruta = " + jasper.getPath());

        String rawJsonData = "[{\"cod_deposito\":\"01\",\"nom_deposito\":\"Deposito Principal\",\"cod_emp\":\"Data\"}]";

        System.out.println(rawJsonData);
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());

        JsonDataSource ds = new JsonDataSource(jsonDataStream);

        JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=Reporte.pdf");
        try (ServletOutputStream stream = response.getOutputStream()) {
            JasperExportManager.exportReportToPdfStream(jp, stream);
        }
        FacesContext.getCurrentInstance().responseComplete();
        Resulta[0] = "OK";
        Resulta[1] = "Reporte";
        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('vendedor').hide();");
    }

    public void datosBean() {
        System.out.println("Datos Bean signo " + objAjuste.getSigno());

    }

    public AjusteStockService getAJService() {
        return AJService;
    }

    public void setAJService(AjusteStockService AJService) {
        this.AJService = AJService;
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

    public AjusteStock getObjAjuste() {
        if (objAjuste == null) {
            objAjuste = new AjusteStock();
        }
        return objAjuste;
    }

    public void setObjAjuste(AjusteStock objAjuste) {
        this.objAjuste = objAjuste;
    }

    public List<AjusteStock> getListajuste() {
        return listajuste;
    }

    public void setListajuste(List<AjusteStock> listajuste) {
        this.listajuste = listajuste;
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

    public List<Depositos> getListDepositos() {
        return listDepositos;
    }

    public void setListDepositos(List<Depositos> listDepositos) {
        this.listDepositos = listDepositos;
    }

    public AjusteStockDT getObjAjusteDT() {
        if (objAjusteDT == null) {
            objAjusteDT = new AjusteStockDT();
        }
        return objAjusteDT;
    }

    public void setObjAjusteDT(AjusteStockDT objAjusteDT) {
        this.objAjusteDT = objAjusteDT;
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

    public boolean isReporte() {
        return reporte;
    }

    public void setReporte(boolean reporte) {
        this.reporte = reporte;
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

    public List<Operaciones> getOepraciones() {
        return oepraciones;
    }

    public void setOepraciones(List<Operaciones> oepraciones) {
        this.oepraciones = oepraciones;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

}
