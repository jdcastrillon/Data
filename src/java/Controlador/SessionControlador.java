/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import ModeloService.iniciarSesion;
import Servicios.Menu;
import Servicios.UsuarioService;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@SessionScoped
public class SessionControlador implements Serializable {

    // @ManagedProperty(value = "#{usuarioService}")
    @Inject
    private UsuarioService userService;

    private iniciarSesion objUsuario;

    private MenuModel model;

    @PostConstruct
    public void init() {
        try {
            getObjUsuario();
        } catch (Exception ex) {
            Logger.getLogger(SessionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SessionControlador() {
    }

    public String iniciarSesion() {
        System.out.println("Usuario : " + objUsuario.toString());
        objUsuario = userService.iniciarSesion(objUsuario);
        if (objUsuario != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("login", objUsuario);
        }
        Menu menuDynamic = new Menu();
        menuDynamic.cargaMenu();
        this.model = menuDynamic.getModel();

        return objUsuario == null ? "false" : "true";
    }

    public void cerrarSesion() {
        System.out.println("Entro a cerrar sesion");
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login") != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("login");
        }
    }

    public void validacionSesion() {
        System.out.println("Entro a validacionSesion");
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login") != null) {
            System.out.println("Sesion Activa");
        } else {
            System.out.println("Sesion InActiva");
        }
    }

    public UsuarioService getUserService() {
        return userService;
    }

    public void setUserService(UsuarioService userService) {
        this.userService = userService;
    }

    public iniciarSesion getObjUsuario() {
        if (objUsuario == null) {
            objUsuario = new iniciarSesion();
        }
        return objUsuario;
    }

    public void setObjUsuario(iniciarSesion objUsuario) {
        this.objUsuario = objUsuario;
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }

}
