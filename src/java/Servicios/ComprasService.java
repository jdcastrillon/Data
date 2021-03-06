/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Compras.Compras;
import Modelo.Compras.ComprasDT;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.imp_tablas;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author LENOVO
 */
@Named(value = "comprasservice")
@ApplicationScoped
public class ComprasService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    public ComprasService() {
    }

    public Object[] Transaccion(Compras obj, String accion) {
        System.out.println("Entro a Servicio " + obj.toString());
        Object Resulta[] = new Object[2];
        try {
            List<objsql> transacciones = new ArrayList();

            //Validacion Datos
            iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

            //Log Registro
            BigDecimal logproceso = new BigDecimal(obj.getCod_log());
            if (accion.equalsIgnoreCase("Nuevo")) {
                logproceso = Service.service.logProceso("logs");
                obj.setTrans(Service.service.logProceso("trans").intValue());
            }
            transacciones.add(Service.service.log("Compras", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setCod_docum("Compra");
            obj.setFec_doc("@fecha" + obj.getD_fec_doc().getTime());
            obj.setFec_entrega(obj.getFec_doc());

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_compras");
            o1.setBase(login.getBase());
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            int linea = 1;
            for (ComprasDT comprasDT : obj.getComprasDt()) {
                comprasDT.setTrans(obj.getTrans());
                comprasDT.setLinea(linea);

                objsql o2 = new objsql();
                o2.setAccion(accion);
                o2.setTabla("td_compras");
                o2.setBase(login.getBase());
                o2.setDatos(gson.toJson(comprasDT));

                transacciones.add(o2);
                linea++;
            }

            //Impactos
            imp_tablas param = new imp_tablas();
            param.setCod_trans(obj.getTrans());
            param.setAccion(accion);
            objsql o3 = new objsql();
            o3.setAccion("Impacto");
            o3.setTabla("imp_compra");
            o3.setBase(login.getBase());
            o3.setDatos(gson.toJson(param));

            transacciones.add(o3);

            String resultado = dao.TransObj(gson.toJson(transacciones));
            Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
            }.getType());
            System.out.println(":" + map.get("mns").toString());
            Resulta[0] = map.get("estado");
            Resulta[1] = map.get("mns").toString().indexOf("#imp") > 0 ? map.get("mns").toString().substring(12, 100) : map.get("mns");
        } catch (JsonSyntaxException ex) {
            Resulta[0] = "Error";
            Resulta[1] = "Comuniquese con soporte";
        }

        return Resulta;
    }

    public List<Compras> Lista() {
        String respuesta = dao.QueryObj("SELECT trans, cod_emp, cod_provedor, cod_docum, nro_docum, cod_fpago, \n"
                + "  fec_doc, fec_entrega, observaciones, cod_log\n"
                + "  FROM public.t_compras");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Compras> listCompras = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Date date1;
                try {
                    Compras obj = new Compras();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());

                    obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                    obj.setCod_docum(map.get("cod_docum").toString());
                    obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                    obj.setCod_fpago(new BigDecimal(map.get("cod_fpago").toString()).intValue());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setFec_entrega(map.get("fec_entrega").toString());
                    obj.setObservaciones(map.get("observaciones").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    System.out.println("FEcha : " + obj.getFec_doc());
                    date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setD_fec_doc(date1);
                    listCompras.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(ComprasService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listCompras;
    }

    public Object[] recuperarInfo(Compras obj) {
        Object Resulta[] = new Object[2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        //Logs
        String respuesta = dao.QueryObj("select proceso , usuario , operacion , cast (fecha as varchar(20)) fecha from sys_logs_trans where cod_proceso=" + obj.getCod_log());
        obj.getLogs().clear();
        JsonArray JsonLog = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Log_Transaccion log = new Log_Transaccion();
                Map<String, Object> mapcorreo = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                log.setOperacion(mapcorreo.get("operacion").toString());
                log.setProceso(mapcorreo.get("proceso").toString());
                log.setUsuario(mapcorreo.get("usuario").toString());
                log.setFecha(mapcorreo.get("fecha").toString());

                obj.getLogs().add(log);
            }
        }

        //Articulos
        String respuesta2 = dao.QueryObj("SELECT trans, A.cod_articulo, stock, cantidad, imp_costo, impuesto, A.porc_imp, \n"
                + "imp_impuesto, imp_neto, imp_total, linea,B.nom_articulo,B.codigo\n"
                + " FROM public.td_compras A inner join m_articulos B on A.cod_articulo=B.cod_articulo \n"
                + "where trans=" + obj.getTrans() + " order by linea");

        obj.getComprasDt().clear();
        JsonArray JsonLog2 = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                ComprasDT obj2 = new ComprasDT();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                obj2.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                obj2.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                obj2.setStock(new BigDecimal(map.get("stock").toString()).intValue());
                obj2.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                obj2.setImp_costo(new BigDecimal(map.get("imp_costo").toString()).intValue());
                obj2.setImpuesto(map.get("impuesto").toString());
                obj2.setPorc_imp(new BigDecimal(map.get("porc_imp").toString()).intValue());
                obj2.setImp_impuesto(new BigDecimal(map.get("imp_impuesto").toString()).intValue());
                obj2.setImp_neto(new BigDecimal(map.get("imp_neto").toString()).intValue());
                obj2.setImp_total(new BigDecimal(map.get("imp_total").toString()).intValue());
                obj2.setLinea(new BigDecimal(map.get("linea").toString()).intValue());
                obj2.setNom_articulo(map.get("nom_articulo").toString());
                obj2.setCodigo(map.get("codigo").toString());
                obj.getComprasDt().add(obj2);
            }
        }
        Resulta[0] = obj;

        return Resulta;
    }

    public List<Compras> ListaBusqueda(String busqueda) {
        String consulta = "SELECT trans, cod_emp, B.cod_provedor, cod_docum, nro_docum, A.cod_fpago, \n"
                + "fec_doc, fec_entrega, observaciones, A.cod_log\n"
                + "FROM public.t_compras A INNER JOIN m_proveedores B ON\n"
                + "A.cod_provedor=B.cod_provedor\n"
                + "WHERE ";

        if (busqueda.contains("%")) {
            consulta += "(B.cod_documento like '%" + busqueda + "%' or cast(nro_docum as varchar(15)) like '%" + busqueda + "%') ";
        } else {
            consulta += " (B.cod_documento ='" + busqueda + "' or cast(nro_docum as varchar(15)) ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by A.nro_docum desc Limit 50");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Compras> listCompras = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Date date1;
                try {
                    Compras obj = new Compras();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());

                    obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                    obj.setCod_docum(map.get("cod_docum").toString());
                    obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                    obj.setCod_fpago(new BigDecimal(map.get("cod_fpago").toString()).intValue());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setFec_entrega(map.get("fec_entrega").toString());
                    obj.setObservaciones(map.get("observaciones").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    System.out.println("FEcha : " + obj.getFec_doc());
                    date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setD_fec_doc(date1);
                    listCompras.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(ComprasService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listCompras;
    }

}
