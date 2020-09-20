/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;


import Dao.DaoUsuario;
import Dao.UsuarioImp;
import ModeloService.contenedor;
import Modelo.Usuarios;
import ModeloService.iniciarSesion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author LENOVO
 */
@Named
@ApplicationScoped
public class UsuarioService {

    DaoUsuario ObjUsuario = new UsuarioImp();
    Gson gson = new Gson();

    public UsuarioService() {
        System.out.println("Contruccion UsuarioService");
    }

    public iniciarSesion iniciarSesion(iniciarSesion objUsuario) {
        Map<String, Object> contenido = new HashMap<>();
        iniciarSesion iniSesion = new iniciarSesion(objUsuario.getUsuario(), objUsuario.getClave());
        contenido.put("login", iniSesion);
        contenido.put("base", objUsuario.getBase());
        String JsonResponse = "";
        JsonResponse = ObjUsuario.iniciarSesion(gson.toJson(contenido));
        Map<String, Object> map = gson.fromJson(JsonResponse, new TypeToken<Map<String, Object>>() {
        }.getType());
        if (map.get("estado").equals("true")) {
            Map<String, Object> mapObj = gson.fromJson(map.get("mns").toString(), new TypeToken<Map<String, Object>>() {
            }.getType());
//            objUsuario.setNombreCompleto(mapObj.get("nombrecompleto").toString());
//            cargaDatosArbol();
        } else {
            objUsuario = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", map.get("mns").toString()));
        }

        return objUsuario;
    }

    private static void cargaDatosArbol() {
        DataService.Start_Service service = new DataService.Start_Service();
        DataService.Start port = service.getStartPort();
        port.cargaDatosArbol();
    }

    public DaoUsuario getObjUsuario() {
        return ObjUsuario;
    }

    public void setObjUsuario(DaoUsuario ObjUsuario) {
        this.ObjUsuario = ObjUsuario;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

}
