/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.Estados;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
@Named(value = "estadoService")
@ApplicationScoped
public class EstadoService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public EstadoService() {
    }

    public Object[] Transaccion(Estados obj, String accion) {
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
            transacciones.add(Service.service.log("Estados", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setActivo(obj.isActivoE() == true ? "S" : "N");
            obj.setUni_negativas(obj.isNegativos() == true ? "S" : "N");
            obj.setUbicacion(obj.isUbicacionE() == true ? "S" : "N");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_estados");
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            String resultado = dao.TransObj(gson.toJson(transacciones));
            Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
            }.getType());
            Resulta[0] = map.get("estado");
            Resulta[1] = map.get("mns");
        } catch (Exception ex) {
            Resulta[0] = "Error";
            Resulta[1] = "Error , Comuniquese con soporte";
            System.out.println("Error :  " + ex.toString());
        }

        return Resulta;
    }

    public List<Estados> Lista() {
        String respuesta = dao.QueryObj("select * from m_estados");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Estados> listEstados = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Estados obj = new Estados();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_estado(map.get("cod_estado").toString());
                obj.setNom_estado(map.get("nom_estado").toString());
                obj.setCod_categoria(map.get("cod_categoria").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setUbicacion(map.get("ubicacion").toString());
                obj.setUni_negativas(map.get("uni_negativas").toString());
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setCod_propietario(map.get("cod_propietario").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

//                //Otras Variables
                obj.setActivoE(obj.getActivo().equalsIgnoreCase("S"));
                obj.setUbicacionE(obj.getUbicacion().equalsIgnoreCase("S"));
                obj.setNegativos(obj.getUni_negativas().equalsIgnoreCase("S"));
                listEstados.add(obj);
            }
        }
        return listEstados;
    }

    public List<Estados> ListaBusqueda(String busqueda) {
                String consulta = "select * from m_estados WHERE ";

        if (busqueda.contains("%")) {
            consulta += " (cod_estado like '" + busqueda + "' or nom_estado ilike '" + busqueda + "') ";
        } else {
            consulta += " (cod_estado ='"+ busqueda + "' or nom_estado ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by cod_estado desc Limit 50");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Estados> listEstados = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Estados obj = new Estados();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_estado(map.get("cod_estado").toString());
                obj.setNom_estado(map.get("nom_estado").toString());
                obj.setCod_categoria(map.get("cod_categoria").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setUbicacion(map.get("ubicacion").toString());
                obj.setUni_negativas(map.get("uni_negativas").toString());
                obj.setCod_deposito(map.get("cod_deposito").toString());
                obj.setCod_propietario(map.get("cod_propietario").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

//                //Otras Variables
                obj.setActivoE(obj.getActivo().equalsIgnoreCase("S"));
                obj.setUbicacionE(obj.getUbicacion().equalsIgnoreCase("S"));
                obj.setNegativos(obj.getUni_negativas().equalsIgnoreCase("S"));
                listEstados.add(obj);
            }
        }
        return listEstados;
    }

    public Object[] recuperarInfo(Estados obj) {
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
