/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Sucursal;
import Modelo.UserXSucursal;
import Modelo.Usuarios;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
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
import javax.faces.context.FacesContext;

/**
 *
 * @author LENOVO
 */
@Named(value = "sucursalService")
@ApplicationScoped
public class SucursalService {

    ObjectoDao dao = new ObjectoImp();
    Gson gson = new Gson();

    public SucursalService() {
    }

    public Object[] Transaccion(Sucursal obj, String accion) {
        Object Resulta[] = new Object[2];
        List<objsql> transacciones = new ArrayList();

        //Validacion Datos
        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

        //Log Registro
        BigDecimal logproceso = Service.service.logProceso("logs");
        transacciones.add(Service.service.log("Sucursales", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

        //Registro Transaccion
        obj.setCod_log(logproceso.intValue());
        objsql o1 = new objsql();
        o1.setAccion(accion);
        o1.setTabla("m_sucursales");
        o1.setDatos(gson.toJson(obj));

        transacciones.add(o1);

        //Registro Usuarios por Sucursal
        obj.getListUser().stream().map((usuarios) -> {
            UserXSucursal user = new UserXSucursal();
            user.setCod_sucursal(obj.getCod_sucursal());
            user.setUsuario(usuarios.getUsuario());
            return user;
        }).forEachOrdered((user) -> {
            objsql o2 = new objsql();
            o2.setAccion(accion);
            o2.setTabla("t_sucxuser");
            o2.setDatos(gson.toJson(user));
            transacciones.add(o2);
        });

        String resultado = dao.TransObj(gson.toJson(transacciones));
        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
        }.getType());
        Resulta[0] = map.get("estado");
        Resulta[1] = map.get("mns");
        return Resulta;
    }

    public List<Sucursal> Lista() {
        String respuesta = dao.QueryObj("select * from m_sucursales");
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Sucursal> listSucursales = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Sucursal obj = new Sucursal();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_emp(map.get("cod_emp").toString());
                obj.setCod_sucursal(map.get("cod_sucursal").toString());
                obj.setNom_sucursal(map.get("nom_sucursal").toString());
                obj.setDireccion(map.get("direccion").toString());
                obj.setTelefono(map.get("telefono").toString());
                obj.setEstado(map.get("estado").toString());
                obj.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                obj.setEstadoB(obj.getEstado().equalsIgnoreCase("A") ? true : false);
                listSucursales.add(obj);
            }
        }
        return listSucursales;
    }

}
