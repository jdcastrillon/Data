package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Servicios.CierreInventarioService;
import Servicios.InventarioService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class CierreInventarioControlador {

    @Inject
    private CierreInventarioService cierreInventarioService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    @Inject
    private InventarioService InventarioService;

    private CierreInventario objCierreInventario;

    private List<CierreInventario> listCierreInventario = new ArrayList();
    private List<Inventario> listInventario = new ArrayList();

    private List<Empresa> listEmpresas = new ArrayList();
    private List<ProcesoDatosDT> listProDatosDT = new ArrayList();
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
            getObjCierreInventario();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CierreInventarioControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listCierreInventario.clear();
        listCierreInventario = cierreInventarioService.Lista();

        listProDatosDT.clear();

        if (condicion == 1) {
            listEmpresas.clear();

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
        }
    }

    public void prepareNuevo() {
        setObjCierreInventario(null);
        getObjCierreInventario();
        listEmpresas.clear();

        listInventario.clear();
        listInventario = InventarioService.Lista();
        //Numerador
        //objInventario.setNro_inventario(ObjIni.numerador_Controlado("Data", "nro_inventario"));
        //Empresas
//        JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
//        for (JsonElement jsonElement : Jelementos) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Empresa emp = new Empresa();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                emp.setCod_emp(map.get("cod_emp").toString());
//                emp.setNom_emp(map.get("nom_emp").toString());
//                listEmpresas.add(emp);
//            }
//        }
        //Variables
//        System.out.println("Size Empresa : " + listEmpresas.size());
//        objCierreInventario.setCod_emp(listEmpresas.get(0).getCod_emp());
        this.evento = "Nuevo";
//        objCierreInventario.setFecha_c(new Date());
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
        listInventario.clear();
        this.evento = "Buscar";
        setObjCierreInventario(null);
        controlEventos(evento);
    }

//    public void prepareReporte() throws JRException, IOException {
//        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/Prueba.jasper"));
//
//        System.out.println("Mauricio = " + jasper.getPath());
//
////        ReportePruebaDataSource rdatasource = new ReportePruebaDataSource();
//        List<ReportePrueba> listReporte = new ArrayList();
//
//        for (int i = 0; i < 10; i++) {
//            ReportePrueba r = new ReportePrueba(i, "Nombre" + i, "Apellido" + i, "Direccion" + i, "Telefono" + i);
//            listReporte.add(r);
//        }
////        rdatasource.setListReporte(listReporte);
//        String json = new Gson().toJson(listReporte);
//
//        System.out.println(json);
//        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());
//
//        JsonDataSource ds = new JsonDataSource(jsonDataStream);
//
//        byte[] jp = JasperRunManager.runReportToPdf(jasper.getPath(), null, ds);
//        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        response.setContentType("application/pdf");
//        response.setContentLength(jp.length);
//        try (ServletOutputStream outStream = response.getOutputStream()) {
//            outStream.write(jp, 0, jp.length);
//            outStream.flush();
//            outStream.close();
//        }
//        FacesContext.getCurrentInstance().responseComplete();
//
//    }
//
//    public void downloadReporte() throws JRException, IOException {
//        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/Prueba.jasper"));
//
//        System.out.println("Mauricio = " + jasper.getPath());
//
////        ReportePruebaDataSource rdatasource = new ReportePruebaDataSource();
//        List<ReportePrueba> listReporte = new ArrayList();
//
//        for (int i = 0; i < 10; i++) {
//            ReportePrueba r = new ReportePrueba(i, "Nombre" + i, "Apellido" + i, "Direccion" + i, "Telefono" + i);
//            listReporte.add(r);
//        }
////        rdatasource.setListReporte(listReporte);
//        String json = new Gson().toJson(listReporte);
//
//        System.out.println(json);
//        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());
//
//        JsonDataSource ds = new JsonDataSource(jsonDataStream);
//
////        byte[] jp = JasperRunManager.runReportToPdf(jasper.getPath(), null, ds);
////        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
////        response.setContentType("application/pdf");
////        response.setContentLength(jp.length);
////        try (ServletOutputStream outStream = response.getOutputStream()) {
////            outStream.write(jp, 0, jp.length);
////            outStream.flush();
////            outStream.close();
////        }
////        FacesContext.getCurrentInstance().responseComplete();
//        JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
//        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        response.addHeader("Content-disposition", "attachment; filename=Reporte.pdf");
//        try (ServletOutputStream stream = response.getOutputStream()) {
//            JasperExportManager.exportReportToPdfStream(jp, stream);
//        }
//        FacesContext.getCurrentInstance().responseComplete();
//
//    }
    public void exportarExcel() throws IOException, JRException {

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/PruebaExcel.jasper"));

        System.out.println("Mauricio = " + jasper.getPath());

// Ajustar Json
//        List<ReportePrueba> listReporte = new ArrayList();
//
//        for (int i = 0; i < 10; i++) {
//            ReportePrueba r = new ReportePrueba(i, "Nombre" + i, "Apellido" + i, "Direccion" + i, "Telefono" + i);
//            listReporte.add(r);
//        }
//
        String json = new Gson().toJson("");

        System.out.println(json);
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());

        JsonDataSource ds = new JsonDataSource(jsonDataStream);

        JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=PruebaExcel.xls");

        ServletOutputStream stream = response.getOutputStream();
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, stream);
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.exportReport();
        stream.flush();

        FacesContext.getCurrentInstance().responseComplete();

    }

    public void prepareCrud(CierreInventario objecto, int condicion) {
        setObjCierreInventario(null);
        Object Resulta[] = new Object[2];
        Resulta = cierreInventarioService.recuperarInfo(objecto);
        setObjCierreInventario((CierreInventario) Resulta[0]);
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
        setObjCierreInventario(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = cierreInventarioService.Transaccion(objCierreInventario, "Nuevo");
                    mns = "Cierre creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = cierreInventarioService.Transaccion(objCierreInventario, "Borrar");
                    mns = "Cierre Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = cierreInventarioService.Transaccion(objCierreInventario, "Editar");
                    mns = "Cierre Editado exitosamente";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("reporte");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(CierreInventarioControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = InventarioService.buscarDoc(objInventario);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listProDatosDT.clear();
                    listCierreInventario.clear();
                    listCierreInventario = (List<CierreInventario>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjCierreInventario(null);
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
        getObjCierreInventario();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objInventario.getCod_tipodoc() + "'")) {
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
        listInventario.clear();
        listCierreInventario = cierreInventarioService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listInventario.clear();
        listCierreInventario = cierreInventarioService.Lista();
        this.valorBusqueda = "";
    }

    public CierreInventarioService getCierreInventarioService() {
        return cierreInventarioService;
    }

    public void setCierreInventarioService(CierreInventarioService cierreInventarioService) {
        this.cierreInventarioService = cierreInventarioService;
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

    public CierreInventario getObjCierreInventario() {
        if (objCierreInventario == null) {
            objCierreInventario = new CierreInventario();
        }
        return objCierreInventario;
    }

    public void setObjCierreInventario(CierreInventario objCierreInventario) {
        this.objCierreInventario = objCierreInventario;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
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

    public void setCurrentInventario(int id) {
//        System.out.println("id = " + id);
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        listProDatosDT.clear();
        if (id > 0) {
            listInventario.stream().filter((inv) -> (inv.getNro_inventario() == id)).forEachOrdered((inv) -> {
                String[] dateString = inv.getFec_doc().split("-");
                String strFecha = inv.getFec_doc();
                Date fecha = null;
                try {
                    fecha = formato.parse(strFecha);

                } catch (ParseException ex) {
                    System.out.println("Error " + ex);
                }
                objCierreInventario.setCod_emp(inv.getCod_emp());
                objCierreInventario.setFecha2(new Date());
                objCierreInventario.setFecha(fecha);
                objCierreInventario.setFec_inv(dateString[2] + "/" + dateString[1] + "/" + dateString[0]);
                objCierreInventario.setObservacion(inv.getObservacion());
            });
            JsonArray Jelementos = ObjIni.listObjectos("select d.* from t_pro_datos t "
                    + "inner join  td_pro_datos d on t.nro_proceso = d.nro_proceso "
                    + "where t.nro_inventario = " + id);
            for (JsonElement jsonElement : Jelementos) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    ProcesoDatosDT prdDt = new ProcesoDatosDT();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    prdDt.setNro_detalle_pro(new BigDecimal(map.get("nro_detalle_pro").toString()).intValue());
                    prdDt.setNro_proceso(new BigDecimal(map.get("nro_proceso").toString()).intValue());
                    prdDt.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                    prdDt.setNom_articulo(map.get("nom_articulo").toString());
                    prdDt.setStock(new BigDecimal(map.get("stock").toString()).intValue());
                    prdDt.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                    prdDt.setAjuste(new BigDecimal(map.get("ajuste").toString()).intValue());
                    prdDt.setLinea(new BigDecimal(map.get("linea").toString()).intValue());
                    listProDatosDT.add(prdDt);
                }
            }
        }

        /*else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", "Seleccione Inventario"));
        }
         */
//        System.out.println(this.currentInventario.toString());
    }

    public List<ProcesoDatosDT> getListProDatosDT() {
        return listProDatosDT;
    }

    public void setListProDatosDT(List<ProcesoDatosDT> listProDatosDT) {
        this.listProDatosDT = listProDatosDT;
    }

    public List<CierreInventario> getListCierreInventario() {
        return listCierreInventario;
    }

    public void setListCierreInventario(List<CierreInventario> listCierreInventario) {
        this.listCierreInventario = listCierreInventario;
    }

    public List<Inventario> getListInventario() {
        return listInventario;
    }

    public void setListInventario(List<Inventario> listInventario) {
        this.listInventario = listInventario;
    }

}
