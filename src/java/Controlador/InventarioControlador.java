package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
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
public class InventarioControlador {

    @Inject
    private InventarioService InventarioService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Inventario objInventario;  
    private List<Inventario> listInventario = new ArrayList();

    private List<Empresa> listEmpresas = new ArrayList();   
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
            getObjInventario();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public InventarioControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listInventario.clear();
        listInventario = InventarioService.Lista();

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
        setObjInventario(null);
        getObjInventario();
        listEmpresas.clear();

        //Numerador
        objInventario.setNro_inventario(ObjIni.numerador_Controlado("Data", "nro_inventario"));

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

        //Variables
        System.out.println("Size Empresa : " + listEmpresas.size());
        objInventario.setCod_emp(listEmpresas.get(0).getCod_emp());

        this.evento = "Nuevo";
        objInventario.setFecha(new Date());
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
        setObjInventario(null);
        controlEventos(evento);
    }

//    public void exportarExcel() throws IOException, JRException {
//
//        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/PruebaExcel.jasper"));
//
//        System.out.println("Mauricio = " + jasper.getPath());
//
//// Ajustar Json
////        List<ReportePrueba> listReporte = new ArrayList();
////
////        for (int i = 0; i < 10; i++) {
////            ReportePrueba r = new ReportePrueba(i, "Nombre" + i, "Apellido" + i, "Direccion" + i, "Telefono" + i);
////            listReporte.add(r);
////        }
////
//        String json = new Gson().toJson("");
//
//        System.out.println(json);
//        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());
//
//        JsonDataSource ds = new JsonDataSource(jsonDataStream);
//
//        JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
//        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        response.addHeader("Content-disposition", "attachment; filename=PruebaExcel.xls");
//
//        ServletOutputStream stream = response.getOutputStream();
//        JRXlsExporter exporter = new JRXlsExporter();
//        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
//        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, stream);
//        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
//        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
//        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
//        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//        exporter.exportReport();
//        stream.flush();
//
//        FacesContext.getCurrentInstance().responseComplete();
//
//    }

    public void prepareCrud(Inventario objecto, int condicion) {
        setObjInventario(null);
        Object Resulta[] = new Object[2];
        Resulta = InventarioService.recuperarInfo(objecto);
        setObjInventario((Inventario) Resulta[0]);
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
        setObjInventario(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = InventarioService.Transaccion(objInventario, "Nuevo");
                    mns = "Inventario Creado Exisitosamente";
                    break;
                case "Eliminar":
                    Resulta = InventarioService.Transaccion(objInventario, "Borrar");
                    mns = "Inventario Eliminado";
                    break;
                case "Editar":
                    Resulta = InventarioService.Transaccion(objInventario, "Editar");
                    mns = "Inventario Editado";
                    break;
            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listInventario.clear();
                    listInventario = (List<Inventario>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjInventario(null);
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
        getObjInventario();
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
        listInventario = InventarioService.ListaBusqueda(valorBusqueda);
    }

    public void limpiarDatos() {
        listInventario.clear();
        listInventario = InventarioService.Lista();
        this.valorBusqueda = "";
    }

    public InventarioService getInventarioService() {
        return InventarioService;
    }

    public void setInventarioService(InventarioService InventarioService) {
        this.InventarioService = InventarioService;
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

    public Inventario getObjInventario() {
        if (objInventario == null) {
            objInventario = new Inventario();
        }
        return objInventario;
    }

    public void setObjInventario(Inventario objInventario) {
        this.objInventario = objInventario;
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
}
