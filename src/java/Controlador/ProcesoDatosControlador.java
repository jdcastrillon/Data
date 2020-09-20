package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Servicios.ProcesoDatosService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class ProcesoDatosControlador {

    @Inject
    private ProcesoDatosService CService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private ProcesoDatos objProDatos;
    private Inventario objInvetario;
    private List<ProcesoDatos> listaProcesoDatos = new ArrayList();
    private List<Inventario> listInventario = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private UploadedFile uploadedFile;

    //Variables de combo
    Object acciones[] = new Object[6];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private String evento;
    private String eventJavascrtipt = "";
    private boolean executeReport = false;

    @PostConstruct
    public void init() {
        try {
            getObjProcesoDatos();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ProcesoDatosControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listaProcesoDatos.clear();
        listEmpresas.clear();
        listaProcesoDatos = CService.Lista();

    }

    public void prepareNuevo() {
        setObjProcesoDatos(null);
        getObjProcesoDatos();

        listEmpresas.clear();
        listInventario.clear();
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
        System.out.println("select a.nro_inventario,cod_emp,a.fec_doc,B.nro_conteo from t_inventario A inner join t_pro_conteo B \n"
                + "on A.nro_inventario=B.nro_inventario where \n"
                + "B.nro_conteo not in(select nro_conteo from t_pro_datos where nro_inventario=A.nro_inventario)\n"
                + "and  cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");
        //Inventario 
        JsonArray Jelementos2 = ObjIni.listObjectos(
                "select a.nro_inventario,cod_emp,a.fec_doc,B.nro_conteo from t_inventario A inner join t_pro_conteo B \n"
                + "on A.nro_inventario=B.nro_inventario where \n"
                + "B.nro_conteo not in(select nro_conteo from t_pro_datos where nro_inventario=A.nro_inventario)\n"
                + "and  cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Inventario obj = new Inventario();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);

                obj.setNro_inventario((int) Double.parseDouble(map.get("nro_inventario").toString()));
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setFec_doc(map.get("fec_doc").toString());
                obj.setNro_conteo((int) Double.parseDouble(map.get("nro_conteo").toString()));
                listInventario.add(obj);
            }
        }
        //Numerador
        objProDatos.setNro_proceso(ObjIni.numerador("nro_proceso"));
        this.evento = "Nuevo";
        objProDatos.setFecha(new Date());
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
        listaProcesoDatos.clear();
        this.evento = "Buscar";
        setObjProcesoDatos(null);
        controlEventos(evento);
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, SQLException {
        FacesMessage msg = new FacesMessage("Aviso", event.getFile().getFileName() + " se cargo correctamente..!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        uploadedFile = event.getFile();
        cargarArchivo();
    }

    public void cargarArchivo() throws IOException {
        if (uploadedFile != null) {

            HSSFWorkbook workbook;
            // Get the workbook instance for XLS file
            InputStream input = uploadedFile.getInputstream();
            workbook = new HSSFWorkbook(input);
            // Get first sheet from the workbook
            System.out.println("numero de hojas =" + workbook.getNumberOfSheets());
            ArrayList<String> lista = new ArrayList();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                // Iterate through each rows from first sheet
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() > 0) {
                        StringBuilder sb = new StringBuilder();
                        // For each row, iterate through each columns
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            System.out.println("Tipo :" + cell.getCellType());
//                            switch (cell.getCellType()) {
//
//                                case Cell.CELL_TYPE_BOOLEAN:
//                                    System.out.print(cell.getBooleanCellValue());
//                                    break;
//                                case Cell.CELL_TYPE_NUMERIC:
////                                System.out.print(Integer.toString((int) cell.getNumericCellValue()));
//                                    sb.append(Integer.toString((int) cell.getNumericCellValue())).append(",");
//                                    break;
//                                case Cell.CELL_TYPE_STRING:
////                                System.out.print(cell.getStringCellValue());
//                                    sb.append(cell.getStringCellValue().trim()).append(",");
//                                    break;
//                            }
                        }
                        if (!sb.toString().equals("")) {
                            lista.add(sb.toString().substring(0, sb.toString().length() - 1));
                        }
                    }

                }
            }

            lista.forEach((next) -> {
                System.out.println("next " + next);
                String datos[] = next.split(",");//                                                        int nro_proceso, int codigo, String nombre, int stock, int cantidad, int ajuste
                ProcesoDatosDT obj = new ProcesoDatosDT(objProDatos.getNro_proceso(), Integer.parseInt(datos[0]), datos[1],
                        Integer.parseInt(datos[2]), Integer.parseInt(datos[3]), (Integer.parseInt(datos[3]) - Integer.parseInt(datos[2])));
                objProDatos.getDetalleProDatos().add(obj);
            });

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Debes seleccionar primero un archivo..!"));
        }

    }

//    public void prepareReporte(boolean reporte) throws JRException, IOException {
//
//        System.out.println("reporte = " + reporte);
//
//        if (reporte) {
//            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/Prueba.jasper"));
//
//            System.out.println("Mauricio = " + jasper.getPath());
//
////        ReportePruebaDataSource rdatasource = new ReportePruebaDataSource();
//            List<ReportePrueba> listReporte = new ArrayList();
//
//            for (int i = 0; i < 10; i++) {
//                ReportePrueba r = new ReportePrueba(i, "Nombre" + i, "Apellido" + i, "Direccion" + i, "Telefono" + i);
//                listReporte.add(r);
//            }
////        rdatasource.setListReporte(listReporte);
//            String json = new Gson().toJson(listReporte);
//
//            System.out.println(json);
//            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());
//
//            JsonDataSource ds = new JsonDataSource(jsonDataStream);
//
//            byte[] jp = JasperRunManager.runReportToPdf(jasper.getPath(), null, ds);
//            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//            response.setContentType("application/pdf");
//            response.setContentLength(jp.length);
//            try (ServletOutputStream outStream = response.getOutputStream()) {
//                outStream.write(jp, 0, jp.length);
//                outStream.flush();
//                outStream.close();
//            }
//            FacesContext.getCurrentInstance().responseComplete();
//        }
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

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/ProcesoProcesoDatos.jasper"));

        System.out.println("Mauricio = " + jasper.getPath());

        //Ajuste Json
//        List<StockxDepositos> listReporte = new ArrayList();
//
////        ReportePruebaDataSource rdatasource = new ReportePruebaDataSource();
//        JsonArray Jelementos3 = ObjIni.listObjectos("select * from s_stkdepositos");
//        for (JsonElement jsonElement : Jelementos3) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                StockxDepositos obj = new StockxDepositos();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_emp(map.get("cod_emp").toString());
//                obj.setCod_estado(map.get("cod_estado").toString());
//                obj.setCod_tit(map.get("cod_tit").toString());
//                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
//                obj.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
//                listReporte.add(obj);
//            }
//        }
        //        rdatasource.setListReporte(listReporte);
        String json = new Gson().toJson("");

        System.out.println(json);
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());

        JsonDataSource ds = new JsonDataSource(jsonDataStream);

        JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=ProcesoProcesoDatos.xls");

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

    public void prepareCrud(ProcesoDatos objecto, int condicion) {
        setObjProcesoDatos(null);
        Object Resulta[] = new Object[2];
        Resulta = CService.recuperarInfo(objecto);
        setObjProcesoDatos((ProcesoDatos) Resulta[0]);
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
        setObjProcesoDatos(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = CService.Transaccion(objProDatos, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = CService.Transaccion(objProDatos, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = CService.Transaccion(objProDatos, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("reporte");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(ProcesoDatosControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = CService.buscarDoc(objProDatos);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listaProcesoDatos.clear();
                    listaProcesoDatos = (List<ProcesoDatos>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjProcesoDatos(null);
                    this.evento = "inicio";
                    controlEventos(evento);
                }
                setExecuteReport(true);
            } else {
                setExecuteReport(false);
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
        getObjProcesoDatos();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objProDatos.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void cargarNroInventario() {
        getObjInvetario();
        System.out.println("Orden de compra : " + objInvetario.toString());
        objProDatos.setCod_emp(objInvetario.getCod_emp());
        objProDatos.setNro_inventario(objInvetario.getNro_inventario());

        objProDatos.setNro_conteo(objInvetario.getNro_conteo());
        JsonArray conteo = ObjIni.listObjectos("select count(nro_proceso)+1 conteo from t_pro_datos where nro_inventario=" + objInvetario.getNro_inventario());

        int Nroconteo = 0;
        for (JsonElement jsonElement : conteo) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                Nroconteo = ((int) Double.parseDouble(map.get("conteo").toString()));
            }
        }
        objProDatos.setNro_proceso(Nroconteo);
    }

//    public void cargarInventario() {
////        System.out.println("Cargando cargarInventario " + objProDatos.getCod_emp());
//        listInventario.clear();
//        //Depositos
//        JsonArray Jelementos2 = ObjIni.listObjectos("select nro_inventario from t_inventario where cod_emp='" + objProDatos.getCod_emp() + "'");
//        for (JsonElement jsonElement : Jelementos2) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Inventario obj = new Inventario();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setNro_inventario((int) map.get("nro_inventario"));
//                listInventario.add(obj);
//            }
//        }
////        cargarUbicaciones();
//    }
//    public void cargarDepositos() {
//        System.out.println("Cargando cargarDepositos Mauricio" + objProDatos.getCod_emp());
//        listDepositos.clear();
//        //Depositos
//        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + objProDatos.getCod_emp() + "' and activo='S'");
//        for (JsonElement jsonElement : Jelementos2) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Depositos obj = new Depositos();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_deposito(map.get("cod_deposito").toString());
//                obj.setNom_deposito(map.get("nom_deposito").toString());
//                listDepositos.add(obj);
//            }
//        }
//
//    }
//    public void cargarSubCategorias() {
//        listSubCategorias.clear();
//        //Depositos
//        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_subcategoria,nom_subcategoria from m_subcategoria where cod_categoria='" + objProDatos.getCod_categoria() + "'");
//        for (JsonElement jsonElement : Jelementos2) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                SubCategoria obj = new SubCategoria();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_subcategoria(map.get("cod_subcategoria").toString());
//                obj.setNom_subcategoria(map.get("nom_subcategoria").toString());
//                listSubCategorias.add(obj);
//            }
//        }
//    }
//    public void cargaArticulo() {
//        System.out.println("Carga Estado : " + objArticulo.toString());
//        ProProcesoDatosDT obj = new ProProcesoDatosDT(ObjIni.numerador("nro_art_conteo"), objProDatos.getNro_conteo(), objArticulo.getCod_articulo(), objArticulo.getNom_articulo());
//        if (buscarElemento(obj) == false) {
//            objProDatos.getDetalleCont().add(obj);
//        }
//        setObjArticulo(null);
//    }
    public ProcesoDatosService getCService() {
        return CService;
    }

    public void setCService(ProcesoDatosService CService) {
        this.CService = CService;
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

    public ProcesoDatos getObjProcesoDatos() {
        if (objProDatos == null) {
            objProDatos = new ProcesoDatos();
        }
        return objProDatos;
    }

    public void setObjProcesoDatos(ProcesoDatos objProDatos) {
        this.objProDatos = objProDatos;
    }

    public List<ProcesoDatos> getListaProcesoDatos() {
        return listaProcesoDatos;
    }

    public void setListaProcesoDatos(List<ProcesoDatos> listaProcesoDatos) {
        this.listaProcesoDatos = listaProcesoDatos;
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

    public boolean isReporte() {
        return reporte;
    }

    public void setReporte(boolean reporte) {
        this.reporte = reporte;
    }

    public String getEventJavascrtipt() {
        return eventJavascrtipt;
    }

    public void setEventJavascrtipt(String eventJavascrtipt) {
        this.eventJavascrtipt = eventJavascrtipt;
    }

    public boolean isExecuteReport() {
        return executeReport;
    }

    public void setExecuteReport(boolean executeReport) {
        this.executeReport = executeReport;
    }

    public ProcesoDatos getObjProDatos() {
        return objProDatos;
    }

    public void setObjProDatos(ProcesoDatos objProDatos) {
        this.objProDatos = objProDatos;
    }

    public List<Inventario> getListInventario() {
        return listInventario;
    }

    public void setListInventario(List<Inventario> listInventario) {
        this.listInventario = listInventario;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public Inventario getObjInvetario() {
        if (objInvetario == null) {
            objInvetario = new Inventario();
        }

        return objInvetario;
    }

    public void setObjInvetario(Inventario objInvetario) {
        this.objInvetario = objInvetario;
    }

}
