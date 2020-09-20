/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.CierreInventario;
import Modelo.Sistema.Log_Transaccion;
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
@Named(value = "cierreinventarioservice")
@ApplicationScoped
public class CierreInventarioService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public CierreInventarioService() {
    }

    public Object[] Transaccion(CierreInventario obj, String accion) {
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
            }
            transacciones.add(Service.service.log("CierreInventario", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setFec_inv("@fecha" + obj.getFecha().getTime());
            obj.setFec_cierre("@fecha" + obj.getFecha2().getTime());

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_cierre_invent");
            o1.setBase(login.getBase());
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

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

    public List<CierreInventario> Lista() {
        String respuesta = dao.QueryObj("SELECT nro_inventario, cod_emp, fec_cierre, fec_inv,  observacion, cod_log\n"
                + "FROM public.t_cierre_invent order by 1 desc limit 500");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<CierreInventario> listCierreInventario = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    CierreInventario obj = new CierreInventario();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());

                    obj.setNro_inventario(new BigDecimal(map.get("nro_inventario").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setFec_inv(map.get("fec_inv").toString());
                    obj.setFec_cierre(map.get("fec_cierre").toString());
                    obj.setObservacion(map.get("observacion").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_inv());
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_cierre());
                    obj.setFecha(date1);
                    obj.setFecha2(date2);

                    listCierreInventario.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(CierreInventarioService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listCierreInventario;
    }

    public List<CierreInventario> ListaBusqueda(String busqueda) {
        String consulta = "SELECT nro_inventario, cod_emp, fec_cierre, fec_inv,  observacion, cod_log\n"
                + "FROM public.t_cierre_invent A WHERE";

        if (busqueda.contains("%")) {
            consulta += " cast(A.nro_inventario as varchar(15)) like '" + busqueda + "' ";
        } else {
            consulta += " cast(A.nro_inventario as varchar(15))='" + busqueda + "' ";
        }

        String respuesta = dao.QueryObj(consulta + " order by A.nro_inventario desc Limit 50");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<CierreInventario> listCierreInventario = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    CierreInventario obj = new CierreInventario();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());

                    obj.setNro_inventario(new BigDecimal(map.get("nro_inventario").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setFec_cierre(map.get("fec_cierre").toString());
                    obj.setFec_inv(map.get("fec_inv").toString());
                    obj.setObservacion(map.get("observacion").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_inv());
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_cierre());
                    obj.setFecha(date1);
                    obj.setFecha2(date2);

                    listCierreInventario.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(CierreInventarioService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listCierreInventario;
    }

    public Object[] recuperarInfo(CierreInventario obj) {
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

        Resulta[0] = obj;

        return Resulta;
    }

}
