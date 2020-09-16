/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios.Sistema;

import Dao.ObjectoDao;
import Dao.ObjectoImp;
import ModeloService.ConsultaMult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author LENOVO
 */
@Named(value = "inicializacion")
@ApplicationScoped
public class Inicializacion {

    ObjectoDao objIni = new ObjectoImp();
    Gson gson = new Gson();

    public Inicializacion() {
        System.out.println("Contruccion Inicializacion");
    }

    public int numerador(String secuencia) {
        String respuesta = objIni.QueryObj("SELECT nextval('" + secuencia + "')");
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
        return codigoSecuencia.intValue();
    }

    public int numerador_Controlado(String Empresa,String secuencia) {
        String respuesta = objIni.QueryObj("select numerador('"+Empresa+"','"+secuencia+"')");
        System.out.println("Respuesta de Secuencia " + respuesta);
        BigDecimal codigoSecuencia = null;
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : Jelementos) {
            Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
            }.getType());
            System.out.println("Secuencia : " + map.get("numerador"));
            //palabra = palabra.substring(0, palabra.indexOf("."));
            String substring = map.get("numerador").toString();
            codigoSecuencia = new BigDecimal(substring);
        }
        return codigoSecuencia.intValue();
    }

    public JsonArray listObjectos(String consulta) {
        System.out.println("Consulta : " + consulta);
        String respuesta = objIni.QueryObj(consulta);
        System.out.println("Respuesta : " + respuesta);
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        return Jelementos;
    }

    public ConsultaMult[] listObjMulti(List<String> Consultas) {
        System.out.println("Consultas : " + gson.toJson(Consultas));
        String respuesta = objIni.QueryMultiObj(gson.toJson(Consultas));
        System.out.println("Respuesta de iniCostoArt " + respuesta);
        ConsultaMult[] listaObj = gson.fromJson(respuesta, ConsultaMult[].class);

        return listaObj;
    }

    public Map<String, Object> fromJson(JsonElement jsonElement) {
        Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
        }.getType());
        return map;
    }

    public int StockDisponible(String cod_emp, int cod_articulo, String AplicaBodega, String Bodega) {
        String Consulta = "";
        if (AplicaBodega.equalsIgnoreCase("S")) {
            Consulta = "select cantidad from s_stkdepositos where cod_emp='" + cod_emp + "' "
                    + "and cod_articulo=" + cod_articulo + " and cod_estado='Disponible' "
                    + "and cod_tit='" + Bodega + "'";
        } else {
            Consulta = "select cantidad from s_stkestados where cod_emp='" + cod_emp + "' "
                    + "and cod_articulo=" + cod_articulo + " and cod_estado='Disponible'";
        }
        String respuesta = objIni.QueryObj(Consulta);
        System.out.println("Respuesta de Secuencia " + respuesta);
        BigDecimal codigoSecuencia = BigDecimal.ZERO;
        if (!respuesta.equalsIgnoreCase("[\"No hay Datos\"]")) {
            System.out.println("Entro a no hay datos");
            JsonParser parser = new JsonParser();
            JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
            for (JsonElement jsonElement : Jelementos) {
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("Secuencia : " + map.get("cantidad"));
                //palabra = palabra.substring(0, palabra.indexOf("."));
                String substring = map.get("cantidad").toString().substring(0, map.get("cantidad").toString().indexOf("."));
                codigoSecuencia = new BigDecimal(substring);
            }
        }
        return codigoSecuencia.intValue();
    }

    public double iniCostoArt(String cod_emp, String Provedor, int Articulo) {
        double costo = 0;
        List<String> Consultas = new ArrayList();
        Consultas.add("select imp_costo from sp_costoart where cod_emp='Data' and cod_proveedor='11'\n"
                + "  and cod_articulo='30'");
        Consultas.add("select imp_costo from sp_costoart where cod_emp='Data' and cod_proveedor='11'\n"
                + "  and cod_articulo='30'");
        System.out.println("Consultas : " + gson.toJson(Consultas));
        String respuesta = objIni.QueryMultiObj(gson.toJson(Consultas));
        System.out.println("Respuesta de iniCostoArt " + respuesta);
        ConsultaMult[] listaObj = gson.fromJson(respuesta, ConsultaMult[].class);

        for (ConsultaMult consultaMult : listaObj) {
            System.out.println(" - " + consultaMult.toString());
        }
        BigDecimal codigoSecuencia = BigDecimal.ZERO;
        if (!respuesta.equalsIgnoreCase("[\"No hay Datos\"]")) {
            System.out.println("Entro a no hay datos");
            JsonParser parser = new JsonParser();
            JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
            for (JsonElement jsonElement : Jelementos) {
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("Costo : " + map.get("imp_costo"));
                //palabra = palabra.substring(0, palabra.indexOf("."));
                String substring = map.get("imp_costo").toString().substring(0, map.get("imp_costo").toString().indexOf("."));
                codigoSecuencia = new BigDecimal(substring);
            }
        }
        return costo;
    }

    public Object[] eventos(String accion) {
        Object acciones[] = new Object[6];
        boolean aceptar = false;
        boolean editar = false;
        boolean eliminar = false;
        boolean nuevo = false;
        boolean buscar = false;
        boolean reporte = false;
        System.out.println("Accion de evento :" + accion);
        if (accion.equalsIgnoreCase("inicio")) {
            nuevo = false;
            editar = true;
            eliminar = true;
            buscar = false;
            aceptar = true;
            reporte = true;
        } else if (accion.equalsIgnoreCase("Nuevo")) {
            aceptar = false;
            nuevo = false;
            buscar = true;
            editar = true;
            eliminar = true;
            reporte = true;
        } else if (accion.equalsIgnoreCase("Ver")) {
            aceptar = true;
            nuevo = true;
            buscar = true;
            editar = false;
            eliminar = false;
            reporte = false;
        } else if (accion.equalsIgnoreCase("Editar")) {
            aceptar = false;
            nuevo = true;
            buscar = true;
            editar = false;
            eliminar = true;
            reporte = true;
        } else if (accion.equalsIgnoreCase("Eliminar")) {
            aceptar = false;
            nuevo = true;
            buscar = true;
            editar = true;
            eliminar = false;
            reporte = true;
        } else if (accion.equalsIgnoreCase("Buscar")) {
            aceptar = false;
            nuevo = true;
            buscar = false;
            editar = true;
            eliminar = true;
            reporte = true;
        } else if (accion.equalsIgnoreCase("Reporte")) {
            aceptar = false;
            nuevo = true;
            buscar = true;
            editar = true;
            eliminar = true;
            reporte = false;
        }
        acciones[0] = aceptar;
        acciones[1] = editar;
        acciones[2] = eliminar;
        acciones[3] = nuevo;
        acciones[4] = buscar;
        acciones[5] = reporte;

        return acciones;
    }

}
