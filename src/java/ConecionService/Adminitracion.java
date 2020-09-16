/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConecionService;

import Modelo.Sistema.Log_Transaccion;
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
import java.util.Date;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public class Adminitracion implements Serializable {

    Gson gson = new Gson();

    public String jsonTrans(java.lang.String json) {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        return port.jsonTrans(json);
    }

    public String login(java.lang.String json) {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        return port.login(json);
    }

    public String jsonquery(java.lang.String consulta) {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        return port.jsonquery(consulta);
    }

    public String jsonreporte(java.lang.String consulta) {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        return port.jsonreporte(consulta);
    }

    public String jsonqueryMulti(java.lang.String consulta) {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        return port.jsonqueryMulti(consulta);
    }

    public objsql log(String proceso, String usuario, String operacion, int codLog, String base) {
        System.out.println("Entro a Log");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Log_Transaccion log = new Log_Transaccion(codLog, 0, proceso, usuario, operacion, "@fecha" + new Date().getTime());

        objsql o1 = new objsql();
        o1.setAccion("Nuevo");
        o1.setTabla("sys_logs_trans");
        o1.setBase(base);
        o1.setDatos(gson.toJson(log));

        System.out.println(" - log : " + log.toString());
        return o1;
    }

    public BigDecimal logProceso(String Secuencia) {
        String respuesta = jsonquery("SELECT nextval('" + Secuencia + "')");
        System.out.println("Respuesta de Secuencia " + respuesta);
        BigDecimal codigoSecuencia = null;
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : Jelementos) {
            Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
            }.getType());
            System.out.println("Secuencia : " + map.get("nextval"));
            //palabra = palabra.substring(0, palabra.indexOf("."));
            String substring = map.get("nextval").toString().substring(0, map.get("nextval").toString().indexOf("."));
            codigoSecuencia = new BigDecimal(substring);
        }
        return codigoSecuencia;
    }

    private static void cargaDatosArbol() {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        port.cargaDatosArbol();
    }

}
