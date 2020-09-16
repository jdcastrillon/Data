/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.CalculoStock;
import Modelo.Bodega.CalculoStockDT;
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
@Named(value = "calculoService")
@ApplicationScoped
public class CalculoService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public CalculoService() {
    }

    public Object[] Transaccion(CalculoStock obj, String accion) {
        System.out.println("Entro a Servicio");
        Object Resulta[] = new Object[2];
        try {
            List<objsql> transacciones = new ArrayList();

            //Validacion Datos
            iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

            //Log Registro
            BigDecimal logproceso = Service.service.logProceso("logs");
            transacciones.add(Service.service.log("CalculoServi", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setActivo(obj.isActivoC() == true ? "A" : "I");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_calcstock");
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            for (CalculoStockDT list_estado : obj.getList_estados()) {
                list_estado.setCod_calculo(obj.getCod_calculo());
                list_estado.setSigno(list_estado.getOperacion().equalsIgnoreCase("S") ? 1 : -1);
                objsql o2 = new objsql();
                o2.setAccion(accion);
                o2.setTabla("md_calcstock_est");
                o2.setDatos(gson.toJson(list_estado));

                transacciones.add(o2);

            }

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

    public List<CalculoStock> Lista() {
        String respuesta = dao.QueryObj("select A.*,B.nombre_ciudad from m_depositos A INNER JOIN m_ciudad B ON A.cod_ciudad=B.cod_ciudad");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<CalculoStock> listCalculoStock = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                CalculoStock obj = new CalculoStock();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
//                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
//                obj.setCod_deposito(map.get("cod_deposito").toString());
//                obj.setNom_deposito(map.get("nom_deposito").toString());
//                obj.setCod_emp(map.get("cod_emp").toString());
//                obj.setCod_sucursal(map.get("cod_sucursal").toString());
//                obj.setPrincipal(map.get("principal").toString());
//                obj.setActivo(map.get("activo").toString());
//                obj.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
//                obj.setTiene_ubicaciones(map.get("tiene_ubicaciones").toString());
//                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
//                obj.setNom_cuidad(map.get("nombre_ciudad").toString());
//                //Otras Variables
//                obj.setDepositoP(obj.getPrincipal().equalsIgnoreCase("A"));
//                obj.setActDepo(obj.getActivo().equalsIgnoreCase("A"));
//                obj.setTieneUbi(obj.getTiene_ubicaciones().equalsIgnoreCase("A"));

                listCalculoStock.add(obj);
            }
        }
        return listCalculoStock;
    }

    public Object[] recuperarInfo(CalculoStock obj) {
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
