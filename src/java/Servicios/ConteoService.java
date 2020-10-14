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
import ModeloService.imp_tablas;
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
@Named(value = "conteoservice")
@ApplicationScoped
public class ConteoService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public ConteoService() {
    }

    public Object[] Transaccion(Conteo obj, String accion) {
        System.out.println("Entro a Servicio " + obj.toString());
        Object Resulta[] = new Object[9];
        try {
            List<objsql> transacciones = new ArrayList();

            //Validacion Datos
            iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

            //Log Registro
            BigDecimal logproceso = new BigDecimal(obj.getCod_log());
            if (accion.equalsIgnoreCase("Nuevo")) {
                logproceso = Service.service.logProceso("logs");
            }
            transacciones.add(Service.service.log("Conteo", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
//            obj.setNro_docum(Service.service.logProceso("stockajuste").intValue());
            obj.setFec_doc("@fecha" + obj.getFecha().getTime());

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_pro_conteo");
            o1.setBase(login.getBase());
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            //Detalle
            int linea = 1;
            for (ProConteoDT proConteoDT : obj.getDetalleCont()) {
                proConteoDT.setNro_conteo(obj.getNro_conteo());
                proConteoDT.setLinea(linea);
                objsql o2 = new objsql();
                o2.setAccion(accion);
                o2.setTabla("td_artpro_conteo");
                o2.setBase(login.getBase());
                o2.setDatos(gson.toJson(proConteoDT));
                transacciones.add(o2);
                linea++;
            }

            String resultado = dao.TransObj(gson.toJson(transacciones));
            Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
            }.getType());
            System.out.println(":" + map.get("mns").toString());
            Resulta[0] = map.get("estado");
            Resulta[1] = map.get("mns").toString().indexOf("#imp") > 0 ? map.get("mns").toString().substring(12, 100) : map.get("mns");
            Resulta[2] = "Inventario :" + obj.getNro_inventario();
            Resulta[3] = "Descarga :" + obj.getNro_conteo();
            Resulta[4] = "Categoria :" + (obj.getCod_categoria().equalsIgnoreCase("0") ? "TODOS" : obj.getCod_categoria());
            Resulta[5] = obj.getNom_Categoria();
            Resulta[6] = "SubCategoria : " + (obj.getCod_subcategoria().equalsIgnoreCase("0") ? "TODOS" : obj.getCod_subcategoria());
            Resulta[7] = obj.getNom_subcategoria();
            Resulta[8] = "Inventario("+obj.getNro_inventario()+"_"+obj.getNro_conteo()+")";

        } catch (JsonSyntaxException ex) {
            Resulta[0] = "Error";
            Resulta[1] = "Comuniquese con soporte";
            Resulta[2] = "";
            Resulta[3] = "";
            Resulta[4] = "";
            Resulta[5] = "";
            Resulta[6] = "";
            Resulta[7] = "";
            Resulta[8] = "";
        }

        return Resulta;
    }

    public List<Conteo> Lista() {
        System.out.println("Ingreo a lista conteo");
        String respuesta = dao.QueryObj("select * from t_pro_conteo order by 1 desc");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Conteo> listConteo = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    Conteo obj = new Conteo();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
//                    System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                    obj.setNro_conteo(new BigDecimal(map.get("nro_conteo").toString()).intValue());
                    obj.setNro_inventario(new BigDecimal(map.get("nro_inventario").toString()).intValue());
                    obj.setCod_categoria(map.get("cod_categoria").toString());
                    obj.setCod_subcategoria(map.get("cod_subcategoria").toString());
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                    System.out.println("FEcha : " + obj.getFec_doc());
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setFecha(date1);
                    listConteo.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(ConteoService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listConteo;
    }

    public Object[] recuperarInfo(Conteo obj) {
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

        //Articulos
        String respuesta2 = dao.QueryObj("select A.*,B.codigo,B.nom_articulo from td_ajuststock A INNER JOIN  m_articulos B ON A.cod_articulo=B.cod_articulo where trans=" + obj.getNro_conteo() + " order by linea");

        Resulta[0] = obj;
        Resulta[1] = "Inventario:" + obj.getNro_inventario() + " Conteo:" + obj.getNro_conteo();

        return Resulta;
    }

}
