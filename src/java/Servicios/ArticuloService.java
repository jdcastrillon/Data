/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.Articulos;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.imp_tablas_numerador;
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
@Named(value = "articuloService")
@ApplicationScoped
public class ArticuloService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public ArticuloService() {
    }

    public Object[] Transaccion(Articulos obj, String accion) {
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
                obj.setCod_articulo(Service.service.logProceso("articulo").intValue());
            }
            transacciones.add(Service.service.log("Articulo", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setActivo_comercial(obj.isActivoComercial() == true ? "S" : "N");
            obj.setActivo_stock(obj.isActivoStock() == true ? "S" : "N");
            obj.setAlerta_Stock(obj.isAletarStock() == true ? "S" : "N");
            obj.setCod_negocio("0");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_articulos");
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            //Impactos Ajuste Numerador
            imp_tablas_numerador param = new imp_tablas_numerador();
            param.setCod_emp("Data");
            param.setNumerador("codigoarticulo");
            param.setCodigo(obj.getCodigo());
            param.setCodigo2(obj.getCodigo2());
            param.setAccion(accion);
            objsql o3 = new objsql();
            o3.setAccion("Impacto");
            o3.setTabla("imp_numerador");
            o3.setBase(login.getBase());
            o3.setDatos(gson.toJson(param));
            transacciones.add(o3);

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

    public List<Articulos> Lista() {
        String respuesta = dao.QueryObj("select A.*,B.nom_categoria,D.nom_unidad,C.nom_negocio from m_articulos A INNER JOIN m_categoria B ON A.categoria=B.cod_categoria\n"
                + "LEFT JOIN m_negocios C ON A.cod_negocio=C.cod_negocio\n"
                + "INNER JOIN m_unidastock D ON A.cod_unidad=D.cod_unidad "
                + " order by A.cod_articulo desc");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Articulos> listArticulos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Articulos obj = new Articulos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                obj.setCodigo(map.get("codigo").toString());
                obj.setNom_articulo(map.get("nom_articulo").toString());
                obj.setCod_unidad((map.get("cod_unidad") == null) ? "null" : map.get("cod_unidad").toString());
                obj.setCategoria(map.get("categoria").toString());
                obj.setSubcategoria((map.get("subcategoria") == null) ? "null" : map.get("subcategoria").toString());
                obj.setCod_negocio(map.get("cod_negocio").toString());
                obj.setActivo_stock(map.get("activo_stock").toString());
                obj.setStock_min(new BigDecimal(map.get("stock_min").toString()).intValue());
                obj.setStock_max(new BigDecimal(map.get("stock_max").toString()).intValue());
                obj.setAlerta_Stock(map.get("alerta_stock").toString());
                obj.setActivo_comercial(map.get("activo_comercial").toString());
                obj.setActivo_compras(map.get("activo_compras").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                obj.setNom_categoria(map.get("nom_categoria").toString());
                obj.setNom_negocio(map.get("nom_negocio").toString());
                obj.setNom_unidad(map.get("nom_unidad").toString());
                obj.setPorc_imp(new BigDecimal(map.get("porc_imp").toString()).intValue());

                //Otras Variables
                obj.setActivoComercial(obj.getActivo_comercial().equalsIgnoreCase("S"));
                obj.setActivoStock(obj.getActivo_stock().equalsIgnoreCase("S"));
                obj.setAletarStock(obj.getAlerta_Stock().equalsIgnoreCase("S"));

                listArticulos.add(obj);
            }
        }
        return listArticulos;
    }

    public List<Articulos> ListaBusqueda(String busqueda) {
        String consulta = "select A.*,B.nom_categoria,D.nom_unidad,C.nom_negocio from m_articulos A INNER JOIN m_categoria B ON A.categoria=B.cod_categoria\n"
                + "LEFT JOIN m_negocios C ON A.cod_negocio=C.cod_negocio\n"
                + "INNER JOIN m_unidastock D ON A.cod_unidad=D.cod_unidad WHERE ";

        if (busqueda.contains("%")) {
            consulta += " (A.codigo like '" + busqueda + "' or A.nom_articulo ilike '" + busqueda + "') ";
        } else {
            consulta += " (A.codigo ='" + busqueda + "' or A.nom_articulo ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by A.cod_articulo desc Limit 50");

        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Articulos> listArticulos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Articulos obj = new Articulos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                obj.setCodigo(map.get("codigo").toString());
                obj.setNom_articulo(map.get("nom_articulo").toString());
                obj.setCod_unidad((map.get("cod_unidad") == null) ? "null" : map.get("cod_unidad").toString());
                obj.setCategoria(map.get("categoria").toString());
                obj.setSubcategoria((map.get("subcategoria") == null) ? "null" : map.get("subcategoria").toString());
                obj.setCod_negocio(map.get("cod_negocio").toString());
                obj.setActivo_stock(map.get("activo_stock").toString());
                obj.setStock_min(new BigDecimal(map.get("stock_min").toString()).intValue());
                obj.setStock_max(new BigDecimal(map.get("stock_max").toString()).intValue());
                obj.setAlerta_Stock(map.get("alerta_stock").toString());
                obj.setActivo_comercial(map.get("activo_comercial").toString());
                obj.setActivo_compras(map.get("activo_compras").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                obj.setNom_categoria(map.get("nom_categoria").toString());
                obj.setNom_negocio(map.get("nom_negocio").toString());
                obj.setNom_unidad(map.get("nom_unidad").toString());
                obj.setPorc_imp(new BigDecimal(map.get("porc_imp").toString()).intValue());

                //Otras Variables
                obj.setActivoComercial(obj.getActivo_comercial().equalsIgnoreCase("S"));
                obj.setActivoStock(obj.getActivo_stock().equalsIgnoreCase("S"));
                obj.setAletarStock(obj.getAlerta_Stock().equalsIgnoreCase("S"));

                listArticulos.add(obj);
            }
        }

        return listArticulos;
    }

    public Object[] recuperarInfo(Articulos obj) {
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
