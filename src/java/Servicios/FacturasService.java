/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Comercial.Facturas;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author LENOVO
 */
@Named(value = "facturasservice")
@ApplicationScoped
public class FacturasService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public FacturasService() {
    }

    public Object[] Transaccion(Facturas obj, String accion) {
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
            transacciones.add(Service.service.log("Facturas", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());


            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_facturas");
            o1.setDatos(gson.toJson(obj));
            o1.setBase(login.getBase());
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

    public List<Facturas> Lista() {
        String respuesta = dao.QueryObj("SELECT cod_cliente, cod_tipodoc, cod_documento, nombre, apellido, telefono, \n"
                + "email, cod_ciudad, direccion, barrio, activo, cod_log\n"
                + "FROM public.m_clientes");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Facturas> listFacturas = new ArrayList();
//        for (JsonElement jsonElement : Jelementos) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//
//                Facturas obj = new Facturas();
//                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
//                }.getType());
//                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
//                obj.setCod_cliente(new BigDecimal(map.get("cod_cliente").toString()).intValue());
//                obj.setCod_tipodoc(map.get("cod_tipodoc").toString());
//                obj.setCod_documento(map.get("cod_documento").toString());
//                obj.setNombre(map.get("nombre").toString());
//                obj.setApellido(map.get("apellido").toString());
//                obj.setTelefono(map.get("telefono").toString());
//                obj.setEmail(map.get("email").toString());
//                obj.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
//                obj.setDireccion(map.get("direccion").toString());
//                obj.setActivo(map.get("activo").toString());
//                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
//
//                obj.setActivoB(obj.getActivo().equalsIgnoreCase("S"));
//
//                listFacturas.add(obj);
//
//            }
//        }
        return listFacturas;
    }

    public Object[] recuperarInfo(Facturas obj) {
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
