/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.Depositos;
import Modelo.Sistema.Log_Transaccion;
import Modelo.Bodega.Ubicaciones;
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
@Named(value = "ubicacionService")
@ApplicationScoped
public class UbicacionService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public UbicacionService() {
    }

    public Object[] Transaccion(Ubicaciones obj, String accion) {
        System.out.println("Entro a Servicio");
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
            transacciones.add(Service.service.log("Ubicacion", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setActivo(obj.isActivoUbi() == true ? "S" : "N");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_ubicaciones");
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            String resultado = dao.TransObj(gson.toJson(transacciones));
            Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
            }.getType());
            Resulta[0] = map.get("estado");
            Resulta[1] = map.get("mns");
        } catch (JsonSyntaxException ex) {
            Resulta[0] = "Error";
            Resulta[1] = "Comuniquese con soporte";
        }

        return Resulta;
    }

    public List<Ubicaciones> Lista() {
        String respuesta = dao.QueryObj("select * from m_ubicaciones");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Ubicaciones> listUbicaciones = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Ubicaciones obj = new Ubicaciones();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                obj.setCod_ubicacion(map.get("cod_ubicacion").toString());
                obj.setNom_ubicacion(map.get("nom_ubicacion").toString());
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setPasillo(map.get("pasillo").toString());
                obj.setColumna(map.get("columna").toString());
                obj.setNivel(map.get("nivel").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                //Otras Variables
                obj.setActivoUbi(obj.getActivo().equalsIgnoreCase("S"));

                listUbicaciones.add(obj);
            }
        }
        return listUbicaciones;
    }

    public Object[] recuperarInfo(Ubicaciones obj) {
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

        //Depositos
        Depositos sucu = new Depositos();
        String respuesta2 = dao.QueryObj("select cod_deposito,nom_deposito from m_depositos where activo='A' and cod_deposito='" + obj.getCod_deposito() + "'");
        JsonArray JsonLog2 = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                sucu.setCod_deposito(map.get("cod_deposito").toString());
                sucu.setNom_deposito(map.get("nom_deposito").toString());
            }
        }
        Resulta[1] = sucu;

        return Resulta;
    }

    public List<Ubicaciones> ListaBusqueda(String busqueda) {
        String consulta = "select * from m_ubicaciones WHERE ";

        if (busqueda.contains("%")) {
            consulta += " (cod_ubicacion like '" + busqueda + "' or nom_ubicacion ilike '" + busqueda + "') ";
        } else {
            consulta += " (cod_ubicacion ='" + busqueda + "' or nom_ubicacion ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by cod_ubicacion desc Limit 50");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Ubicaciones> listUbicaciones = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Ubicaciones obj = new Ubicaciones();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                obj.setCod_ubicacion(map.get("cod_ubicacion").toString());
                obj.setNom_ubicacion(map.get("nom_ubicacion").toString());
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setPasillo(map.get("pasillo").toString());
                obj.setColumna(map.get("columna").toString());
                obj.setNivel(map.get("nivel").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                //Otras Variables
                obj.setActivoUbi(obj.getActivo().equalsIgnoreCase("S"));

                listUbicaciones.add(obj);
            }
        }
        return listUbicaciones;
    }

}
