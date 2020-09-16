/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.Categoria;
import Modelo.Sistema.Log_Transaccion;
import Modelo.SubCategoria;
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
@Named(value = "categoriaService")
@ApplicationScoped
public class CategoriaService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    public CategoriaService() {
    }

    public Object[] Transaccion(Categoria obj, String accion) {
        Object Resulta[] = new Object[2];
        List<objsql> transacciones = new ArrayList();

        //Validacion Datos
        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

        //Log Registro
        BigDecimal logproceso = new BigDecimal(obj.getCod_log());
        if (accion.equalsIgnoreCase("Nuevo")) {
            logproceso = Service.service.logProceso("logs");
        }
        transacciones.add(Service.service.log("Categoria", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

        //Registro Transaccion
        obj.setCod_log(logproceso.intValue());
        objsql o1 = new objsql();
        o1.setAccion(accion);
        o1.setTabla("m_categoria");
        o1.setDatos(gson.toJson(obj));

        transacciones.add(o1);

        if (accion.equalsIgnoreCase("Editar")) {
            //Eliminamos SubCategoria
            objsql o2 = new objsql();
            o2.setAccion("Transaccion");
            o2.setTabla("m_subcategoria");
            o2.setDatos("delete from m_subcategoria where cod_categoria='"+obj.getCod_categoria()+"'");
            o2.setSql("delete from m_subcategoria where cod_categoria='"+obj.getCod_categoria()+"'");

            transacciones.add(o2);
            //Agregamos Subcategorias
            for (SubCategoria subCategoria : obj.getListSubCategoria()) {
                subCategoria.setCod_categoria(obj.getCod_categoria());
                objsql o3 = new objsql();
                o3.setAccion("Nuevo");
                o3.setTabla("m_subcategoria");
                o3.setDatos(gson.toJson(subCategoria));

                transacciones.add(o3);
            }
        }

        //Agregamos Subcategorias
        for (SubCategoria subCategoria : obj.getListSubCategoria()) {
            subCategoria.setCod_categoria(obj.getCod_categoria());
            objsql o2 = new objsql();
            o2.setAccion(accion);
            o2.setTabla("m_subcategoria");
            o2.setDatos(gson.toJson(subCategoria));

            transacciones.add(o2);
        }

        String resultado = dao.TransObj(gson.toJson(transacciones));
        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
        }.getType());
        Resulta[0] = map.get("estado");
        Resulta[1] = map.get("mns");
        return Resulta;
    }

    public List<Categoria> Lista() {
        String respuesta = dao.QueryObj("select * from m_categoria");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Categoria> listCategoria = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Categoria obj = new Categoria();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_categoria(map.get("cod_categoria").toString());
                obj.setNom_categoria(map.get("nom_categoria").toString());
                obj.setModulo(map.get("modulo").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                listCategoria.add(obj);
            }
        }
        return listCategoria;
    }

    public Object[] recuperarInfo(Categoria obj) {
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

        // SubCategorias
        obj.getListSubCategoria().clear();
        String respuesta2 = dao.QueryObj("select cod_subcategoria,cod_categoria,nom_subcategoria from m_subcategoria where cod_categoria='" + obj.getCod_categoria() + "'");
        obj.getLogs().clear();
        JsonArray JsonLog2 = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                SubCategoria scat = new SubCategoria();
                Map<String, Object> mapcorreo = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                scat.setCod_subcategoria(mapcorreo.get("cod_subcategoria").toString());
                scat.setCod_categoria(mapcorreo.get("cod_categoria").toString());
                scat.setNom_subcategoria(mapcorreo.get("nom_subcategoria").toString());

                obj.getListSubCategoria().add(scat);
            }
        }

        Resulta[0] = obj;
        return Resulta;
    }

    public List<Categoria> ListaBusqueda(String busqueda) {
        String consulta = "select * from m_categoria WHERE ";

        if (busqueda.contains("%")) {
            consulta += " (cod_categoria like '" + busqueda + "' or nom_categoria ilike '" + busqueda + "') ";
        } else {
            consulta += " (cod_categoria ='" + busqueda + "' or nom_categoria ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by cod_categoria desc Limit 50");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Categoria> listCategoria = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Categoria obj = new Categoria();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_categoria(map.get("cod_categoria").toString());
                obj.setNom_categoria(map.get("nom_categoria").toString());
                obj.setModulo(map.get("modulo").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                listCategoria.add(obj);
            }
        }
        return listCategoria;
    }

}
