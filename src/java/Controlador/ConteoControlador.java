package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Modelo.SubCategoria;
import Servicios.ConteoService;
import Servicios.Sistema.*;
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
public class ConteoControlador {

    @Inject
    private ConteoService CService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Conteo objConteo;
    private List<Conteo> listaConteo = new ArrayList();
    private List<Inventario> listInventario = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Categoria> listCategorias = new ArrayList();
    private List<SubCategoria> listSubCategorias = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();

    private Articulos objArticulo;

//    private ProConteoDT objConteoDT;
//    private Articulos objArticulo;
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
    private String eventJavascrtipt = "";
    private boolean executeReport = false;

    @PostConstruct
    public void init() {
        try {
            getObjConteo();
//            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ConteoControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listaConteo.clear();
        listaConteo = CService.Lista();
        listInventario.clear();
        listEmpresas.clear();
        listDepositos.clear();
        listCategorias.clear();
        listSubCategorias.clear();
        listArticulos.clear();

//        if (condicion == 1) {
//          
//            JsonArray Jelementos = ObjIni.listObjectos("select cod_emp,nom_emp from m_empresa");
//            for (JsonElement jsonElement : Jelementos) {
//                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                    Empresa emp = new Empresa();
//                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                    emp.setCod_emp(map.get("cod_emp").toString());
//                    emp.setNom_emp(map.get("nom_emp").toString());
//                    listEmpresas.add(emp);
//                }
//            }
//
//            //Depositos
//            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
//            for (JsonElement jsonElement : Jelementos2) {
//                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                    Depositos obj = new Depositos();
//                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                    obj.setCod_deposito(map.get("cod_deposito").toString());
//                    obj.setNom_deposito(map.get("nom_deposito").toString());
//                    listDepositos.add(obj);
//                }
//            }
//
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
//        }
    }

    public void prepareNuevo() {
        setObjConteo(null);
        getObjConteo();
        listaConteo.clear();
        listaConteo = CService.Lista();
        listInventario.clear();
        listEmpresas.clear();
        listDepositos.clear();
        listCategorias.clear();
        listSubCategorias.clear();
        listArticulos.clear();

        //Numerador
        objConteo.setNro_conteo(ObjIni.numerador("nro_conteo"));

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
//        //Inventario 
        JsonArray Jelementos2 = ObjIni.listObjectos("select nro_inventario from t_inventario where cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Inventario obj = new Inventario();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);

                obj.setNro_inventario((int) Double.parseDouble(map.get("nro_inventario").toString()));
                listInventario.add(obj);
            }
        }

//        //Depositos
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos obj = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setNom_deposito(map.get("nom_deposito").toString());
                listDepositos.add(obj);
            }
        }

//        //Categorias
        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_categoria,nom_categoria from m_categoria ");//where cod_categoria='Stock'
        for (JsonElement jsonElement : Jelementos4) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Categoria obj = new Categoria();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_categoria(map.get("cod_categoria").toString());
                obj.setNom_categoria(map.get("nom_categoria").toString());
                listCategorias.add(obj);
            }
        }
        //Variables
//        System.out.println("Size Empresa : " + listEmpresas.size());
//        System.out.println("Size Depositod : " + listDepositos.size());
////        objConteo.setCod_emp(listEmpresas.get(0).getCod_emp());
//        objConteo.setCod_deposito(listDepositos.get(0).getCod_deposito());
        this.evento = "Nuevo";
        objConteo.setFecha(new Date());
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
        listaConteo.clear();
        this.evento = "Buscar";
        setObjConteo(null);
        controlEventos(evento);
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

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/ProcesoConteo.jasper"));

        System.out.println("Mauricio = " + jasper.getPath());

        //Ajuste para Json
//
//        List<StockxDepositos> listReporte = new ArrayList();
//
//
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
        String String_json = SelService.ConsultaIreport("select A.cod_emp,A.cod_estado,A.cod_tit,B.codigo as cod_articulo,A.cantidad from s_stkdepositos A INNER JOIN m_articulos B on A.cod_Articulo=B.cod_Articulo\n"
                + "where cod_emp='Data' and cod_estado='Disponible'");
//        String json = new Gson().toJson(String_json);
        System.out.println(String_json);
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(String_json.getBytes());

        JsonDataSource ds = new JsonDataSource(jsonDataStream);

        JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=ProcesoConteo.xls");

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

    public void prepareCrud(Conteo objecto, int condicion) {
        setObjConteo(null);
        Object Resulta[] = new Object[2];
        Resulta = CService.recuperarInfo(objecto);
        setObjConteo((Conteo) Resulta[0]);
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
        setObjConteo(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = CService.Transaccion(objConteo, "Nuevo");
                    mns = "Deposito Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = CService.Transaccion(objConteo, "Borrar");
                    mns = "Deposito Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = CService.Transaccion(objConteo, "Editar");
                    mns = "Deposito Editado exitosamente";
                    break;
                case "Reporte": {
                    try {
                        Resulta = SelService.PDFDescargar2("reporte");
                    } catch (IOException ex) {
                        System.out.println("Error reporte");
                        Logger.getLogger(ConteoControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = CService.buscarDoc(objConteo);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listaConteo.clear();
                    listaConteo = (List<Conteo>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjConteo(null);
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
        getObjConteo();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objConteo.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void cargarInventario() {
//        System.out.println("Cargando cargarInventario " + objConteo.getCod_emp());
        listInventario.clear();
        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select nro_inventario from t_inventario where cod_emp='" + objConteo.getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Inventario obj = new Inventario();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setNro_inventario((int) map.get("nro_inventario"));
                listInventario.add(obj);
            }
        }
//        cargarUbicaciones();
    }

    public void cargarDepositos() {
        System.out.println("Cargando cargarDepositos Mauricio" + objConteo.getCod_emp());
        listDepositos.clear();
        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + objConteo.getCod_emp() + "' and activo='S'");
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

    public void cargarSubCategorias() {
        listSubCategorias.clear();
        //Depositos
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_subcategoria,nom_subcategoria from m_subcategoria where cod_categoria='" + objConteo.getCod_categoria() + "'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                SubCategoria obj = new SubCategoria();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_subcategoria(map.get("cod_subcategoria").toString());
                obj.setNom_subcategoria(map.get("nom_subcategoria").toString());
                listSubCategorias.add(obj);
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
        ProConteoDT obj = new ProConteoDT(ObjIni.numerador("nro_art_conteo"), objConteo.getNro_conteo(), objArticulo.getCod_articulo(), objArticulo.getNom_articulo());
        if (buscarElemento(obj) == false) {
            objConteo.getDetalleCont().add(obj);
        }
        setObjArticulo(null);
    }

    public boolean buscarElemento(ProConteoDT obj) {
        boolean valor = false;
        for (ProConteoDT obj2 : objConteo.getDetalleCont()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void cargarUbicaciones() {
//        System.out.println("Cargando cargarInventario " + objConteo.toString());
//        if (objConteo.getCod_deposito() == null) {
//            System.out.println("Objecto Null");
//        }
//        listUbicaciones.clear();
//        //Ubicaciones
////        System.out.println("Variable 1 : " + variables[0].toString());
////        System.out.println("Variable 1 : " + variables[1].toString());
//        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_ubicacion,nom_ubicacion from m_ubicaciones where cod_emp='" + objConteo.getCod_deposito() + "' "
//                + "and cod_deposito='" + objConteo.getCod_deposito() + "' and activo='S'");
//        for (JsonElement jsonElement : Jelementos4) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Ubicaciones obj = new Ubicaciones();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                obj.setCod_ubicacion(map.get("cod_ubicacion").toString());
//                obj.setNom_ubicacion(map.get("nom_ubicacion").toString());
//                listUbicaciones.add(obj);
//            }
//        }
    }

//    public List<Articulos> articulos(String query) {
//        getObjArticulo();
//        System.out.println("------------ Bean articulos------------");
//        System.out.println("Articulo : " + objArticulo.toString());
//
//        listArticulos = SelService.cargaArticulo(query, 2);
//        return listArticulos;
//    }
//    public void cargaArticulo() {
//        System.out.println("Carga Estado : " + objArticulo.toString());
//        ProConteoDT obj = new ProConteoDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
//        if (buscarElemento(obj) == false) {
//            objConteo.getDetalleArt().add(obj);
//        }
//        setObjArticulo(null);
//    }
    public void eliminarArticuloGrilla(ProConteoDT obj) {
        if (buscarElemento(obj)) {
            objConteo.getDetalleCont().remove(obj);
        }

    }
//    public boolean buscarElemento(ConteoDT obj) {
//        boolean valor = false;
//        for (ConteoDT obj2 : objConteo.getDetalleArt()) {
//            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
//                valor = true;
//                break;
//            }
//        }
//        return valor;
//    }

    public ConteoService getCService() {
        return CService;
    }

    public void setCService(ConteoService CService) {
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

    public Conteo getObjConteo() {
        if (objConteo == null) {
            objConteo = new Conteo();
        }
        return objConteo;
    }

    public void setObjConteo(Conteo objConteo) {
        this.objConteo = objConteo;
    }

    public List<Conteo> getListaConteo() {
        return listaConteo;
    }

    public void setListaConteo(List<Conteo> listaConteo) {
        this.listaConteo = listaConteo;
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

//    public ConteoDT getObjConteoDT() {
//        if (objConteoDT == null) {
//            objConteoDT = new ConteoDT();
//        }
//        return objConteoDT;
//    }
//    public void setObjConteoDT(ConteoDT objConteoDT) {
//        this.objConteoDT = objConteoDT;
//    }
    public Articulos getObjArticulo() {
        if (objArticulo == null) {
            objArticulo = new Articulos();
        }
        return objArticulo;
    }

    public void setObjArticulo(Articulos objArticulo) {
        this.objArticulo = objArticulo;
    }
//
//    public List<Articulos> getListArticulos() {
//        return listArticulos;
//    }
//
//    public void setListArticulos(List<Articulos> listArticulos) {
//        this.listArticulos = listArticulos;
//    }
//
//    public List<Ubicaciones> getListUbicaciones() {
//        return listUbicaciones;
//    }
//
//    public void setListUbicaciones(List<Ubicaciones> listUbicaciones) {
//        this.listUbicaciones = listUbicaciones;
//    }

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

    public List<Inventario> getListInventario() {
        return listInventario;
    }

    public void setListInventario(List<Inventario> listInventario) {
        this.listInventario = listInventario;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public List<Depositos> getListDepositos() {
        return listDepositos;
    }

    public void setListDepositos(List<Depositos> listDepositos) {
        this.listDepositos = listDepositos;
    }

    public List<Categoria> getListCategorias() {
        return listCategorias;
    }

    public void setListCategorias(List<Categoria> listCategorias) {
        this.listCategorias = listCategorias;
    }

    public List<SubCategoria> getListSubCategorias() {
        return listSubCategorias;
    }

    public void setListSubCategorias(List<SubCategoria> listSubCategorias) {
        this.listSubCategorias = listSubCategorias;
    }

}
