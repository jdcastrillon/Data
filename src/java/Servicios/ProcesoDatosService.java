/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.*;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
import Servicios.Sistema.Inicializacion;
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
import javax.inject.Inject;

/**
 *
 * @author LENOVO
 */
@Named(value = "procesodatoservice")
@ApplicationScoped
public class ProcesoDatosService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    @Inject
    private Inicializacion ObjIni;

    /**
     * Creates a new instance of ArticuloService
     */
    public ProcesoDatosService() {
    }

    public Object[] Transaccion(ProcesoDatos obj, String accion) {
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
            transacciones.add(Service.service.log("ProcesoDatos", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
//            obj.setNro_docum(Service.service.logProceso("stockajuste").intValue());
            obj.setFec_doc("@fecha" + obj.getFecha().getTime());
            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_pro_datos");
            o1.setBase(login.getBase());
            o1.setDatos(gson.toJson(obj));
            transacciones.add(o1);

            //Detalle
            int linea = 1;
            for (ProcesoDatosDT proDatosDT : obj.getDetalleProDatos()) {               
                proDatosDT.setLinea(linea);
                objsql o2 = new objsql();
                o2.setAccion(accion);
                o2.setTabla("td_pro_datos");
                o2.setBase(login.getBase());
                o2.setDatos(gson.toJson(proDatosDT));
                transacciones.add(o2);
                linea++;
            }

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

    public List<ProcesoDatos> Lista() {
        String respuesta = dao.QueryObj("select * from t_pro_datos order by 1 desc");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<ProcesoDatos> listProcesoDatos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    ProcesoDatos obj = new ProcesoDatos();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                    obj.setNro_proceso(new BigDecimal(map.get("nro_proceso").toString()).intValue());
                    obj.setDescripcion(map.get("descripcion").toString());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                    System.out.println("FEcha : " + obj.getFec_doc());
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setFecha(date1);

                    listProcesoDatos.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(InventarioService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listProcesoDatos;
    }

    public Object[] recuperarInfo(ProcesoDatos obj) {
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
