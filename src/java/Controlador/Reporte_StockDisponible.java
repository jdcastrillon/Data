package Controlador;

import Modelo.Bodega.*;
import Modelo.Empresa;
import Modelo.SubCategoria;
import Servicios.Sistema.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class Reporte_StockDisponible implements Serializable {

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private StockDisponible objStock;
    private Articulos objArticulo;
    private List<StockDisponible> list_StockDisponible = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Categoria> listCategoria = new ArrayList();
    private List<SubCategoria> listSubCategoria = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<Estados> ListEstados = new ArrayList();

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
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Reporte_StockDisponible() {
    }

    public void lista(int condicion) {
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

        //Categorias
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_categoria,nom_categoria from m_categoria");
        for (JsonElement jsonElement : Jelementos3) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Categoria cat = new Categoria();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                cat.setCod_categoria(map.get("cod_categoria").toString());
                cat.setNom_categoria(map.get("nom_categoria").toString());

                listCategoria.add(cat);
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

    public void transaccion() {
        System.out.println("Bodega : " + objStock.getDeposito());
        Rep_stockDisponible();

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

    public void Rep_stockDisponible() {
        list_StockDisponible.clear();
        System.out.println("categoria : " + objStock.getCod_categoria());
        System.out.println("Sub categoria : " + objStock.getCod_subcategoria());
        String consulta = "";
        String articulos = "";

        if (objStock.getDeposito().equalsIgnoreCase("0")) {
            consulta = "select COALESCE(C.nom_deposito, 'No Asignado') nom_deposito,D.nom_categoria,COALESCE(E.nom_subcategoria, 'N/A') nom_subcategoria,\n"
                    + "COALESCE( B.cod_estado,'') cod_estado,\n"
                    + "A.codigo,A.nom_articulo,COALESCE(B.cantidad,0) cantidad\n"
                    + "FROM m_articulos A left join s_stkdepositos B on A.cod_articulo=B.cod_articulo\n"
                    + "LEFT JOIN m_depositos C on B.cod_tit=c.cod_deposito \n"
                    + "LEFT JOIN m_categoria D on A.categoria=D.cod_categoria \n"
                    + "LEFT JOIN m_subcategoria E on A.subcategoria=E.cod_subcategoria WHERE ";
        } else {
            consulta = "select COALESCE(C.nom_deposito, 'No Asignado') nom_deposito,D.nom_categoria,COALESCE(E.nom_subcategoria, 'N/A') nom_subcategoria,\n"
                    + "COALESCE( B.cod_estado,'') cod_estado,\n"
                    + "A.codigo,A.nom_articulo,COALESCE(B.cantidad,0) cantidad\n"
                    + "FROM m_articulos A left join s_stkdepositos B on A.cod_articulo=B.cod_articulo\n"
                    + "LEFT JOIN m_depositos C on B.cod_tit=c.cod_deposito \n"
                    + "LEFT JOIN m_categoria D on A.categoria=D.cod_categoria \n"
                    + "LEFT JOIN m_subcategoria E on A.subcategoria=E.cod_subcategoria  WHERE B.cod_tit='" + objStock.getDeposito() + "' AND ";
        }

        if (!objStock.getCod_categoria().equalsIgnoreCase("0")) {
            consulta += " A.categoria='" + objStock.getCod_categoria() + "' AND ";
            System.out.println("Entro condicion de categoria");
        }

        if (!objStock.getCod_subcategoria().equalsIgnoreCase("0")) {
            consulta += " A.subcategoria='" + objStock.getCod_subcategoria() + "' AND ";
            System.out.println("Entro condicion de Sub categoria");
        }

        if (!objStock.getEstado().equalsIgnoreCase("0")) {
            consulta += " B.cod_estado='" + objStock.getEstado() + "' AND ";
            System.out.println("Entro condicion de Sub categoria");
        }

        if (objStock.getListArticulos().size() > 0) {
            for (Articulos articulo : objStock.getListArticulos()) {
                articulos += articulo.getCod_articulo() + ",";
            }
            consulta += "A.cod_articulo in (" + articulos.substring(0, articulos.length() - 1) + ") AND ";
        }

        consulta += "1=1";
        consulta += " order by 1";
        JsonArray Jelementos = ObjIni.listObjectos(consulta);
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                StockDisponible stock = new StockDisponible();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                stock.setDeposito(map.get("nom_deposito").toString());
                stock.setCod_categoria(map.get("nom_categoria").toString());
                stock.setCod_subcategoria(map.get("nom_subcategoria").toString());
                stock.setCod_articulo(map.get("codigo").toString());
                stock.setNom_articulo(map.get("nom_articulo").toString());
                stock.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                stock.setEstado(map.get("cod_estado").toString());
                list_StockDisponible.add(stock);
            }
        }
    }

//    public void Rep_stockDisponiblexBodega() {
//        System.out.println(" Rep_stockDisponiblexBodega ");
//        list_StockDisponible.clear();
//        JsonArray Jelementos = ObjIni.listObjectos("select C.nom_deposito,A.codigo,A.nom_articulo,B.cantidad  from m_articulos A inner join s_stkdepositos B on A.cod_articulo=B.cod_articulo\n"
//                + "INNER JOIN m_depositos C on B.cod_tit=c.cod_deposito WHERE B.cod_tit='" + objStock.getDeposito() + "'");
//        for (JsonElement jsonElement : Jelementos) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                StockDisponible stock = new StockDisponible();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                stock.setDeposito(map.get("nom_deposito").toString());
//                stock.setCod_articulo(map.get("codigo").toString());
//                stock.setNom_articulo(map.get("nom_articulo").toString());
//                stock.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
//                list_StockDisponible.add(stock);
//            }
//        }
//    }
    public void cargarSubCategorias() {
        //SubCategorias
        listSubCategoria.clear();
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_subcategoria,nom_subcategoria from m_subcategoria where cod_categoria='" + objStock.getCod_categoria() + "'");
        for (JsonElement jsonElement2 : Jelementos3) {
            if (!jsonElement2.getAsString().equalsIgnoreCase("No hay Datos")) {
                SubCategoria Scat = new SubCategoria();
                Map<String, Object> map2 = ObjIni.fromJson(jsonElement2);
                Scat.setCod_subcategoria(map2.get("cod_subcategoria").toString());
                Scat.setNom_subcategoria(map2.get("nom_subcategoria").toString());
                listSubCategoria.add(Scat);
            }
        }
    }

//     public void exportarXLS() throws IOException, JRException, SQLException {
//
//            LoginBean log = new LoginBean();
//            Map parametros = new HashMap();
//            int id_empresa = 0;
//            if (log.getIdEmpresa() == 60 && getE() != null) {
//                id_empresa = getE().getId_empresa();
//            } else {
//                id_empresa = log.getIdEmpresa();
//            }
//            parametros.put("idEmpresa", id_empresa);
//            ConexionPool pool = new ConexionPool();
//            pool.con = pool.dataSource.getConnection();
//            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reports/report1.jasper"));
//            JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), parametros, pool.con);
//            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//            response.addHeader("Content-disposition", "attachment; filename=empleados.xls");
//            try (ServletOutputStream stream = response.getOutputStream()) {
//                JRXlsExporter exporter = new JRXlsExporter();
//                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
//                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, stream);
//                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
//                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
//                exporter.exportReport();
//                stream.flush();
//            } catch (JRException e) {
//                System.out.println("error " + e);
//            }
//            FacesContext.getCurrentInstance().responseComplete();
//        
//    }
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

    public List<StockDisponible> getList_StockDisponible() {
        return list_StockDisponible;
    }

    public void setList_StockDisponible(List<StockDisponible> list_StockDisponible) {
        this.list_StockDisponible = list_StockDisponible;
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

    public StockDisponible getObjStock() {
        if (objStock == null) {
            objStock = new StockDisponible();
        }
        return objStock;
    }

    public void setObjStock(StockDisponible objStock) {
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

}
