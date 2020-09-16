/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Ciudades;
import Modelo.Empresa;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
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
@Named(value = "empresaServicios")
@ApplicationScoped
public class EmpresaServicios {

    ObjectoDao dao = new ObjectoImp();
    Gson gson = new Gson();

    public EmpresaServicios() {
    }

    public Object[] Transaccion(Empresa objEmp, String accion) {
        Object Resulta[] = new Object[2];
        List<objsql> transacciones = new ArrayList();

        //Validacion Datos
        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

        System.out.println("Login : " + login.toString());
        //Log Registro
        BigDecimal logproceso = Service.service.logProceso("logs");
        System.out.println("Log : " + logproceso);
        transacciones.add(Service.service.log("Empresas", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

        //Registro Transaccion Empresa        
        objEmp.setCod_log(logproceso.intValue());
        objsql o1 = new objsql();
        o1.setAccion(accion);
        o1.setTabla("m_empresa");
        o1.setDatos(gson.toJson(objEmp));

        transacciones.add(o1);

        String resultado = dao.TransObj(gson.toJson(transacciones));
        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
        }.getType());
        Resulta[0] = map.get("estado");
        Resulta[1] = map.get("mns");
        return Resulta;
    }

    public List<Empresa> Lista() {
        String respuesta = dao.QueryObj("select * from m_empresa");
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Empresa> listaEmpresa = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Empresa emp = new Empresa();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_emp : " + map.get("cod_emp"));

                emp.setCod_emp(map.get("cod_emp").toString());
                emp.setNom_emp(map.get("nom_emp").toString());
                emp.setRazon_social(map.get("razon_social").toString());
                emp.setCod_doc(map.get("cod_doc").toString());
                emp.setNit(map.get("nit").toString());
                emp.setDireccion(map.get("direccion").toString());
                emp.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
                emp.setTelefono(map.get("telefono").toString());
                emp.setCorreo(map.get("correo").toString());
                emp.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
                listaEmpresa.add(emp);
            }
        }
        return listaEmpresa;
    }

    public Object[] recuperarInfo(Empresa objEmp) {
        Object Resulta[] = new Object[2];
        JsonParser parser = new JsonParser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        //Logs
        String respuesta = dao.QueryObj("select proceso , usuario , operacion , cast (fecha as varchar(20)) fecha from sys_logs_trans where cod_proceso=" + objEmp.getCod_log());
        objEmp.getLogs().clear();
        JsonArray JsonLog = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Log_Transaccion log = new Log_Transaccion();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                log.setOperacion(map.get("operacion").toString());
                log.setProceso(map.get("proceso").toString());
                log.setUsuario(map.get("usuario").toString());
                log.setFecha(map.get("fecha").toString());

                objEmp.getLogs().add(log);
            }
        }
        Resulta[0] = objEmp;

        //Ciudad
        Ciudades ciudad = new Ciudades();
        String respuesta2 = dao.QueryObj("select cod_ciudad,nombre_ciudad||'('||(nombre_dpto)||')' nombre from m_ciudad A , m_dpto B\n"
                + "where A.cod_Dpto=B.cod_dpto and cod_ciudad in (select cod_ciudad from m_empresa where cod_emp='" + objEmp.getCod_emp() + "')");
        objEmp.getLogs().clear();
        JsonArray JsonCiudad = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonCiudad) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {

                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                ciudad.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
                ciudad.setCiudadDpto(map.get("nombre").toString());

            }
        }
        Resulta[1] = ciudad;

        return Resulta;
    }

}
