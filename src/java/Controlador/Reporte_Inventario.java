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
public class Reporte_Inventario implements Serializable {

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    @Inject
    private Seleccion SelService;

    private ReporteInventario objInventario;
    private Articulos objArticulo;
    private List<ReporteInventario> list_ReporteInventario = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Inventario> listInventario = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Categoria> listCategoria = new ArrayList();
    private List<SubCategoria> listSubCategoria = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();

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

    public Reporte_Inventario() {
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

        //Inventarios
        JsonArray Jelementos4 = ObjIni.listObjectos("select nro_inventario,observacion from t_inventario where cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos4) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Inventario inv = new Inventario();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                inv.setNro_inventario(new BigDecimal(map.get("nro_inventario").toString()).intValue());
                inv.setObservacion(map.get("observacion").toString());

                listInventario.add(inv);
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
        System.out.println("Bodega : " + objInventario.getDeposito());
        Re_Inventario();

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

    public void Re_Inventario() {
        list_ReporteInventario.clear();
        System.out.println("categoria : " + objInventario.getCod_categoria());
        System.out.println("Sub categoria : " + objInventario.getCod_subcategoria());
        String consulta = "";
        String articulos = "";

//        if (objInventario.getDeposito().equalsIgnoreCase("0")) {
        consulta = " select A.nro_inventario,E.nom_deposito,B.nro_proceso,A.fec_doc,C.cod_articulo,C.nom_articulo,C.stock,C.cantidad,C.ajuste from t_inventario A inner join t_pro_datos B\n"
                + " on A.nro_inventario=B.nro_inventario\n"
                + " inner join td_pro_datos C on B.nro_proceso=C.nro_proceso\n"
                + " inner join t_pro_conteo D on B.nro_conteo=D.nro_conteo\n"
                + " inner join m_depositos E on D.cod_deposito=E.cod_deposito\n"
                + " inner join m_articulos F on F.codigo=cast(C.cod_articulo as varchar(12))\n"
                + " left join m_categoria G on F.categoria=G.cod_categoria\n"
                + " left join m_subcategoria H on F.subcategoria=H.cod_subcategoria \n"
                + " WHERE A.cod_emp='" + objInventario.getCod_emp() + "' AND ";
//        } else {
//            consulta = "select COALESCE(C.nom_deposito, 'No Asignado') nom_deposito,D.nom_categoria,COALESCE(E.nom_subcategoria, 'N/A') nom_subcategoria,\n"
//                    + "COALESCE( B.cod_estado,'') cod_estado,\n"
//                    + "A.codigo,A.nom_articulo,COALESCE(B.cantidad,0) cantidad\n"
//                    + "FROM m_articulos A left join s_stkdepositos B on A.cod_articulo=B.cod_articulo\n"
//                    + "LEFT JOIN m_depositos C on B.cod_tit=c.cod_deposito \n"
//                    + "LEFT JOIN m_categoria D on A.categoria=D.cod_categoria \n"
//                    + "LEFT JOIN m_subcategoria E on A.subcategoria=E.cod_subcategoria  WHERE B.cod_tit='" + objInventario.getDeposito() + "' AND ";
//        }

        if (objInventario.getNro_inventario() != 0) {
            consulta += " A.nro_inventario=" + objInventario.getNro_inventario() + " AND ";
            System.out.println("Entro condicion de inventario");
        }

        if (!objInventario.getEstado().equalsIgnoreCase("0")) {
            consulta += " A.estado_inventario='" + objInventario.getEstado() + "' AND ";
            System.out.println("Entro condicion de Estado");
        }

        if (objInventario.getDeposito().equalsIgnoreCase("0")) {
            consulta += " D.cod_deposito='" + objInventario.getDeposito() + "' AND ";
            System.out.println("Entro condicion de inventario");
        }

        if (!objInventario.getCod_categoria().equalsIgnoreCase("0")) {
            consulta += " F.categoria='" + objInventario.getCod_categoria() + "' AND ";
            System.out.println("Entro condicion de Categoria");
        }

        if (!objInventario.getCod_subcategoria().equalsIgnoreCase("0")) {
            consulta += " F.subcategoria='" + objInventario.getCod_subcategoria() + "' AND ";
            System.out.println("Entro condicion de Categoria");
        }

        if (objInventario.getListArticulos().size() > 0) {
            for (Articulos articulo : objInventario.getListArticulos()) {
                articulos += articulo.getCod_articulo() + ",";
            }
            consulta += "C.cod_articulo in (" + articulos.substring(0, articulos.length() - 1) + ") AND ";
        }

        consulta += "1=1";
        consulta += " order by 1";
        JsonArray Jelementos = ObjIni.listObjectos(consulta);
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                ReporteInventario stock = new ReporteInventario();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                stock.setNro_inventario(new BigDecimal(map.get("nro_inventario").toString()).intValue());
                stock.setDeposito(map.get("nom_categoria").toString());
                stock.setNro_proceso(new BigDecimal(map.get("cantidad").toString()).intValue());
                stock.setFec_doc(map.get("codigo").toString());
                stock.setCod_articulo(map.get("codigo").toString());
                stock.setNom_articulo(map.get("nom_articulo").toString());
                stock.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                stock.setConteo(new BigDecimal(map.get("cantidad").toString()).intValue());
                stock.setAjuste(new BigDecimal(map.get("cantidad").toString()).intValue());
//                stock.setEstado(map.get("cod_estado").toString());
                list_ReporteInventario.add(stock);
            }
        }
    }

    public void cargarSubCategorias() {
        //SubCategorias
        listSubCategoria.clear();
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_subcategoria,nom_subcategoria from m_subcategoria where cod_categoria='" + objInventario.getCod_categoria() + "'");
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
            objInventario.getListArticulos().add(obj);
        }
        setObjArticulo(null);
    }

    public void eliminarArticuloGrilla(Articulos obj) {
        if (buscarElemento(obj)) {
            objInventario.getListArticulos().remove(obj);
        }

    }

    public boolean buscarElemento(Articulos obj) {
        boolean valor = false;
        for (Articulos obj2 : objInventario.getListArticulos()) {
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

    public ReporteInventario getObjInventario() {
        if (objInventario == null) {
            objInventario = new ReporteInventario();
        }
        return objInventario;
    }

    public void setObjInventario(ReporteInventario objInventario) {
        this.objInventario = objInventario;
    }

    public Articulos getObjArticulo() {
        return objArticulo;
    }

    public void setObjArticulo(Articulos objArticulo) {
        this.objArticulo = objArticulo;
    }

    public List<ReporteInventario> getList_ReporteInventario() {
        return list_ReporteInventario;
    }

    public void setList_ReporteInventario(List<ReporteInventario> list_ReporteInventario) {
        this.list_ReporteInventario = list_ReporteInventario;
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

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
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

    public List<Inventario> getListInventario() {
        return listInventario;
    }

    public void setListInventario(List<Inventario> listInventario) {
        this.listInventario = listInventario;
    }

}
