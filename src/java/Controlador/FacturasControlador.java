package Controlador;

import Modelo.Bodega.Articulos;
import Modelo.Comercial.Clientes;
import Modelo.Comercial.Facturas;
import Modelo.Comercial.FacturasDT;
import Modelo.Comercial.FacturasDT_Pagos;
import Servicios.FacturasService;
import Servicios.Sistema.Inicializacion;
import Servicios.Sistema.Seleccion;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
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
public class FacturasControlador {

    @Inject
    private FacturasService facturaService;

    @Inject
    private Inicializacion ObjIni;
//
//    @Inject
//    private Validaciones ObjVal;
//
    @Inject
    private Seleccion SelService;

    private Facturas objFacturas;
    private List<Facturas> listFacturas = new ArrayList();
    private Clientes objCliente;
    private Articulos objArticulo;
    private List<Clientes> listClientes = new ArrayList();
    private List<Articulos> listArticulos = new ArrayList();
    private List<FacturasDT_Pagos> FacturaPagos = new ArrayList();

    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    Object acciones[] = new Object[6];
    Object totales[] = new Object[8];
    private boolean aceptar;
    private boolean editar;
    private boolean eliminar;
    private boolean nuevo;
    private boolean buscar;
    private boolean reporte;
    private boolean datos;
    private String evento;

    //Variables para calculo
    private String tipoPago;
    private double valor;
    private double totalGrilla;
    private double totalCambio;

    @PostConstruct
    public void init() {
        try {
            getObjFacturas();
            getObjArticulo();
            lista(1);
            this.evento = "inicio";
            this.datos = true;
            controlEventos(evento);
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FacturasControlador() {
    }

    public void lista(int condicion) {
        //1: Carga Inicial
        //2: Carga Despues de Transaccion
        listFacturas.clear();
        listFacturas = facturaService.Lista();

    }

    public void prepareNuevo() {
        setObjFacturas(null);
        getObjFacturas();
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
        listFacturas.clear();
        this.evento = "Buscar";
        setObjFacturas(null);
        controlEventos(evento);
    }

    public void prepareReporte() {
        this.evento = "Reporte";
        controlEventos(evento);
    }

    public void prepareCrud(Facturas objecto, int condicion) {
        setObjFacturas(null);
        Object Resulta[] = new Object[2];
        Resulta = facturaService.recuperarInfo(objecto);
        setObjFacturas((Facturas) Resulta[0]);
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
        setObjFacturas(null);
    }

    public void transaccion() {
        Object Resulta[] = new Object[1];
        String mns = "";
        if (validaciones()) {
            switch (this.evento) {
                case "Nuevo":
                    Resulta = facturaService.Transaccion(objFacturas, "Nuevo");
                    mns = "Proveedor Creado exitosamente";
                    break;
                case "Eliminar":
                    Resulta = facturaService.Transaccion(objFacturas, "Borrar");
                    mns = "Proveedor Eliminado exitosamente";
                    break;
                case "Editar":
                    Resulta = facturaService.Transaccion(objFacturas, "Editar");
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
//                    Resulta = facturaService.buscarDoc(objFacturas);
//                    break;//                case "Buscar":
//                    Resulta = facturaService.buscarDoc(objFacturas);
//                    break;

            }
            if (Resulta[0].equals("OK")) {
                if (evento.equalsIgnoreCase("Buscar")) {
                    listFacturas.clear();
                    listFacturas = (List<Facturas>) Resulta[1];
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", mns));
                    lista(2);
                    setObjFacturas(null);
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
        getObjFacturas();
        String mns = "";
        System.out.println(" aceptar : " + aceptar);
        System.out.println(" editar : " + editar);
        System.out.println(" eliminar : " + eliminar);
        System.out.println(" nuevo : " + nuevo);
        System.out.println(" buscar : " + buscar);

        if (this.nuevo == false) {
            //Validaciones
//            if (ObjVal.ValPrimaryKey("select count(*) from m_tipodocumentos where cod_tipodoc='" + objFacturas.getCod_tipodoc() + "'")) {
//                mns = "El codigo del documento ya existe";
//            }
        }

        System.out.println("mns : " + mns);
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", mns));
        }
        return mns.length() <= 0;
    }

    public List<Clientes> selclientes(String query) {
        getObjCliente();
        System.out.println("------------ Bean clientes------------");
        System.out.println("Articulo : " + objCliente.toString());

        listClientes = SelService.SelClientes(query, 2);
        return listClientes;
    }

    public void cargarCliente() {
//        System.out.println("Carga Estado : " + objCliente.toString());
//        FacturasDT obj = new FacturasDT(objCliente.getCod_articulo(), objCliente.getCodigo(), objCliente.getNom_articulo());
//        obj.setStock(ObjIni.StockDisponible(objFacturas.getCod_emp(), objCliente.getCod_articulo()));
//        if (buscarElemento(obj) == false) {
//            objFacturas.getComprasDt().add(0, obj);
//        }
//        setObjArticulo(null);
//        totalesFooter();
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
        FacturasDT obj = new FacturasDT(objArticulo.getCod_articulo(), objArticulo.getCodigo(), objArticulo.getNom_articulo(), 1);
        obj.setStock(ObjIni.StockDisponible("Data", objArticulo.getCod_articulo(),"N",""));
        if (buscarElemento(obj) == false) {
            objFacturas.getFacturasDT().add(0, obj);
        }
        setObjArticulo(null);
        totalesGrilla(obj);
    }

    public boolean buscarElemento(FacturasDT obj) {
        boolean valor = false;
        for (FacturasDT obj2 : objFacturas.getFacturasDT()) {
            if (obj.getCod_articulo() == obj2.getCod_articulo()) {
                valor = true;
                break;
            }
        }
        return valor;
    }

    public void totalesGrilla(FacturasDT obj) {
        System.out.println("TotalGrilla");

        for (FacturasDT facturasDT : objFacturas.getFacturasDT()) {
            if (obj.getCod_articulo() == facturasDT.getCod_articulo()) {
                facturasDT.setImp_neto(facturasDT.getImp_fact() * facturasDT.getCantidad());
                facturasDT.setImp_total(facturasDT.getImp_neto() + (facturasDT.getImp_neto() * (facturasDT.getPorc_imp() / 100)));
                facturasDT.setImp_impuesto(facturasDT.getImp_neto() * (facturasDT.getPorc_imp() / 100));
                System.out.println("Neto : " + facturasDT.getImp_neto());
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
        for (FacturasDT facturasDT : objFacturas.getFacturasDT()) {
//            stock = stock + facturasDT.getStock();
//            costo = costo + facturasDT.getImp_costo();
//            cantidad = cantidad + facturasDT.getCantidad();
            neto = neto + (facturasDT.getImp_neto());
            total = total + facturasDT.getImp_total();
            impuesto = impuesto + facturasDT.getImp_impuesto();
        }
//        totales[0] = stock;
//        totales[1] = formatter.format(costo);
//        totales[2] = cantidad;
        totales[3] = formatter.format(neto);
        totales[4] = formatter.format(total);
        totales[5] = formatter.format(costo * cantidad);
        totales[6] = formatter.format(impuesto);

        objFacturas.setImp_neto(neto);
        objFacturas.setImp_impuesto(impuesto);
        objFacturas.setImp_total(total);

        System.out.println("NETO : " + objFacturas.getImp_neto());

    }

    public void valorPagar(int condicion) {
        System.out.println("Valor : " + valor);
        System.out.println("Valor : " + tipoPago);
        this.totalGrilla = 0;
        this.totalCambio = 0;
        if (condicion == 1) {
            boolean existe = false;
            for (FacturasDT_Pagos FacturaPago : FacturaPagos) {
                if (tipoPago.equalsIgnoreCase(FacturaPago.getTipoPago())) {
                    FacturaPago.setImporte(valor);
                    existe = true;
                    break;
                }
            }

            if (existe == false) {
                FacturaPagos.add(new FacturasDT_Pagos(tipoPago, valor));
            }

            //Total
            for (FacturasDT_Pagos FacturaPago : FacturaPagos) {
                totalGrilla += FacturaPago.getImporte();
            }

            this.totalCambio = totalGrilla - objFacturas.getImp_total();
        } else {
            this.valor = 0;
            for (FacturasDT_Pagos FacturaPago : FacturaPagos) {
                if (tipoPago.equalsIgnoreCase(FacturaPago.getTipoPago())) {
                    this.valor = FacturaPago.getImporte();
                    break;
                }
            }
        }

    }

    public FacturasService getFacturaService() {
        return facturaService;
    }

    public void setFacturaService(FacturasService facturaService) {
        this.facturaService = facturaService;
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

    public Facturas getObjFacturas() {
        if (objFacturas == null) {
            objFacturas = new Facturas();
        }

        return objFacturas;
    }

    public void setObjFacturas(Facturas objFacturas) {
        this.objFacturas = objFacturas;
    }

    public List<Facturas> getListFacturas() {
        return listFacturas;
    }

    public void setListFacturas(List<Facturas> listFacturas) {
        this.listFacturas = listFacturas;
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

    public Clientes getObjCliente() {
        if (objCliente == null) {
            objCliente = new Clientes();

        }
        return objCliente;
    }

    public void setObjCliente(Clientes objCliente) {
        this.objCliente = objCliente;
    }

    public List<Clientes> getListClientes() {
        return listClientes;
    }

    public void setListClientes(List<Clientes> listClientes) {
        this.listClientes = listClientes;
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

    public Object[] getTotales() {
        return totales;
    }

    public void setTotales(Object[] totales) {
        this.totales = totales;
    }

    public NumberFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public List<FacturasDT_Pagos> getFacturaPagos() {
        return FacturaPagos;
    }

    public void setFacturaPagos(List<FacturasDT_Pagos> FacturaPagos) {
        this.FacturaPagos = FacturaPagos;
    }

    public double getTotalGrilla() {
        return totalGrilla;
    }

    public void setTotalGrilla(double totalGrilla) {
        this.totalGrilla = totalGrilla;
    }

    public double getTotalCambio() {
        return totalCambio;
    }

    public void setTotalCambio(double totalCambio) {
        this.totalCambio = totalCambio;
    }

}
