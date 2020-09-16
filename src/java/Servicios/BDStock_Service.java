/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.DashboardSK_Depostio;
import Modelo.Bodega.Dashboard_stock;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
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
@Named(value = "bdstock_service")
@ApplicationScoped
public class BDStock_Service implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public BDStock_Service() {
    }

    public Object[] Transaccion(Dashboard_stock obj, String accion) {
        System.out.println("Entro a Servicio " + obj.toString());
        Object Resulta[] = new Object[2];

        return Resulta;
    }

    public int Stock(Dashboard_stock obj) {
        int stock = 0;
        String consulta = "select sum(cantidad) cantidad from s_stkestados where cod_estado='Stock'";

        String respuesta = dao.QueryObj(consulta);
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {

                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                stock = (new BigDecimal(map.get("cantidad").toString()).intValue());
            }
        }
        return stock;
    }

    public List<DashboardSK_Depostio> Consulta(Dashboard_stock obj) {
        String consulta = "";
        consulta = "select A.cod_emp,A.cod_tit,B.nom_deposito,sum(A.cantidad) cantidad\n"
                + "from s_stkdepositos A LEFT JOIN m_depositos B ON A.cod_tit=B.cod_deposito\n"
                + "group by A.cod_emp,A.cod_tit,B.nom_deposito";
        String respuesta = dao.QueryObj(consulta);
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<DashboardSK_Depostio> depostiosGeneral = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                DashboardSK_Depostio deposito = new DashboardSK_Depostio();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                deposito.setCod_emp(map.get("cod_emp").toString());
                deposito.setCod_deposito(map.get("cod_tit").toString());
                deposito.setNom_deposito(map.get("nom_deposito").toString());
                deposito.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                depostiosGeneral.add(deposito);

            }
        }
        return depostiosGeneral;
    }

    public List<DashboardSK_Depostio> DepositoXEstado(Dashboard_stock obj) {
        String consulta = "";

        consulta = "select A.cod_emp,A.cod_tit,B.nom_deposito,A.cod_estado,sum(A.cantidad) cantidad\n"
                + "from s_stkdepositos A LEFT JOIN m_depositos B ON A.cod_tit=B.cod_deposito\n"
                + "group by A.cod_emp,A.cod_tit,B.nom_deposito,A.cod_estado";

        String respuesta = dao.QueryObj(consulta);
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<DashboardSK_Depostio> depostiosGeneral = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                DashboardSK_Depostio deposito = new DashboardSK_Depostio();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                deposito.setCod_emp(map.get("cod_emp").toString());
                deposito.setCod_deposito(map.get("cod_tit").toString());
                deposito.setNom_deposito(map.get("nom_deposito").toString());
                deposito.setCod_estado(map.get("cod_estado").toString());
                deposito.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                depostiosGeneral.add(deposito);

            }
        }
        return depostiosGeneral;
    }

    public List<DashboardSK_Depostio> DetalleArticulo(Dashboard_stock obj) {
        String consulta = "";

        consulta = "select A.cod_emp,A.cod_tit,B.nom_deposito,A.cod_estado,A.cod_Articulo,C.nom_articulo,A.cantidad \n"
                + "from s_stkdepositos A LEFT JOIN m_depositos B ON A.cod_tit=B.cod_deposito\n"
                + "LEFT JOIN m_articulos C ON A.cod_Articulo=C.cod_articulo";

        String respuesta = dao.QueryObj(consulta);
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<DashboardSK_Depostio> depostiosGeneral = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                DashboardSK_Depostio deposito = new DashboardSK_Depostio();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                deposito.setCod_emp(map.get("cod_emp").toString());
                deposito.setCod_deposito(map.get("cod_tit").toString());
                deposito.setNom_deposito(map.get("nom_deposito").toString());
                deposito.setCod_estado(map.get("cod_estado").toString());
                deposito.setCod_articulo(map.get("cod_articulo").toString());
                deposito.setNom_articulo(map.get("nom_articulo").toString());
                deposito.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                
                
                depostiosGeneral.add(deposito);

            }
        }
        return depostiosGeneral;
    }

}
