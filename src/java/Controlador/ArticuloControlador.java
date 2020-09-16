
package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Bodega.Categoria;
import Modelo.SubCategoria;
import Modelo.Bodega.m_unidaStock;
import Servicios.ArticuloService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Validaciones;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class ArticuloControlador implements Serializable {

    @Inject
    private ArticuloService articuloService;

    @Inject
    private Inicializacion ObjIni;

    @Inject
    private Validaciones ObjVal;

    private Articulos objArticulo;
    private List<Articulos> listArticulos = new ArrayList();

//    private Negocio objNegocio;
//    private List<Negocio> listNegocios = new ArrayList();
    private Categoria objCategoria;
    private List<Categoria> listCategoria = new ArrayList();

    private m_unidaStock objUnidad;
    private List<m_unidaStock> ListUnidad = new ArrayList();

    //Campos Para Botonera
    Object acciones[] = new Object[5];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private String evento;
    private String valorBusqueda;

    public ArticuloControlador() {
    }

    @PostConstruct
    public void init() {
        try {
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void lista(int condicion) {
        listArticulos.clear();
        listArticulos = articuloService.Lista();

        if (condicion == 1) {
            listCategoria.clear();
            ListUnidad.clear();
//            //Negocios
//            JsonArray Jelementos = ObjIni.listObjectos("select cod_negocio,nom_negocio from m_negocios where estado='A'");
//            for (JsonElement jsonElement : Jelementos) {
//                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                    Negocio neg = new Negocio();
//                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                    neg.setCod_negocio(map.get("cod_negocio").toString());
//                    neg.setNom_negocio(map.get("nom_negocio").toString());
//                    listNegocios.add(neg);
//                }
//            }

            //Categorias
            JsonArray Jelementos2 = ObjIni.listObjectos("select cod_categoria,nom_categoria from m_categoria");
            for (JsonElement jsonElement : Jelementos2) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Categoria cat = new Categoria();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    cat.setCod_categoria(map.get("cod_categoria").toString());
                    cat.setNom_categoria(map.get("nom_categoria").toString());

                    listCategoria.add(cat);
                }
            }

            //Unidades
            JsonArray Jelementos4 = ObjIni.listObjectos("select cod_unidad,nom_unidad from m_unidaStock");
            for (JsonElement jsonElement4 : Jelementos4) {
                if (!jsonElement4.getAsString().equalsIgnoreCase("No hay Datos")) {
                    m_unidaStock uni = new m_unidaStock();
                    Map<String, Object> map = ObjIni.fromJson(jsonElement4);
                    uni.setCod_unidad(map.get("cod_unidad").toString());
                    uni.setNom_unidad(map.get("nom_unidad").toString());
                    ListUnidad.add(uni);
                }
            }
        }
    }

    public void prepareNuevo() {
        setObjArticulo(null);
        getObjArticulo();

        listCategoria.clear();
        ListUnidad.clear();
        //Negocios
//        JsonArray Jelementos = ObjIni.listObjectos("select cod_negocio,nom_negocio from m_negocios where estado='A'");
//        for (JsonElement jsonElement : Jelementos) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                Negocio neg = new Negocio();
//                Map<String, Object> map = ObjIni.fromJson(jsonElement);
//                neg.setCod_negocio(map.get("cod_negocio").toString());
//                neg.setNom_negocio(map.get("nom_negocio").toString());
//                listNegocios.add(neg);
//            }
//        }

        //Categorias
        JsonArray Jelementos2 = ObjIni.listObjectos("select cod_categoria,nom_categoria from m_categoria");
        for (JsonElement jsonElement : Jelementos2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Categoria cat = new Categoria();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                cat.setCod_categoria(map.get("cod_categoria").toString());
                cat.setNom_categoria(map.get("nom_categoria").toString());

                listCategoria.add(cat);
            }
        }

        //Unidades
        JsonArray Jelementos4 = ObjIni.listObjectos("select cod_unidad,nom_unidad from m_unidaStock");
        for (JsonElement jsonElement4 : Jelementos4) {
            if (!jsonElement4.getAsString().equalsIgnoreCase("No hay Datos")) {
                m_unidaStock uni = new m_unidaStock();
                Map<String, Object> map = ObjIni.fromJson(jsonElement4);
                uni.setCod_unidad(map.get("cod_unidad").toString());
                uni.setNom_unidad(map.get("nom_unidad").toString());
                ListUnidad.add(uni);
            }
        }

        this.evento = "Nuevo";
        objArticulo.setAutogenerar(false);
        objArticulo.setActivoComercial(true);
        objArticulo.setActivoStock(true);
        objArticulo.setAletarStock(true);
        controlEventos(evento);
    }

    public void prepareEditar() {
        this.evento = "Editar";
        controlEventos(evento);
    }

    public void prepareEliminar() {
        this.evento = "Eliminar";
        controlEventos(evento);
    }

    public void prepareBuscar() {
        listArticulos.clear();
        this.evento = "Buscar";
        setObjArticulo(null);
        controlEventos(evento);
    }

    public void prepareCrud(Articulos objecto, int condicion) {
        setObjArticulo(null);
        Object Resulta[] = new Object[2];
        Resulta = articuloService.recuperarInfo(objecto);
        setObjArticulo((Articulos) Resulta[0]);
        objArticulo.setCodigo2(objArticulo.getCodigo());
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
        setObjArticulo(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        System.out.println("Evento : " + this.evento);
        System.out.println("Evento : " + this.objArticulo.toString());
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = articuloService.Transaccion(objArticulo, "Nuevo");
                    mns = "Articulo Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = articuloService.Transaccion(objArticulo, "Borrar");
                    mns = "Articulo Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = articuloService.Transaccion(objArticulo, "Editar");
                    mns = "Documento Editado exitosamente";
                    break;
//            case "Buscar":
//                Resulta = docService.buscarDoc(objDoc);
//                break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listArticulos.clear();
                    listArticulos = (List<Articulos>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjArticulo(null);
                    this.evento = "inicio";
                    controlEventos(evento);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", (String) Resulta[1]));
            }
        }
    }

    public boolean validaciones() {
        getObjArticulo();
        String mns = "";
        System.out.println("Articulo " + objArticulo.toString());
        if (this.nuevo == false) {
            //Validaciones
            if (ObjVal.ValPrimaryKey("select count(*) from m_articulos where codigo='" + objArticulo.getCodigo().trim() + "'")) {
                mns = "El codigo del Articulo ya existe";
            } else if (objArticulo.getCodigo().isEmpty()) {
                mns = "Codigo del articulo Obligatorio";
            } else if (objArticulo.getCategoria().equalsIgnoreCase("0")) {
                mns = "Debe Seleccionar una categoria";
            } else if (objArticulo.getCod_unidad().equalsIgnoreCase("0")) {
                mns = "Debe Seleccionar una unidad de medida";
            } else if (objArticulo.getNom_articulo().isEmpty()) {
                mns = "Nombre del articulo Obligatorio";
            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public void controlEventos(String accion) {
        acciones = ObjIni.eventos(accion);
        this.aceptar = (boolean) acciones[0];
        this.editar = (boolean) acciones[1];
        this.eliminar = (boolean) acciones[2];
        this.nuevo = (boolean) acciones[3];
        this.buscar = (boolean) acciones[4];
    }

    public void autoGenerarNum() {
        System.out.println("Entro autoGenerarNum");
        String num = "0";
        if (this.objArticulo.isAutogenerar()) {
            JsonArray Jelementos = ObjIni.listObjectos("select numerador('Data','CodigoArticulo')");
            String Codigo = "0";
            for (JsonElement jsonElement : Jelementos) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Map<String, Object> map = ObjIni.fromJson(jsonElement);
                    Codigo = map.get("numerador").toString();
                }
            }
            num = Codigo;
        } else {
            num = "0";
        }

        this.objArticulo.setCodigo(num);
    }

    public void cargarSubCategorias() {
        //SubCategorias
        objCategoria.getListSubCategoria().clear();
        JsonArray Jelementos3 = ObjIni.listObjectos("select cod_subcategoria,nom_subcategoria from m_subcategoria where cod_categoria='" + objArticulo.getCategoria().trim() + "'");
        for (JsonElement jsonElement2 : Jelementos3) {
            if (!jsonElement2.getAsString().equalsIgnoreCase("No hay Datos")) {
                SubCategoria Scat = new SubCategoria();
                Map<String, Object> map2 = ObjIni.fromJson(jsonElement2);
                Scat.setCod_subcategoria(map2.get("cod_subcategoria").toString());
                Scat.setNom_subcategoria(map2.get("nom_subcategoria").toString());
                objCategoria.getListSubCategoria().add(Scat);
            }
        }
    }
//
//    public void cargafiltros() {
//        getObjFiltros();
//        filtros.add(new FiltrosTabla(1, "Codigo Articulo"));
//        filtros.add(new FiltrosTabla(2, "Nombre Articulo"));
//        filtros.add(new FiltrosTabla(3, "Categoria"));
//        setObjFiltros(new FiltrosTabla(1, "Codigo Articulo"));
//    }

    public void busquedaDatos() {
        System.out.println("Valor : " + valorBusqueda);
        listArticulos.clear();
        listArticulos = articuloService.ListaBusqueda(valorBusqueda);
    }


    public void limpiarDatos() {
        listArticulos.clear();
        listArticulos = articuloService.Lista();
        this.valorBusqueda="";
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

    public ArticuloService getArticuloService() {
        return articuloService;
    }

    public void setArticuloService(ArticuloService articuloService) {
        this.articuloService = articuloService;
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

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Categoria getObjCategoria() {
        if (objCategoria == null) {
            objCategoria = new Categoria();
        }
        return objCategoria;
    }

    public void setObjCategoria(Categoria objCategoria) {
        this.objCategoria = objCategoria;
    }

    public List<Categoria> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<Categoria> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public m_unidaStock getObjUnidad() {
        if (objUnidad == null) {
            objUnidad = new m_unidaStock();
        }
        return objUnidad;
    }

    public void setObjUnidad(m_unidaStock objUnidad) {
        this.objUnidad = objUnidad;
    }

    public List<m_unidaStock> getListUnidad() {
        return ListUnidad;
    }

    public void setListUnidad(List<m_unidaStock> ListUnidad) {
        this.ListUnidad = ListUnidad;
    }

    public Validaciones getObjVal() {
        return ObjVal;
    }

    public void setObjVal(Validaciones ObjVal) {
        this.ObjVal = ObjVal;
    }

    

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

}
