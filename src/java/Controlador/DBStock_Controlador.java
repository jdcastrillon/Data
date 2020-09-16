package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Bodega.DashboardSK_Depostio;
import Modelo.Bodega.Dashboard_stock;
import Modelo.Bodega.Depositos;
import Modelo.Empresa;
import Modelo.Bodega.Estados;
import Servicios.BDStock_Service;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class DBStock_Controlador {

    @Inject
    private BDStock_Service BDStock_Service;

    @Inject
    private Inicializacion ObjIni;
//
//    @Inject
//    private Validaciones ObjVal;
//
    @Inject
    private Seleccion SelService;
    
    private Dashboard_stock obj_Board;
    private Articulos objArticulo;
    private List<DashboardSK_Depostio> depostiosGeneral = new ArrayList();
    private List<DashboardSK_Depostio> depositoxestado = new ArrayList();
    private List<DashboardSK_Depostio> detalle = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Estados> listEstados = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();

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

    @PostConstruct
    public void init() {
        try {
            getObj_Board();
            getObjArticulo();
            lista();
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DBStock_Controlador() {
    }

    public void lista() {
        depostiosGeneral.clear();
        depositoxestado.clear();
        detalle.clear();
        listEmpresas.clear();
        listDepositos.clear();
        listEstados.clear();

        depostiosGeneral = BDStock_Service.Consulta(obj_Board);
        depositoxestado = BDStock_Service.DepositoXEstado(obj_Board);
        detalle = BDStock_Service.DetalleArticulo(obj_Board);
        obj_Board.setStock(BDStock_Service.Stock(obj_Board));

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

    }

    public void transaccion() {

    }
    
     public List<Articulos> articulos(String query) {
        System.out.println("------------ Bean articulos------------");
        System.out.println("Articulo : " + objArticulo.toString());
        listArticulos = SelService.cargaArticulo(query, 2);
        return listArticulos;
    }

    public void cancelarEventos() {
        setObj_Board(null);
    }

    public BDStock_Service getBDStock_Service() {
        return BDStock_Service;
    }

    public void setBDStock_Service(BDStock_Service BDStock_Service) {
        this.BDStock_Service = BDStock_Service;
    }

    public Dashboard_stock getObj_Board() {
        if (obj_Board == null) {
            obj_Board = new Dashboard_stock();
        }
        return obj_Board;
    }

    public void setObj_Board(Dashboard_stock obj_Board) {
        this.obj_Board = obj_Board;
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

    public List<DashboardSK_Depostio> getDepostiosGeneral() {
        return depostiosGeneral;
    }

    public void setDepostiosGeneral(List<DashboardSK_Depostio> depostiosGeneral) {
        this.depostiosGeneral = depostiosGeneral;
    }

    public List<DashboardSK_Depostio> getDepositoxestado() {
        return depositoxestado;
    }

    public void setDepositoxestado(List<DashboardSK_Depostio> depositoxestado) {
        this.depositoxestado = depositoxestado;
    }

    public List<DashboardSK_Depostio> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DashboardSK_Depostio> detalle) {
        this.detalle = detalle;
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

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Articulos getObjArticulo() {
        if(objArticulo==null){
            objArticulo=new Articulos();
        }
        return objArticulo;
    }

    public void setObjArticulo(Articulos objArticulo) {
        this.objArticulo = objArticulo;
    }

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

}
