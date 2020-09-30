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
import Modelo.Compras.Prove_Vendedor;
import Modelo.Compras.Provee_Bancos;
import Modelo.Compras.Proveedores;
import Modelo.Correos;
import Modelo.Sistema.Log_Transaccion;
import Modelo.Telefonos;
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
import java.math.BigInteger;
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
@Named(value = "proveedorservice")
@ApplicationScoped
public class ProveedorService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public ProveedorService() {
    }

    public Object[] Transaccion(Proveedores obj, String accion) {
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
                obj.setCod_provedor(Service.service.logProceso("proveedores").intValue());
            }
            transacciones.add(Service.service.log("Proveedores", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setActivo(obj.isActivoB() ? "S" : "N");

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("m_proveedores");
            o1.setDatos(gson.toJson(obj));
            o1.setBase(login.getBase());
            transacciones.add(o1);

//            for (Correos listCorreo : obj.getListCorreos()) {
//                listCorreo.setCod_persona(new BigDecimal(BigInteger.ZERO));
//                listCorreo.setTabla("m_proveedores");
//                listCorreo.setCod_log(logproceso);
//                objsql o2 = new objsql();
//                o2.setAccion(accion);
//                o2.setTabla("t_correos");
//                o2.setDatos(gson.toJson(listCorreo));
//
//                transacciones.add(o2);
//            }
//
//            for (Telefonos listTele : obj.getListTele()) {
//                listTele.setCod_persona(new BigDecimal(BigInteger.ZERO));
//                listTele.setTabla("m_proveedores");
//                listTele.setCod_log(logproceso);
//                objsql o3 = new objsql();
//                o3.setAccion(accion);
//                o3.setTabla("t_telefonos");
//                o3.setDatos(gson.toJson(listTele));
//
//                transacciones.add(o3);
//            }
            for (Prove_Vendedor listVender : obj.getList_vend()) {
                listVender.setCod_proveedor(obj.getCod_provedor());
                objsql o3 = new objsql();
                o3.setAccion(accion);
                o3.setTabla("td_provevendedor");
                o3.setBase(login.getBase());
                o3.setDatos(gson.toJson(listVender));

                transacciones.add(o3);
            }

            for (Provee_Bancos listCuenta : obj.getList_Bancos()) {
                listCuenta.setCod_proveedor(obj.getCod_provedor());
                objsql o4 = new objsql();
                o4.setAccion(accion);
                o4.setTabla("td_provebancos");
                o4.setBase(login.getBase());
                o4.setDatos(gson.toJson(listCuenta));

                transacciones.add(o4);
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

    public List<Proveedores> Lista() {
        String respuesta = dao.QueryObj("SELECT cod_provedor, cod_tipodoc, cod_documento, razon_social, \n"
                + " telefono, email, cod_ciudad, direccion, activo, cod_log\n"
                + " FROM public.m_proveedores");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Proveedores> listProveedores = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {

                Proveedores obj = new Proveedores();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                obj.setCod_tipodoc(map.get("cod_tipodoc").toString());
                obj.setCod_documento(map.get("cod_documento").toString());
                obj.setRazon_social(map.get("razon_social").toString());
                obj.setTelefono(map.get("telefono").toString());
                obj.setEmail(map.get("email").toString());
                obj.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
                obj.setDireccion(map.get("direccion").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                obj.setActivoB(obj.getActivo().equalsIgnoreCase("S"));

                listProveedores.add(obj);

            }
        }
        return listProveedores;
    }

    public List<Proveedores> ListaBusqueda(String busqueda) {
        String consulta = "SELECT cod_provedor, cod_tipodoc, cod_documento, razon_social, \n"
                + " telefono, email, cod_ciudad, direccion, activo, cod_log\n"
                + " FROM public.m_proveedores A WHERE ";

        if (busqueda.contains("%")) {
            consulta += " (A.cod_documento like '" + busqueda + "' or A.razon_social ilike '" + busqueda + "') ";
        } else {
            consulta += " (A.cod_documento ='" + busqueda + "' or A.cod_documento ='" + busqueda + "') ";
        }

        String respuesta = dao.QueryObj(consulta + " order by A.cod_provedor desc Limit 50");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Proveedores> listProveedores = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {

                Proveedores obj = new Proveedores();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                obj.setCod_provedor(new BigDecimal(map.get("cod_provedor").toString()).intValue());
                obj.setCod_tipodoc(map.get("cod_tipodoc").toString());
                obj.setCod_documento(map.get("cod_documento").toString());
                obj.setRazon_social(map.get("razon_social").toString());
                obj.setTelefono(map.get("telefono").toString());
                obj.setEmail(map.get("email").toString());
                obj.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
                obj.setDireccion(map.get("direccion").toString());
                obj.setActivo(map.get("activo").toString());
                obj.setCod_provedor(new BigDecimal(map.get("cod_log").toString()).intValue());

                obj.setActivoB(obj.getActivo().equalsIgnoreCase("S"));

                listProveedores.add(obj);

            }
        }
        return listProveedores;
    }

    public Object[] recuperarInfo(Proveedores obj) {
        Object Resulta[] = new Object[2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        //Logs
        String respuesta = dao.QueryObj("select proceso , usuario , operacion , cast (fecha as varchar(20)) fecha from sys_logs_trans where cod_proceso=" + obj.getCod_log());
        obj.getLogs().clear();
        JsonArray JsonLog = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Log_Transaccion log = new Log_Transaccion();
                Map<String, Object> mapLog = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                log.setOperacion(mapLog.get("operacion").toString());
                log.setProceso(mapLog.get("proceso").toString());
                log.setUsuario(mapLog.get("usuario").toString());
                log.setFecha(mapLog.get("fecha").toString());

                obj.getLogs().add(log);
            }
        }

        Resulta[0] = obj;

        //Ciudad
        String respuesta2 = dao.QueryObj("select C.cod_ciudad,'('||nombre_ciudad||')-'||nombre_dpto CiudadDpto from m_pais P inner join m_dpto D \n"
                + "on P.cod_pais=D.cod_pais \n"
                + "inner join m_ciudad C on D.cod_dpto=C.cod_dpto and C.cod_ciudad=" + obj.getCod_ciudad());

        JsonArray JsonCiudad = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonCiudad) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Ciudades ciudad = new Ciudades();
                Map<String, Object> mapCiudad = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                ciudad.setCod_ciudad(new BigDecimal(mapCiudad.get("cod_ciudad").toString()).intValue());
                ciudad.setCiudadDpto(mapCiudad.get("ciudaddpto").toString());
                Resulta[1] = ciudad;

            }
        }

        //Vendedores
        obj.getList_vend().clear();
        String respuesta3 = dao.QueryObj("SELECT cod_proveedor, cod_tipodoc, cod_documento, nombre, telefono\n"
                + "FROM public.td_provevendedor where cod_proveedor=" + obj.getCod_provedor());

        JsonArray jsonVendedor = parser.parse(respuesta3).getAsJsonArray();
        for (JsonElement jsonElement : jsonVendedor) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Prove_Vendedor proveedor = new Prove_Vendedor();
                Map<String, Object> mapVendedor = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                proveedor.setCod_proveedor(obj.getCod_provedor());
                proveedor.setCod_tipodoc(mapVendedor.get("cod_tipodoc").toString());
                proveedor.setCod_documento(mapVendedor.get("cod_documento").toString());
                proveedor.setNombre(mapVendedor.get("nombre").toString());
                proveedor.setTelefono(mapVendedor.get("telefono").toString());
                obj.getList_vend().add(proveedor);
            }
        }

        //Bancos
        obj.getList_Bancos().clear();
        String respuesta4 = dao.QueryObj("SELECT cod_proveedor, A.cod_banco, cod_cta, tipo_cta,B.descripcion\n"
                + "FROM public.td_provebancos A inner join m_bancos B\n"
                + "on A.cod_banco=B.cod_banco where cod_proveedor=" + obj.getCod_provedor());

        JsonArray jsonBanco = parser.parse(respuesta4).getAsJsonArray();
        for (JsonElement jsonElement : jsonBanco) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Provee_Bancos banco = new Provee_Bancos();
                Map<String, Object> mapBanco = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                banco.setCod_proveedor(obj.getCod_provedor());
                banco.setCod_banco(new BigDecimal(mapBanco.get("cod_banco").toString()).intValue());
                banco.setCod_cta(mapBanco.get("cod_cta").toString());
                banco.setTipo_cta(mapBanco.get("tipo_cta").toString());
                banco.setNom_banco(mapBanco.get("descripcion").toString());

                obj.getList_Bancos().add(banco);
            }
        }

        return Resulta;
    }

}
