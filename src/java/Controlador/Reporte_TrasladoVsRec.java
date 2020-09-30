package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Modelo.SubCategoria;
import Servicios.Sistema.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
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
import java.util.Calendar;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class Reporte_TrasladoVsRec implements Serializable {

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private Stock_Traslados objStock;
    private Articulos objArticulo;
    private List<Stock_Traslados> list_Stock_Traslados = new ArrayList();
    private List<Stock_Traslados> listDT_Stock_Traslados = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Categoria> listCategoria = new ArrayList();
    private List<SubCategoria> listSubCategoria = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Estados> ListEstados = new ArrayList();

    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
            getObjStock();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Reporte_TrasladoVsRec() {
    }

    public void lista(int condicion) {
        //Empresas
        listEmpresas.clear();
        listDepositos.clear();
        ListEstados.clear();
        getObjStock();

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
        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_estado,nom_estado from m_estados where cod_categoria='Stock' and cod_deposito='Deposito' and activo='S'");
        for (JsonElement jsonElement : Jelementos4) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Estados obj = new Estados();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                obj.setCod_estado(map.get("cod_estado").toString());
                obj.setNom_estado(map.get("nom_estado").toString());
                ListEstados.add(obj);
            }
        }

        objStock.setFec_ini(new Date());
        objStock.setFec_fin(new Date());
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void cancelarEventos() {
        if (this.evento.equalsIgnoreCase("Buscar")) {
            lista(2);
        }
        this.evento = "inicio";
        controlEventos(evento);
//        setObjAjuste(null);
    }

    public void transaccion() throws ParseException {
        System.out.println("Bodega : " + objStock.getDeposito());
        Rep_Traslados();
        Rep_Traslados_Detallado();

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

        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objAjuste.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void Rep_Traslados() throws ParseException {
        list_Stock_Traslados.clear();
        System.out.println("categoria : " + objStock.getCod_categoria());
        System.out.println("Sub categoria : " + objStock.getCod_subcategoria());
        String consulta = "";
        String articulos = "";

        consulta = "select A.cod_deposito,A.cod_deposito2,C.nom_deposito,D.nom_deposito as nom_deposito2,A.nro_docum,A.fec_doc,A.cant_enviada,\n"
                + "COALESCE(B.fec_Doc,'1990-01-01') as fec_doc2,COALESCE(B.cant_recibida,0) cant_recibida,COALESCE((A.cant_enviada-B.cant_recibida),0) pendientes from (\n"
                + "select A.cod_emp,A.fec_doc,A.nro_docum,A.cod_deposito,A.cod_deposito2,sum(cant_enviada) cant_enviada from t_trasladobodega A INNER JOIN \n"
                + "td_trasladobodega D on A.trans=D.trans\n"
                + "group by A.cod_emp,A.fec_doc,A.nro_docum,A.cod_deposito,A.cod_deposito2)A\n"
                + "LEFT JOIN \n"
                + "(select A.cod_emp,A.fec_doc,A.nro_docum,sum(cant_recibida) cant_recibida from t_receptraslado A INNER JOIN \n"
                + "td_receptraslado D on A.trans=D.trans\n"
                + "group by A.cod_emp,A.fec_doc,A.nro_docum,A.cod_deposito,A.cod_deposito2)B\n"
                + "on A.cod_emp=B.cod_emp and A.nro_docum=B.nro_docum\n"
                + "INNER JOIN \n"
                + "m_depositos C on A.cod_deposito=C.cod_deposito INNER JOIN \n"
                + "m_depositos D on A.cod_deposito2=D.cod_deposito WHERE ";

        if (objStock.getAplicaFechas().equalsIgnoreCase("S")) {

            objStock.setFec_ini2(dateFormat.format(objStock.getFec_ini()));
            objStock.setFec_fin2(dateFormat.format(objStock.getFec_fin()));

            consulta += " A.fec_doc between '" + objStock.getFec_ini2() + "' AND '" + objStock.getFec_fin2() + "' AND ";
        }

        if (!objStock.getDeposito().equalsIgnoreCase("0")) {
            consulta += " A.cod_deposito='" + objStock.getDeposito() + "' AND ";
        }

        if (!objStock.getDeposito2().equalsIgnoreCase("0")) {
            consulta += " A.cod_deposito2='" + objStock.getDeposito2() + "' AND ";
        }

        if (objStock.getListArticulos().size() > 0) {
            for (Articulos articulo : objStock.getListArticulos()) {
                articulos += articulo.getCod_articulo() + ",";
            }
            consulta += " A.nro_docum in (select nro_Docum from t_trasladobodega A INNER JOIN \n"
                    + "td_trasladobodega D on A.trans=D.trans where nro_docum=A.nro_docum\n"
                    + "and D.cod_articulo in (" + articulos.substring(0, articulos.length() - 1) + ")) AND ";
        }
        consulta += "1=1";
        consulta += " order by 1";
        JsonArray Jelementos = ObjIni.listObjectos(consulta);
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Stock_Traslados stock = new Stock_Traslados();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("***************************");
                stock.setDeposito(map.get("nom_deposito").toString());
                stock.setDeposito2(map.get("nom_deposito2").toString());
                stock.setNro_traslado(new BigDecimal(map.get("nro_docum").toString()).intValue());
                stock.setFec_ini2(map.get("fec_doc").toString());
                stock.setCant_enviada(new BigDecimal(map.get("cant_enviada").toString()).intValue());
                stock.setFec_fin2(map.get("fec_doc2").toString().equalsIgnoreCase("1990-01-01") ? "" : map.get("fec_doc").toString());
                stock.setCant_recibidad(new BigDecimal(map.get("cant_recibida").toString()).intValue());
                stock.setPendientes(new BigDecimal(map.get("pendientes").toString()).intValue());

//                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(objStock.getFec_ini2());
//                stock.setFec_ini(date1);
//
//                Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(objStock.getFec_fin2());
//                stock.setFec_fin(date2);
                list_Stock_Traslados.add(stock);
            }
        }
    }

    public void Rep_Traslados_Detallado() throws ParseException {
        listDT_Stock_Traslados.clear();
        System.out.println("categoria : " + objStock.getCod_categoria());
        System.out.println("Sub categoria : " + objStock.getCod_subcategoria());
        String consulta = "";
        String nro_traslados = "";
        String articulos = "";

        if (list_Stock_Traslados.size() > 0) {

//        if (objStock.getDeposito().equalsIgnoreCase("0")) {
            consulta = "select C.nom_deposito,D.nom_deposito as nom_deposito2,A.nro_docum,A.fec_doc,A.codigo,A.nom_articulo,A.cod_unidad,A.cant_enviada,\n"
                    + "COALESCE(B.fec_Doc,'1990-01-01') as fec_doc2,COALESCE(B.cant_recibida,0) cant_recibida,COALESCE((A.cant_enviada-B.cant_recibida),0) Pendientes from (\n"
                    + "select A.cod_emp,A.fec_doc,A.nro_docum,A.cod_deposito,A.cod_deposito2,C.codigo,C.nom_articulo,D.cod_unidad,sum(cant_enviada) cant_enviada from t_trasladobodega A INNER JOIN \n"
                    + "td_trasladobodega D on A.trans=D.trans\n"
                    + "inner join m_articulos C on D.cod_articulo=C.cod_articulo\n"
                    + "group by A.cod_emp,A.fec_doc,A.nro_docum,A.cod_deposito,A.cod_deposito2,C.codigo,C.nom_articulo,D.cod_unidad)A\n"
                    + "LEFT JOIN \n"
                    + "(select A.cod_emp,A.fec_doc,A.nro_docum,C.codigo,C.nom_articulo,sum(cant_recibida) cant_recibida from t_receptraslado A INNER JOIN \n"
                    + "td_receptraslado D on A.trans=D.trans\n"
                    + "inner join m_articulos C on D.cod_articulo=C.cod_articulo\n"
                    + "group by A.cod_emp,A.fec_doc,A.nro_docum,C.codigo,C.nom_articulo)B\n"
                    + "on A.cod_emp=B.cod_emp and A.nro_docum=B.nro_docum and \n"
                    + "A.codigo=B.codigo\n"
                    + "INNER JOIN \n"
                    + "m_depositos C on A.cod_deposito=C.cod_deposito INNER JOIN \n"
                    + "m_depositos D on A.cod_deposito2=D.cod_deposito WHERE ";

            //Filtro por Numero de Traslado
            for (Stock_Traslados traslado : list_Stock_Traslados) {
                nro_traslados += traslado.getNro_traslado() + ",";
            }
            consulta += "A.nro_docum in (" + nro_traslados.substring(0, nro_traslados.length() - 1) + ") AND ";

            if (objStock.getListArticulos().size() > 0) {
                for (Articulos articulo : objStock.getListArticulos()) {
                    articulos += "'" + articulo.getCodigo() + "',";
                }
                consulta += " A.codigo in (" + articulos.substring(0, articulos.length() - 1) + ") AND ";
            }

            consulta += "1=1 ";
            consulta += " order by 3,5";
            JsonArray Jelementos = ObjIni.listObjectos(consulta);
            for (JsonElement jsonElement : Jelementos) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Stock_Traslados stock = new Stock_Traslados();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    System.out.println("***************************");
                    stock.setDeposito(map.get("nom_deposito").toString());
                    stock.setDeposito2(map.get("nom_deposito2").toString());
                    stock.setNro_traslado(new BigDecimal(map.get("nro_docum").toString()).intValue());
                    stock.setFec_ini2(map.get("fec_doc").toString());
                    stock.setCod_articulo(map.get("codigo").toString());
                    stock.setNom_articulo(map.get("nom_articulo").toString());
                    stock.setCod_unidad(map.get("cod_unidad").toString());
                    stock.setCant_enviada(new BigDecimal(map.get("cant_enviada").toString()).intValue());
                    stock.setFec_fin2(map.get("fec_doc2").toString().equalsIgnoreCase("1990-01-01") ? "" : map.get("fec_doc").toString());
                    stock.setCant_recibidad(new BigDecimal(map.get("cant_recibida").toString()).intValue());
                    stock.setPendientes(new BigDecimal(map.get("pendientes").toString()).intValue());

                    listDT_Stock_Traslados.add(stock);
                }
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
        Articulos obj = new Articulos(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo());
        if (buscarElemento(obj) == false) {
            objStock.getListArticulos().add(obj);
        }
        setObjArticulo(null);
    }

    public void eliminarArticuloGrilla(Articulos obj) {
        if (buscarElemento(obj)) {
            objStock.getListArticulos().remove(obj);
        }

    }

    public boolean buscarElemento(Articulos obj) {
        boolean valor = false;
        for (Articulos obj2 : objStock.getListArticulos()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
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

    public Stock_Traslados getObjStock() {
        if (objStock == null) {
            objStock = new Stock_Traslados();
        }
        return objStock;
    }

    public void setObjStock(Stock_Traslados objStock) {
        this.objStock = objStock;
    }

    public List<Categoria> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<Categoria> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public List<SubCategoria> getListSubCategoria() {
        return listSubCategoria;
    }

    public void setListSubCategoria(List<SubCategoria> listSubCategoria) {
        this.listSubCategoria = listSubCategoria;
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

    public List<Estados> getListEstados() {
        return ListEstados;
    }

    public void setListEstados(List<Estados> ListEstados) {
        this.ListEstados = ListEstados;
    }

    public List<Stock_Traslados> getList_Stock_Traslados() {
        return list_Stock_Traslados;
    }

    public void setList_Stock_Traslados(List<Stock_Traslados> list_Stock_Traslados) {
        this.list_Stock_Traslados = list_Stock_Traslados;
    }

    public List<Stock_Traslados> getListDT_Stock_Traslados() {
        return listDT_Stock_Traslados;
    }

    public void setListDT_Stock_Traslados(List<Stock_Traslados> listDT_Stock_Traslados) {
        this.listDT_Stock_Traslados = listDT_Stock_Traslados;
    }

}
