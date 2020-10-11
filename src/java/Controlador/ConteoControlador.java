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
    private Inventario objInvetario;
    private List<Conteo> listaConteo = new ArrayList();
    private List<Inventario> listInventario = new ArrayList();
    private List<Empresa> listEmpresas = new ArrayList();
    private List<Depositos> listDepositos = new ArrayList();
    private List<Categoria> listCategorias = new ArrayList();
    private List<SubCategoria> listSubCategorias = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();

    private Articulos objArticulo;

    Object acciones[] = new Object[6];
    Object titulos_reporte[] = new Object[8];
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
    }

    public void prepareNuevo() {
        setObjConteo(null);
        getObjConteo();

        listInventario.clear();
        listEmpresas.clear();
        listDepositos.clear();
        listCategorias.clear();
        listSubCategorias.clear();
        listArticulos.clear();

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
        JsonArray Jelementos2 = ObjIni.listObjectos("select nro_inventario,cod_emp,fec_doc from t_inventario where cod_emp='" + listEmpresas.get(0).getCod_emp() + "'");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Inventario obj = new Inventario();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);

                obj.setNro_inventario((int) Double.parseDouble(map.get("nro_inventario").toString()));
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setFec_doc(map.get("fec_doc").toString());
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
        objConteo.setCod_emp(listEmpresas.get(0).getCod_emp());
        objConteo.setCod_deposito(listDepositos.get(0).getCod_deposito());
        this.evento = "Nuevo";
        objConteo.setFecha(new Date());
        controlEventos(evento);
        titulos_reporte = null;
        getTitulos_reporte();
        System.out.println("Inventarios : " + listInventario.size());
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

    public void exportarExcel(boolean reporte) throws IOException, JRException {

        if (reporte) {
            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/ProcesoConteo.jasper"));

            String consulta = "select 'Inventario '||D.nom_emp as titulo,'"+titulos_reporte[2]+"' as tit_inventario,'"+titulos_reporte[3]+"' as tit_descarga, \n"
                    + "'Bodega : '||A.cod_tit as tit_bodega,C.nom_deposito as tit_nombodega,\n"
                    + "'"+titulos_reporte[4]+"' as tit_categoria,'"+titulos_reporte[5]+"' as tit_nomcategoria,\n"
                    + "'"+titulos_reporte[6]+"' as tit_subcategoria,'"+titulos_reporte[7]+"' as tit_nomsubcate,\n"
                    + "A.cod_tit,\n"
                    + "B.categoria,B.subcategoria,\n"
                    + "B.codigo as cod_articulo,B.nom_articulo,A.cantidad,0 as cantidad2 \n"
                    + "from s_stkdepositos A INNER JOIN m_articulos B on A.cod_Articulo=B.cod_Articulo\n"
                    + "left join m_depositos C on A.cod_tit=C.cod_deposito\n"
                    + "left join m_empresa D on A.cod_emp=D.cod_emp\n"
                    + "left join m_categoria E on B.categoria=E.cod_Categoria\n"
                    + "left join m_subcategoria F on B.subcategoria=F.cod_subcategoria\n"
                    + "where A.cod_emp='"+objConteo.getCod_emp()+"' and cod_estado='Disponible'"
                    + " and A.cod_tit='"+objConteo.getCod_deposito()+"'";

            //Condiciones
//            if (!objConteo.getCod_deposito().equalsIgnoreCase("0")) {
//                consulta += " A.cod_tit='" + objConteo.getCod_deposito() + "'";
//            }
//
//            if (!objConteo.getCod_categoria().equalsIgnoreCase("0")) {
//
//            }
            String String_json = SelService.ConsultaIreport(consulta);
            System.out.println("" + String_json.replace("\\", "").replace("\"[", "[").replace("]\"", "]"));

            System.out.println(String_json);
            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(String_json.replace("\\", "").replace("\"[", "[").replace("]\"", "]").getBytes());

            JsonDataSource ds = new JsonDataSource(jsonDataStream);

            JasperPrint jp = JasperFillManager.fillReport(jasper.getPath(), null, ds);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.addHeader("Content-disposition", "attachment; filename=Inventario.xls");

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

            setObjConteo(null);
            setExecuteReport(false);

            FacesContext.getCurrentInstance().responseComplete();
        }

    }

    public void prepareCrud(Conteo objecto, int condicion) {
        setObjConteo(null);
        Object Resulta[] = new Object[2];
        Resulta = CService.recuperarInfo(objecto);
        setObjConteo((Conteo) Resulta[0]);
//        setTituloReporte((String) Resulta[1]);
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
        setObjConteo(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[8];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    objConteo.setNom_Categoria("TODOS");
                    objConteo.setNom_subcategoria("TODOS");
                    Resulta = CService.Transaccion(objConteo, "Nuevo");
                    mns = "Conteo Creado Cargando Excel";
                    break;
                case "Eliminar":
                    Resulta = CService.Transaccion(objConteo, "Borrar");
                    mns = "Conteo Eliminado";
                    break;
                case "Editar":
                    Resulta = CService.Transaccion(objConteo, "Editar");
                    mns = "Conteo Editado";

                    mns = "Reporte";
                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listaConteo.clear();
                    listaConteo = (List<Conteo>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);

                    if (evento.equalsIgnoreCase("Nuevo")) {
                        titulos_reporte = Resulta;
                        setExecuteReport(true);
                    }
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
        System.out.println("objConteo " + objConteo);
        if (this.nuevo == false) {
            if (objConteo.getCod_deposito().equals("0")) {
                mns = "Debe seleccionar una bodega";
            }
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

    public void eliminarArticuloGrilla(ProConteoDT obj) {
        if (buscarElemento(obj)) {
            objConteo.getDetalleCont().remove(obj);
        }

    }

    public void cargarNroInventario() {
        getObjInvetario();
        System.out.println("Orden de compra : " + objInvetario.toString());
        objConteo.setCod_emp(objInvetario.getCod_emp());
        objConteo.setNro_inventario(objInvetario.getNro_inventario());

        objConteo.setNro_conteo(ObjIni.numerador("nro_conteo"));
        JsonArray conteo = ObjIni.listObjectos("select count(B.nro_conteo)+1 conteo from t_inventario A inner join t_pro_conteo B on A.nro_inventario=B.nro_inventario\n"
                + "where A.nro_inventario=" + objInvetario.getNro_inventario());

        int Nroconteo = 0;
        for (JsonElement jsonElement : conteo) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                Nroconteo = ((int) Double.parseDouble(map.get("conteo").toString()));
            }
        }
        objConteo.setNro_conteo(Nroconteo);
    }

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

    public Articulos getObjArticulo() {
        if (objArticulo == null) {
            objArticulo = new Articulos();
        }
        return objArticulo;
    }

    public void setObjArticulo(Articulos objArticulo) {
        this.objArticulo = objArticulo;
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

    public Inventario getObjInvetario() {
        if (objInvetario == null) {
            objInvetario = new Inventario();
        }
        return objInvetario;
    }

    public void setObjInvetario(Inventario objInvetario) {
        this.objInvetario = objInvetario;
    }

    public List<Articulos> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulos> listArticulos) {
        this.listArticulos = listArticulos;
    }

    public Object[] getTitulos_reporte() {
        if (titulos_reporte == null) {
            titulos_reporte = new Object[8];
        }
        return titulos_reporte;
    }

    public void setTitulos_reporte(Object[] titulos_reporte) {
        this.titulos_reporte = titulos_reporte;
    }

}
