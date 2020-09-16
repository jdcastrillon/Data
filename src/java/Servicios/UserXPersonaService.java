/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Correos;
import Modelo.Personas;
import Modelo.Telefonos;
import Modelo.Usuarios;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author LENOVO
 */
@Named
@ApplicationScoped
public class UserXPersonaService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();
    private String estado = "";

    public Object[] Transaccion(Usuarios ObjUser, Personas ObjPersona, String accion) {
        Object Resulta[] = new Object[2];
        List<objsql> transacciones = new ArrayList();

        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

        //Log Registro
        BigDecimal logproceso = Service.service.logProceso("logs");
        transacciones.add(Service.service.log("Persona", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

        //SECUENCIAS
        ObjPersona.setCod_persona(Service.service.logProceso("cod_persona"));

        //Registro Transaccion Persona   
        ObjPersona.setCod_log(logproceso);
        ObjPersona.setFec_nacimiento("@fecha" + ObjPersona.getT_fec_nacimiento().getTime());
        ObjPersona.setNombrecompleto(ObjPersona.getPrimernombre() + " " + ObjPersona.getSegundonombre() + " " + ObjPersona.getPrimerapellido() + " " + ObjPersona.getSegundoapellido());
        objsql o1 = new objsql();
        o1.setAccion(accion);
        o1.setTabla("m_persona");
        o1.setDatos(gson.toJson(ObjPersona));
        //jsonContenedor.getContenido().put("T2", o1);
        transacciones.add(o1);

        //Registro Transaccion Usuario    
        ObjUser.setCod_persona(ObjPersona.getCod_persona());
        ObjUser.setCod_log(logproceso);
        ObjUser.setActivo("A");
        objsql o2 = new objsql();
        o2.setAccion(accion);
        o2.setTabla("m_usuarios");
        o2.setDatos(gson.toJson(ObjUser));
        //jsonContenedor.getContenido().put("T3", o2);
        transacciones.add(o2);

        //Registro Transaccion Telefonos
        ObjPersona.getListTele().stream().map((telefonos) -> {
            telefonos.setTabla("m_persona");
            return telefonos;
        }).map((telefonos) -> {
            telefonos.setCod_persona(ObjPersona.getCod_persona());
            return telefonos;
        }).map((telefonos) -> {
            telefonos.setCod_log(logproceso);
            return telefonos;
        }).map((telefonos) -> {
            objsql o3 = new objsql();
            o3.setAccion(accion);
            o3.setTabla("t_telefonos");
            o3.setDatos(gson.toJson(telefonos));
            return o3;
        }).forEachOrdered((o3) -> {
            transacciones.add(o3);
        });

        //Registro Transaccion Emils
        ObjPersona.getListCorreos().stream().map((correo) -> {
            correo.setTabla("m_persona");
            return correo;
        }).map((correo) -> {
            correo.setCod_persona(ObjPersona.getCod_persona());
            return correo;
        }).map((correo) -> {
            correo.setCod_log(logproceso);
            return correo;
        }).map((correo) -> {
            objsql o4 = new objsql();
            o4.setAccion(accion);
            o4.setTabla("t_correos");
            o4.setDatos(gson.toJson(correo));
            return o4;
        }).forEachOrdered((o4) -> {
            transacciones.add(o4);
        });

        String resultado = dao.TransObj(gson.toJson(transacciones));
//        String resultado = "";
        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
        }.getType());
        Resulta[0] = map.get("estado");
        Resulta[1] = map.get("mns");
        return Resulta;
    }

//    public Object[] editarPersona(Usuarios ObjUser, Personas ObjPersona) {
//        Object Resulta[] = new Object[2];
//        List<objsql> transacciones = new ArrayList();
//
//        iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");
//
//        //Log Registro
//        transacciones.add(Service.service.log("Documentos", login.getUsuario(), "Editar", ObjUser.getCod_log().intValue()));
//
//        //Registro Transaccion Persona   
//        ObjPersona.setFec_nacimiento("@fecha" + ObjPersona.getT_fec_nacimiento().getTime());
//        ObjPersona.setNombrecompleto(ObjPersona.getPrimernombre() + " " + ObjPersona.getSegundonombre() + " " + ObjPersona.getPrimerapellido() + " " + ObjPersona.getSegundoapellido());
//        objsql o1 = new objsql();
//        o1.setAccion("Editar");
//        o1.setTabla("m_persona");
//        o1.setDatos(gson.toJson(ObjPersona));
//        //jsonContenedor.getContenido().put("T2", o1);
//        transacciones.add(o1);
//
//        //Registro Transaccion Usuario    
//        ObjUser.setCod_persona(ObjPersona.getCod_persona());
//        ObjUser.setActivo("A");
//        objsql o2 = new objsql();
//        o2.setAccion("Editar");
//        o2.setTabla("m_usuarios");
//        o2.setDatos(gson.toJson(ObjUser));
//        //jsonContenedor.getContenido().put("T3", o2);
//        transacciones.add(o2);
//
//        //Registro Transaccion Telefonos
//        ObjPersona.getListTele().stream().map((telefonos) -> {
//            telefonos.setTabla("m_persona");
//            return telefonos;
//        }).map((telefonos) -> {
//            telefonos.setCod_persona(ObjPersona.getCod_persona());
//            return telefonos;
//        }).map((telefonos) -> {
//            objsql o3 = new objsql();
//            o3.setAccion("Editar");
//            o3.setTabla("t_telefonos");
//            o3.setDatos(gson.toJson(telefonos));
//            return o3;
//        }).forEachOrdered((o3) -> {
//            transacciones.add(o3);
//        });
//
//        //Registro Transaccion Emils
//        ObjPersona.getListCorreos().stream().map((correo) -> {
//            correo.setTabla("m_persona");
//            return correo;
//        }).map((correo) -> {
//            correo.setCod_persona(ObjPersona.getCod_persona());
//            return correo;
//        }).map((correo) -> {
//            objsql o4 = new objsql();
//            o4.setAccion("Editar");
//            o4.setTabla("t_correos");
//            o4.setDatos(gson.toJson(correo));
//            return o4;
//        }).forEachOrdered((o4) -> {
//            transacciones.add(o4);
//        });
//
//        String resultado = objUsuarios.createUser(gson.toJson(transacciones));
////        String resultado = "";
//        Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
//        }.getType());
//        Resulta[0] = map.get("estado");
//        Resulta[1] = map.get("mns");
//        return Resulta;
//    }
    public List<Usuarios> Lista() {
        System.out.println("Lista Usuarios");
        String respuesta = dao.QueryObj("select A.usuario,A.cod_persona,A.passowrd,case when A.activo='A' then 'Activo' else 'Inactivo' end activo,A.cod_log,B.cod_documento,(B.primernombre||' '||B.segundonombre||' '||B.primerapellido||' '||B.segundoapellido) Nombre from m_usuarios A , m_persona B\n"
                + "where A.cod_persona=B.cod_persona");
        System.out.println("Respuesta : " + respuesta);
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<Usuarios> listUsuarios = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            System.out.println("json : " + jsonElement);
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Usuarios user = new Usuarios();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                System.out.println("map : " + map.toString());
                user.setUsuario(map.get("usuario").toString());
                user.setCod_persona(new BigDecimal(map.get("cod_persona").toString()));
                user.setActivo(map.get("activo").toString());
                user.setCod_log(new BigDecimal(map.get("cod_log").toString()));
                user.setCedula(map.get("cod_documento").toString());
                user.setNombre_completo(map.get("nombre").toString());
                listUsuarios.add(user);
            }
        }
        return listUsuarios;
    }

    public Object[] recuperarInfo(Usuarios objUSer, Personas objPersona) {
        Object Resulta[] = new Object[2];
        JsonParser parser = new JsonParser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
        try {
            //Objecto Persona
            String respuesta = dao.QueryObj("select * from m_persona where cod_persona=" + objUSer.getCod_persona());

            JsonElement JsonPersona = parser.parse(respuesta).getAsJsonArray();
            Map<String, Object> mapPersona = gson.fromJson(JsonPersona.getAsString(), new TypeToken<Map<String, Object>>() {
            }.getType());

            objPersona.setCod_persona(new BigDecimal(mapPersona.get("cod_persona").toString()));
            objPersona.setCod_documento(mapPersona.get("cod_documento").toString());
            objPersona.setCod_tipodoc(mapPersona.get("cod_tipodoc").toString());
            objPersona.setPrimernombre(mapPersona.get("primernombre").toString());
            objPersona.setSegundonombre(mapPersona.get("segundonombre").toString());
            objPersona.setPrimerapellido(mapPersona.get("primerapellido").toString());
            objPersona.setSegundoapellido(mapPersona.get("segundoapellido").toString());
            objPersona.setNombrecompleto(mapPersona.get("nombrecompleto").toString());
            objPersona.setSexo(mapPersona.get("sexo").toString());
            objPersona.setFec_nacimiento(mapPersona.get("fec_nacimiento").toString());
            Date date = dateFormat.parse(objPersona.getFec_nacimiento());
            objPersona.setT_fec_nacimiento(date);
            objPersona.setDireccion(mapPersona.get("direccion").toString());
            objPersona.setCod_log(new BigDecimal(mapPersona.get("cod_log").toString()));

            //Objecto Telefonos
            objPersona.getListTele().clear();
            String respuesta2 = dao.QueryObj("select * from t_telefonos where cod_persona=" + objUSer.getCod_persona());
            JsonArray JsonTelefono = parser.parse(respuesta2).getAsJsonArray();
            for (JsonElement jsonElement : JsonTelefono) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Telefonos tel = new Telefonos();
                    Map<String, Object> maptelefono = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    tel.setCod_persona(new BigDecimal(maptelefono.get("cod_persona").toString()));
                    tel.setNumero(maptelefono.get("numero").toString());
                    tel.setTipo(maptelefono.get("tipo").toString());
                    objPersona.getListTele().add(tel);
                }
            }

            //Objecto Correo
            objPersona.getListCorreos().clear();
            String respuesta3 = dao.QueryObj("select * from t_correos where cod_persona=" + objUSer.getCod_persona());
            JsonArray JsonCorreo = parser.parse(respuesta3).getAsJsonArray();
            for (JsonElement jsonElement : JsonCorreo) {
                if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                    Correos correo = new Correos();
                    Map<String, Object> mapcorreo = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    correo.setCod_persona(new BigDecimal(mapcorreo.get("cod_persona").toString()));
                    correo.setCorreo(mapcorreo.get("correo").toString());
                    correo.setTipo(mapcorreo.get("tipo").toString());
                    objPersona.getListCorreos().add(correo);
                }
            }

        } catch (NullPointerException e) {
            System.out.println("Excepció llençada");
        } catch (ParseException ex) {
            Logger.getLogger(UserXPersonaService.class.getName()).log(Level.SEVERE, null, ex);
        }

        Resulta[0] = objPersona;

        return Resulta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
