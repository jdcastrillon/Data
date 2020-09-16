/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Compras.Pagos;
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
@Named(value = "pagosservice")
@ApplicationScoped
public class PagosService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public PagosService() {
    }

    public Object[] Transaccion(Pagos obj, String accion) {
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
                obj.setCod_pago(Service.service.logProceso("pagos").intValue());
            }
            transacciones.add(Service.service.log("Pagos", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            obj.setActivo(obj.isB_activo() ? "S" : "N");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_pagos");
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

    public List<Pagos> Lista() {
        String respuesta = dao.QueryObj("SELECT cod_pago, dias, descripcion, activo, cod_log FROM public.m_pagos");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Pagos> listPagos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Pagos obj = new Pagos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_pago(new BigDecimal(map.get("cod_pago").toString()).intValue());
                obj.setDescripcion(map.get("descripcion").toString());
                obj.setDias(new BigDecimal(map.get("dias").toString()).intValue());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                obj.setB_activo(obj.getActivo().equalsIgnoreCase("S"));
                listPagos.add(obj);
            }
        }
        return listPagos;
    }

    public Object[] recuperarInfo(Pagos obj) {
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
