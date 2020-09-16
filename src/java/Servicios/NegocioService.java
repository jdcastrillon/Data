/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Negocio;
import Modelo.Sucursal;
import Modelo.UserXSucursal;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;

/**
 *
 * @author LENOVO
 */
@Named(value = "negocioService")
@ApplicationScoped
public class NegocioService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    Gson gson = new Gson();

    public NegocioService() {
    }
    
     public Object[] Transaccion(Negocio obj, String accion) {
        Object Resulta[] = new Object[2];
        List<objsql> transacciones = new ArrayList();

        //Validacion Datos
        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

        //Log Registro
        BigDecimal logproceso = Service.service.logProceso("logs");
        transacciones.add(Service.service.log("Negocios", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

        //Registro Transaccion
        obj.setCod_log(logproceso.intValue());
        objsql o1 = new objsql();
        o1.setAccion(accion);
        o1.setTabla("m_negocios");
        o1.setDatos(gson.toJson(obj));

        transacciones.add(o1);

        String resultado = dao.TransObj(gson.toJson(transacciones));
        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
        }.getType());
        Resulta[0] = map.get("estado");
        Resulta[1] = map.get("mns");
        return Resulta;
    }

    public List<Negocio> Lista() {
        String respuesta = dao.QueryObj("select * from m_negocios");
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Negocio> listNegocios = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Negocio obj = new Negocio();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setCod_negocio(map.get("cod_negocio").toString());
                obj.setNom_negocio(map.get("nom_negocio").toString());
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setEstado(map.get("estado").toString());
               
                listNegocios.add(obj);
            }
        }
        return listNegocios;
    }

}
