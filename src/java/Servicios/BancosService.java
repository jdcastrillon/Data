/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Compras.Bancos;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author LENOVO
 */
@Named(value = "bancoservice")
@ApplicationScoped
public class BancosService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public BancosService() {
    }

    public Object[] Transaccion(Bancos obj, String accion) {
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
                obj.setCod_banco(Service.service.logProceso("bancos").intValue());
            }
            transacciones.add(Service.service.log("Bancos", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setActivo(obj.isB_activo() ? "S" : "N");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_bancos");
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

    public List<Bancos> Lista() {
        String respuesta = dao.QueryObj("select cod_banco,descripcion,activo,cod_pais,cod_log from m_bancos");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Bancos> listBancos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {

                Bancos obj = new Bancos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_banco(new BigDecimal(map.get("cod_banco").toString()).intValue());
                obj.setDescripcion(map.get("descripcion").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_pais(new BigDecimal(map.get("cod_pais").toString()).intValue());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                obj.setB_activo(obj.getActivo().equalsIgnoreCase("S"));
                listBancos.add(obj);
            }
        }
        return listBancos;
    }

    public List<Bancos> ListaBusqueda(String busqueda) {

        String consulta = "select cod_banco,descripcion,activo,cod_pais,cod_log from m_bancos A WHERE ";

        if (busqueda.contains("%")) {
            consulta += " (A.cod_banco like '" + busqueda + "' or A.descripcion ilike '" + busqueda + "') ";
        } else {
            consulta += " (A.cod_banco ='" + busqueda + "' or A.descripcion ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by A.cod_banco desc Limit 50");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        
        List<Bancos> listBancos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {

                Bancos obj = new Bancos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_banco(new BigDecimal(map.get("cod_banco").toString()).intValue());
                obj.setDescripcion(map.get("descripcion").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_pais(new BigDecimal(map.get("cod_pais").toString()).intValue());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                obj.setB_activo(obj.getActivo().equalsIgnoreCase("S"));
                listBancos.add(obj);
            }
        }
        return listBancos;
    }

    public Object[] recuperarInfo(Bancos obj) {
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
