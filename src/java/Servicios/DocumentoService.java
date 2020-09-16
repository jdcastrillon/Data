/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Documentos;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author LENOVO
 */
@Named
@javax.enterprise.context.ApplicationScoped
public class DocumentoService {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    public DocumentoService() {
        System.out.println("Contruccion DocumentoService");
    }

    public Object[] Transaccion(Documentos obj, String accion) {
        Object Resulta[] = new Object[2];
        List<objsql> transacciones = new ArrayList();

        //Validacion Datos
        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

        //Log Registro , Solo cuando es nuevo se crea un nuevo proceso de 
        if(accion.equalsIgnoreCase("Nuevo")){
            obj.setCod_log(Service.service.logProceso("logs"));
        }
        //Registro Transaccion Log        
        transacciones.add(Service.service.log("Documentos", login.getUsuario(), accion, obj.getCod_log().intValue(),login.getBase()));

        //Registro Transaccion         
        objsql o1 = new objsql();
        o1.setAccion(accion);
        o1.setTabla("m_tipodocumentos");
        o1.setBase(login.getBase());
        o1.setDatos(gson.toJson(obj));

        transacciones.add(o1);

        String resultado = dao.TransObj(gson.toJson(transacciones));
        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
        }.getType());
        Resulta[0] = map.get("estado");
        Resulta[1] = map.get("mns");
        return Resulta;
    }

    public List<Documentos> listdoc() {
        String respuesta = dao.QueryObj("select * from m_tipodocumentos");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Documentos> listDocumentos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Documentos doc = new Documentos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                
                doc.setCod_tipodoc(map.get("cod_tipodoc").toString());
                doc.setNombredocumento(map.get("nombredocumento").toString());
                doc.setActivo(map.get("activo").toString());
                doc.setAuditoria(map.get("cod_tipodoc").toString());
                doc.setCod_log(new BigDecimal(map.get("cod_log").toString()));
                listDocumentos.add(doc);
            }
        }
        return listDocumentos;
    }

    public Object[] recuperarInfo(Documentos doc) {
        Object Resulta[] = new Object[2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        //Logs
        String respuesta = dao.QueryObj("select proceso , usuario , operacion , cast (fecha as varchar(20)) fecha from sys_logs_trans where cod_proceso=" + doc.getCod_log());
        doc.getLogs().clear();
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

                doc.getLogs().add(log);
            }
        }
        Resulta[0] = doc;

        return Resulta;
    }

    public Object[] buscarDoc(Documentos obj) {
        Object Resulta[] = new Object[2];
        String query = "select * from m_tipodocumentos where 1=1 ";
        int datos = 0;

        if (obj.getCod_tipodoc().length() > 0) {
            if (obj.getCod_tipodoc().contains("%")) {
                query += "and upper(cod_tipodoc) ILIKE '" + obj.getCod_tipodoc().trim() + "'";
            } else {
                query += "and upper(cod_tipodoc)=upper('" + obj.getCod_tipodoc().trim() + "')";
            }
            datos++;
        }

        System.err.println("query : " + query);

        String respuesta = dao.QueryObj(query);
        if (datos > 0) {
            if (respuesta.equalsIgnoreCase("[\"No hay Datos\"]")) {
                System.out.println("Respuesta arrojo que no hay datos");
                Resulta[0] = "Error";
                Resulta[1] = "No hay Datos para la Busqueda";
            } else {
                System.out.println("Respuesta arrojo que si hay datos");
                JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
                List<Documentos> listDocumentos = new ArrayList();
                for (JsonElement jsonElement : Jelementos) {
                    if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                        Documentos doc = new Documentos();
                        Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                        }.getType());
                        System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                        doc.setCod_tipodoc(map.get("cod_tipodoc").toString());
                        doc.setNombredocumento(map.get("nombredocumento").toString());
                        doc.setActivo(map.get("activo").toString());
                        doc.setAuditoria(map.get("cod_tipodoc").toString());
                        doc.setCod_log(new BigDecimal(map.get("cod_log").toString()));
                        listDocumentos.add(doc);
                    }
                }
                Resulta[0] = "OK";
                Resulta[1] = listDocumentos;
            }
        } else {
            Resulta[0] = "Error";
            Resulta[1] = "Debe Colocar al menos un filtro";
        }

        return Resulta;
    }
}
