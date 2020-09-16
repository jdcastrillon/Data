/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModeloService;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class iniciarSesion implements Serializable {

    private String usuario;
    private String clave;
    private String base;

    public iniciarSesion() {
    }

    public iniciarSesion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    

    @Override
    public String toString() {
        return "iniciarSesion{" + "usuario=" + usuario + ", clave=" + clave + '}';
    }

}
