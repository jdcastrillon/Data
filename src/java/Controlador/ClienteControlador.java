package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Ciudades;
import Modelo.Comercial.Clientes;
import Modelo.Correos;
import Modelo.Bodega.Depositos;
import Modelo.Documentos;
import Modelo.Empresa;
import Modelo.Bodega.Estados;
import Modelo.Telefonos;
import Modelo.Bodega.Ubicaciones;
import Servicios.ClientesService;
import Servicios.ProveedorService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import Servicios.Sistema.Validaciones;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.math.BigDecimal;
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

/**
 *
 * @author LENOVO
 */
@ManagedBean
@ViewScoped
public class ClienteControlador {

    @Inject
    private ClientesService clienteService;

    @Inject
    private Inicializacion ObjIni;
//
//    @Inject
//    private Validaciones ObjVal;
//
    @Inject
    private Seleccion SelService;

    private Clientes objClientes;
    private Ciudades objCiudad;


    private List<Clientes> listClientes = new ArrayList();
    private List<Documentos> listDoc = new ArrayList();

    Object acciones[] = new Object[6];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private boolean datos;
    private String evento;

    @PostConstruct
    public void init() {
        try {
            getObjClientes();
            lista(1);
            this.evento = "inicio";
            this.datos = true;
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClienteControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listClientes.clear();
        listDoc.clear();
        listClientes = clienteService.Lista();

        //Documentos
        JsonArray Jelementos = ObjIni.listObjectos("select cod_tipodoc,nombredocumento from m_tipodocumentos");
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Documentos doc = new Documentos();
                Map<String, Object> map = ObjIni.fromJson(jsonElement);
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                doc.setCod_tipodoc(map.get("cod_tipodoc").toString());
                doc.setNombredocumento(map.get("nombredocumento").toString());
                listDoc.add(doc);
            }
        }

        //Bancos
    }

    public void prepareNuevo() {
        setObjClientes(null);
        getObjClientes();
        this.objClientes.setActivoB(true);
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
        listClientes.clear();
        this.evento = "Buscar";
        setObjClientes(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Clientes objecto, int condicion) {
        setObjClientes(null);
        Object Resulta[] = new Object[2];
        Resulta = clienteService.recuperarInfo(objecto);
        setObjClientes((Clientes) Resulta[0]);
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
        setObjClientes(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        objClientes.setCod_ciudad(objCiudad.getCod_ciudad());
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = clienteService.Transaccion(objClientes, "Nuevo");
                    mns = "Proveedor Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = clienteService.Transaccion(objClientes, "Borrar");
                    mns = "Proveedor Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = clienteService.Transaccion(objClientes, "Editar");
                    mns = "Proveedor Editado exitosamente";
                    break;
                case "Reporte": {
//                    try {
//                        Resulta = SelService.PDFDescargar2("reporte");
//                    } catch (IOException ex) {
//                        System.out.println("Error reporte");
//                        Logger.getLogger(ProveedorControlador.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
                mns = "Reporte";
                break;
//                case "Buscar":
//                    Resulta = clienteService.buscarDoc(objClientes);
//                    break;//                case "Buscar":
//                    Resulta = clienteService.buscarDoc(objClientes);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listClientes.clear();
                    listClientes = (List<Clientes>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjClientes(null);
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
        getObjClientes();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objClientes.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public List<Ciudades> ciudades(String query) {
        System.out.println("------------ Bean ciudades------------");
        List<Ciudades> listCiudades = new ArrayList();
        listCiudades = SelService.cargaCiudad(query, 2);
        return listCiudades;
    }

    //************************************************ESTANDAR
//    public void agregarVenedor() {
//        getObjVendedor();
//        System.out.println("Entro agregar vendedor");
//        System.out.println("- " + objVendedor.toString());
//        objClientes.getList_vend().add(objVendedor);
//        setObjVendedor(null);
//        getObjVendedor();
//    }
//
//    public void agregarCuentas() {
//        getObjCuentas();
//        System.out.println("Entro agregar cuenta");
//        System.out.println("- " + objCuentas.toString());
//        for (Bancos listBanco : listBancos) {
//            if (objCuentas.getCod_banco() == listBanco.getCod_banco()) {
//                objCuentas.setNom_banco(listBanco.getDescripcion());
//            }
//        }
//        objClientes.getList_Bancos().add(objCuentas);
//        setObjCuentas(null);
//        getObjCuentas();
//    }
//
//    public void quitarVendedor(Prove_Vendedor obj) {
//        System.out.println("Entro quitar Tel : " + obj.toString());
//        objClientes.getList_vend().remove(obj);
//    }
//
//    public void quitarCuenta(Provee_Bancos obj) {
//        System.out.println("Entro quitar Tel : " + obj.toString());
//        objClientes.getList_Bancos().remove(obj);
//    }

    public ClientesService getClienteService() {
        return clienteService;
    }

    public void setClienteService(ClientesService clienteService) {
        this.clienteService = clienteService;
    }

    public Inicializacion getObjIni() {
        return ObjIni;
    }

    public void setObjIni(Inicializacion ObjIni) {
        this.ObjIni = ObjIni;
    }

    public Seleccion getSelService() {
        return SelService;
    }

    public void setSelService(Seleccion SelService) {
        this.SelService = SelService;
    }

    public Clientes getObjClientes() {
        if(objClientes==null){
            objClientes=new Clientes();
        }
        return objClientes;
    }

    public void setObjClientes(Clientes objClientes) {
        this.objClientes = objClientes;
    }

    public Ciudades getObjCiudad() {
        if(objCiudad==null){
            objCiudad=new Ciudades();
        }
        return objCiudad;
    }

    public void setObjCiudad(Ciudades objCiudad) {
        this.objCiudad = objCiudad;
    }

    public List<Clientes> getListClientes() {
        return listClientes;
    }

    public void setListClientes(List<Clientes> listClientes) {
        this.listClientes = listClientes;
    }

    public List<Documentos> getListDoc() {
        return listDoc;
    }

    public void setListDoc(List<Documentos> listDoc) {
        this.listDoc = listDoc;
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

    public boolean isDatos() {
        return datos;
    }

    public void setDatos(boolean datos) {
        this.datos = datos;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    

}
