package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Bodega.Depositos;
import Modelo.Compras.*;
import Modelo.Empresa;
import Servicios.ComprasService;
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
import java.text.NumberFormat;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class ComprasDirectControlador {

    @Inject
    private ComprasService Compra_service;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Compras objCompra;
    private List<Compras> ListCompra = new ArrayList();
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Impuestos> listImpuestos = new ArrayList();
    private List<Proveedores> listProveedor = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Pagos> ListPagos = new ArrayList();
    private UploadedFile uploadedFile;

    private Articulos objArticulo;

    Object acciones[] = new Object[6];
    Object totales[] = new Object[8];
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
            getObjCompra();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
            resetTotales();
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ComprasDirectControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        ListCompra.clear();
        listDepositos.clear();
        ListCompra = Compra_service.Lista("Compra");

        if (condicion == 1) {
            listEmpresas.clear();
            listProveedor.clear();

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

            //Proveedores
            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_provedor,cod_documento,razon_social from m_proveedores");

            for (JsonElement jsonElement : Jelementos2) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Proveedores obj = new Proveedores();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                    obj.setCod_documento(map.get("cod_documento").toString());
                    obj.setRazon_social(map.get("razon_social").toString());
                    listProveedor.add(obj);
                }
            }

        }
    }

    public void prepareNuevo() {
        setObjCompra(null);
        getObjCompra();
        listEmpresas.clear();
        listArticulos.clear();
        listProveedor.clear();
        listImpuestos.clear();
        listDepositos.clear();

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

        //Proveedores
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_provedor,cod_documento,razon_social from m_proveedores");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Proveedores obj = new Proveedores();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                obj.setCod_documento(map.get("cod_documento").toString());
                obj.setRazon_social(map.get("razon_social").toString());
                listProveedor.add(obj);
            }
        }

        //Impuestos
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_impuesto,porc_imp from m_impuestos where cod_impuesto='IVA' and cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");

        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Impuestos obj = new Impuestos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_impuesto(map.get("cod_impuesto").toString());
                obj.setPorc_imp(new BigDecimal(map.get("porc_imp").toString()).intValue());
                listImpuestos.add(obj);
            }
        }
        //Depositos
        JsonArray Jelementos5 = ObjIni.listObjectos("select cod_deposito,nom_deposito from m_depositos where cod_emp='" + listEmpresas.get(0).getCod_emp() + "' and activo='S'");
        for (JsonElement jsonElement : Jelementos5) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Depositos obj = new Depositos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setNom_deposito(map.get("nom_deposito").toString());
                listDepositos.add(obj);
            }
        }

        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        objCompra.setCod_emp(listEmpresas.get(0).getCod_emp());
        objCompra.setPorcentaje(listImpuestos.get(0).getPorc_imp());
        objCompra.setCod_deposito(listDepositos.get(0).getCod_deposito());
        objCompra.setNro_docum(0);
        objCompra.setD_fec_doc(new Date());
        resetTotales();

        this.evento = "Nuevo";
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
        ListCompra.clear();
        this.evento = "Buscar";
        setObjCompra(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Compras objecto, int condicion) {
        setObjCompra(null);
        Object Resulta[] = new Object[2];
        Resulta = Compra_service.recuperarInfo(objecto);
        setObjCompra((Compras) Resulta[0]);
        lista(2);
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
        setObjCompra(null);
        resetTotales();
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        setExecuteReport(false);
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    articulosNuevos();
                    Resulta = Compra_service.Transaccion(objCompra, "Nuevo", "Compra");
                    mns = "O.C Realizada exitosamente";
                    break;
                case "Eliminar":
                    Resulta = Compra_service.Transaccion(objCompra, "Borrar", "Compra");
                    mns = "O.C Eliminada exitosamente";
                    break;
                case "Editar":
                    articulosNuevos();
                    Resulta = Compra_service.Transaccion(objCompra, "Editar", "Compra");
                    mns = "O.C Editado exitosamente";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                lista(2);
                setObjCompra(null);
                getObjCompra();
                if (evento.equalsIgnoreCase("Nuevo")) {
                    objCompra.setTrans((int) Resulta[2]);
                    System.out.println("Trans : " + objCompra.getTrans());
                    setExecuteReport(true);
                }
                this.evento = "inicio";
                controlEventos(evento);

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
        getObjCompra();
        String mns = "";

        if (this.nuevo == false) {
            //Validaciones
            if (objCompra.getCod_provedor() == 0) {
                mns = "Debe Selecionar un Proveedor";
            }
            if (objCompra.getFactura().length() == 0) {
                mns = "Debe Digitar la Factura";
            }
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objCompra.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
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
            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/FactCompra.jasper"));
//            String datos = "[{\"trans\":86,\"cod_emp\":\"Data\",\"nom_emp\":\"DataPyme\",\"fec_doc\":\"2020-05-15\",\"nro_docum\":109,\"cod_estado\":\"Stock\",\"cod_deposito\":\"1\",\"nom_deposito\":\"Bodega Principal\",\"observacion\":\"Sistema\",\"case\":\"Ingreso\",\"cod_articulo\":100,\"nom_articulo\":\"Invictus\",\"cantidad\":100}]";
            String rawJsonData = SelService.ConsultaIreport("select C.nom_emp,C.nit,C.direccion,A.factura,A.fec_doc,D.cod_documento||'-'||D.razon_social proveedor,D.direccion,E.descripcion,\n"
                    + "F.nom_deposito,B.codigonew,B.nom_articulo,B.cantidad,B.imp_costo,B.porc_imp,B.imp_impuesto art_impuesto ,B.imp_neto art_neto,B.imp_total art_total,\n"
                    + "A.imp_neto,A.imp_impuesto,A.imp_total,A.observaciones from t_compras A inner join td_compras B on A.trans=B.trans\n"
                    + "inner join m_empresa C on A.cod_emp=C.cod_emp\n"
                    + "inner join m_proveedores D on A.cod_provedor=D.cod_provedor\n"
                    + "inner join m_pagos E on A.cod_fpago=E.cod_pago\n"
                    + "inner join m_depositos F on A.cod_deposito=F.cod_deposito\n"
                    + "where A.trans="+objCompra.getTrans());
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

    public void reporte() {
        this.executeReport = true;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, SQLException {
        FacesMessage msg = new FacesMessage("Aviso", event.getFile().getFileName() + " se cargo correctamente..!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        uploadedFile = event.getFile();
        cargarArchivo();
    }

    public void cargarArchivo() throws IOException {
        if (uploadedFile != null) {
            objCompra.getComprasDt().clear();
            XSSFWorkbook workbook;
            // Get the workbook instance for XLS file
            InputStream input = uploadedFile.getInputstream();
            workbook = new XSSFWorkbook(input);

            // Get first sheet from the workbook
            System.out.println("numero de hojas =" + workbook.getNumberOfSheets());
            ArrayList<String> lista = new ArrayList();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                // Iterate through each rows from first sheet
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    System.out.println("Row : " + row.getRowNum());
                    if (row.getRowNum() > 0) {
                        StringBuilder sb = new StringBuilder();
                        // For each row, iterate through each columns
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
//                            System.out.println("Tipo :" + cell.getCellType());
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_BOOLEAN:
                                    System.out.print(cell.getBooleanCellValue());
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
//                                System.out.print(Integer.toString((int) cell.getNumericCellValue()));
                                    sb.append(Integer.toString((int) cell.getNumericCellValue())).append(",");
                                    break;
                                case Cell.CELL_TYPE_STRING:
//                                System.out.print(cell.getStringCellValue());
                                    sb.append(cell.getStringCellValue().trim()).append(",");
                                    break;
                            }
                        }
                        if (!sb.toString().equals("")) {
                            lista.add(sb.toString().substring(0, sb.toString().length() - 1));
                        }
                    }

                }
            }
            lista.forEach((next) -> {
                System.out.println("next " + next);
                String datos[] = next.split(",");
                System.out.println("Datos : " + datos[0]);
                ComprasDT obj = new ComprasDT();
                obj.setCodigo(datos[0]);
                obj.setCodigonew(datos[1]);
                obj.setNom_articulo(datos[2]);
                obj.setCantidad(Integer.parseInt(datos[3]));
                obj.setImp_costo(Double.parseDouble(datos[4]));
                obj.setPorc_imp(Float.parseFloat(datos[5]));
                obj.setNuevoCodigo("S");
//                obj.setImp_neto(obj.getCantidad() * obj.getImp_costo());

                if (buscarElementoxCodigo(obj) == false) {
                    objCompra.getComprasDt().add(obj);
                }
            });

            objCompra.getComprasDt().stream().map((comprasDT) -> {
                System.out.println("Objecto" + comprasDT.toString());
                return comprasDT;
            }).map((comprasDT) -> {
                comprasDT.setImp_neto(comprasDT.getImp_costo() * comprasDT.getCantidad());
                return comprasDT;
            }).map((comprasDT) -> {
                comprasDT.setImp_total(comprasDT.getImp_neto() + (comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100)));
                return comprasDT;
            }).forEachOrdered((comprasDT) -> {
                comprasDT.setImp_impuesto(comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100));
            });

            totalesFooter();
            calcularStock();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Debes seleccionar primero un archivo..!"));
        }

    }

    public void calcularStock() {
        System.out.println("*************** " + "calcularStock");
        String cadenaArticulos = "";
        cadenaArticulos = objCompra.getComprasDt().stream().map((comprasDT) -> "'" + comprasDT.getCodigo() + "',").reduce(cadenaArticulos, String::concat);
        String substring = cadenaArticulos.substring(0, cadenaArticulos.length() - 1);
        List<ArticuloDatos> datos = new ArrayList();
        datos = ObjIni.StockDisponibleMultiple(objCompra.getCod_emp(), substring, objCompra.getCod_deposito());
        System.out.println("Datos : " + datos.size());
        for (ArticuloDatos dato : datos) {
            objCompra.getComprasDt().stream().filter((comprasDT) -> (dato.getCodigo().equalsIgnoreCase(comprasDT.getCodigo()))).forEachOrdered((comprasDT) -> {
                comprasDT.setStock(dato.getStock());
                comprasDT.setCod_articulo(dato.getCod_articulo());
                comprasDT.setNuevoCodigo("N");
                comprasDT.setNom_articulo(dato.getNombre());
                System.out.println("---");
            });
        }
    }

    public void articulosNuevos() {
        System.out.println("Articulos Nuevos");
        for (ComprasDT comprasDT : objCompra.getComprasDt()) {
            comprasDT.setCambia_codigo("N");
            if (!comprasDT.getCodigo().equalsIgnoreCase(comprasDT.getCodigonew())) {
                comprasDT.setCambia_codigo("S");
                comprasDT.setCod_articulo2(ObjIni.codigo_articulo_nuevo(comprasDT.getCodigonew()));
            }
            if (comprasDT.getNuevoCodigo().equalsIgnoreCase("S")) {
                System.out.println("Articulo con nuevo codigo");
                comprasDT.setCambia_codigo("S");
                comprasDT.setCod_articulo2(ObjIni.codigo_articulo_nuevo(comprasDT.getCodigonew()));
                comprasDT.setCod_articulo(comprasDT.getCod_articulo2());
            }
        }
    }

    public void cargarPagos() {
        System.out.println("Cargando cargarDepositos " + objCompra.getCod_provedor());
        ListPagos.clear();
        //Pagos
        JsonArray Jelementos3 = ObjIni.listObjectos("select * from(SELECT A.cod_pago, descripcion ,1 linea FROM public.m_pagos A INNER JOIN \n"
                + "m_proveedores B on A.cod_pago=B.cod_fpago\n"
                + "where A.activo='S' and B.cod_provedor=" + objCompra.getCod_provedor() + " union\n"
                + "SELECT A.cod_pago, descripcion , 2 linea FROM public.m_pagos A INNER JOIN \n"
                + "m_proveedores B on A.cod_pago=B.cod_fpago\n"
                + "where A.activo='S' and B.cod_provedor<>" + objCompra.getCod_provedor() + ")X order by 3");
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                Pagos obj = new Pagos(new BigDecimal(map.get("cod_pago").toString()).intValue(),
                        map.get("descripcion").toString());
                ListPagos.add(obj);
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
        Object Resulta[] = new Object[3];
        ComprasDT obj = new ComprasDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
        Resulta = ObjIni.Compras_Art(objCompra.getCod_emp(), objArticulo.getCod_articulo(), "S", objCompra.getCod_deposito(), objCompra.getCod_provedor());
        obj.setCodigonew(obj.getCodigo());
        obj.setPorc_imp((int) Resulta[2]);
        obj.setStock((int) Resulta[0]);
        obj.setImp_costo((double) Resulta[1]);
        obj.setNuevoCodigo("N");

        if (buscarElemento(obj) == false) {
            objCompra.getComprasDt().add(0, obj);
        }
        setObjArticulo(null);
        totalesFooter();
    }

    public void eliminarArticuloGrilla(ComprasDT obj) {
        if (buscarElemento(obj)) {
            objCompra.getComprasDt().remove(obj);
        }

    }

    public boolean buscarElemento(ComprasDT obj) {
        boolean valor = false;
        for (ComprasDT obj2 : objCompra.getComprasDt()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public boolean buscarElementoxCodigo(ComprasDT obj) {
        System.out.println("->>  " + obj.toString());
        boolean valor = false;
        for (ComprasDT obj2 : objCompra.getComprasDt()) {
            if (obj.getCodigo().equalsIgnoreCase(obj2.getCodigo())) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void totalesGrilla(ComprasDT obj) {
        System.out.println("TotalGrilla");
        System.out.println("Impuesto IVA " + obj.getPorc_imp());
        for (ComprasDT comprasDT : objCompra.getComprasDt()) {
            if (obj.getCod_articulo() == comprasDT.getCod_articulo()) {
                System.out.println("Objecto" + comprasDT.toString());
                comprasDT.setImp_neto(comprasDT.getImp_costo() * comprasDT.getCantidad());
                comprasDT.setImp_total(comprasDT.getImp_neto() + (comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100)));
                comprasDT.setImp_impuesto(comprasDT.getImp_neto() * (comprasDT.getPorc_imp() / 100));
                System.out.println("Neto : " + comprasDT.getImp_neto());
                System.out.println("Neto : " + comprasDT.getPorc_imp());
                break;
            }
        }
        totalesFooter();
    }

    public void totalesFooter() {
        System.out.println("******************** TOTALES FOOTER *****************************");
        int stock = 0;
        double costo = 0;
        int cantidad = 0;
        double neto = 0;
        double total = 0;
        double impuesto = 0;
        for (ComprasDT comprasDT : objCompra.getComprasDt()) {
//            stock = stock + comprasDT.getStock();
            costo = costo + (comprasDT.getImp_costo());
            cantidad = cantidad + comprasDT.getCantidad();
            neto = neto + comprasDT.getImp_neto();
            total = total + comprasDT.getImp_total();
            impuesto = impuesto + comprasDT.getImp_impuesto();
        }
//        totales[0] = stock;
//        totales[1] = formatter.format(costo);
//        totales[2] = cantidad;
        totales[3] = formatter.format(neto);
        totales[4] = formatter.format(total);
//        totales[5] = formatter.format(costo * cantidad);
        totales[6] = formatter.format(impuesto);

        objCompra.setImp_neto(neto);
        objCompra.setImp_impuesto(impuesto);
        objCompra.setImp_total(total);

    }

    public void nombre_articulo(int cod_articulo, String codigo) {
        String nombre = "";
        boolean existeCodigo = false;
        JsonArray Jelementos = ObjIni.listObjectos("select nom_articulo from m_articulos where codigo='" + codigo + "'");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                nombre = (map.get("nom_articulo").toString());
                existeCodigo = true;
            }
        }
        System.out.println("Codigo : " + cod_articulo);
        System.out.println("Nom_articulo : " + nombre);
        if (existeCodigo) {
            for (ComprasDT compraDT : objCompra.getComprasDt()) {
                System.out.println(compraDT.getCod_articulo() + "=" + cod_articulo);
                if (compraDT.getCod_articulo() == cod_articulo) {
                    compraDT.setNom_articulo(nombre);
                    System.out.println("Actualizo ");
                    break;
                }
            }
        } else {
            System.out.println("Articulo Nuevo");
            //Se marca la opcion de nuevo codigo , para pintar la fila
            for (ComprasDT compraDT : objCompra.getComprasDt()) {
                System.out.println(compraDT.getCod_articulo() + "=" + cod_articulo);
                if (compraDT.getCod_articulo() == cod_articulo) {
                    compraDT.setNuevoCodigo("S");
                    break;
                }
            }
        }
    }

    public void resetTotales() {
        //Totales
        totales[0] = 0;
        totales[1] = 0;
        totales[2] = 0;
        totales[3] = 0;
        totales[4] = 0;
        totales[5] = 0;
        totales[6] = 0;
    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listArticulos.clear();
        ListCompra = Compra_service.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        ListCompra.clear();
        ListCompra = Compra_service.Lista("Compra");
        this.valorBusqueda = "";
    }

    public void datosBean() {
        System.out.println("Datos Bean signo " + objCompra.getCod_deposito());
    }

    public ComprasService getCompra_service() {
        return Compra_service;
    }

    public void setCompra_service(ComprasService Compra_service) {
        this.Compra_service = Compra_service;
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

    public Compras getObjCompra() {
        if (objCompra == null) {
            objCompra = new Compras();
        }
        return objCompra;
    }

    public void setObjCompra(Compras objCompra) {
        this.objCompra = objCompra;
    }

    public List<Compras> getListCompra() {
        return ListCompra;
    }

    public void setListCompra(List<Compras> ListCompra) {
        this.ListCompra = ListCompra;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
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

    public List<Proveedores> getListProveedor() {
        return listProveedor;
    }

    public void setListProveedor(List<Proveedores> listProveedor) {
        this.listProveedor = listProveedor;
    }

    public Object[] getTotales() {
        return totales;
    }

    public void setTotales(Object[] totales) {
        this.totales = totales;
    }

    public List<Pagos> getListPagos() {
        return ListPagos;
    }

    public void setListPagos(List<Pagos> ListPagos) {
        this.ListPagos = ListPagos;
    }

    public NumberFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
    }

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

    public List<Impuestos> getListImpuestos() {
        return listImpuestos;
    }

    public void setListImpuestos(List<Impuestos> listImpuestos) {
        this.listImpuestos = listImpuestos;
    }

    public List<Depositos> getListDepositos() {
        return listDepositos;
    }

    public void setListDepositos(List<Depositos> listDepositos) {
        this.listDepositos = listDepositos;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public boolean isExecuteReport() {
        return executeReport;
    }

    public void setExecuteReport(boolean executeReport) {
        this.executeReport = executeReport;
    }

}
