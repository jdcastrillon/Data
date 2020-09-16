/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModeloService;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public class contenedor {

    private String metodo;
    private Map<String, Object> contenido = new HashMap<>();

    public contenedor() {
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public Map<String, Object> getContenido() {
        return contenido;
    }

    public void setContenido(Map<String, Object> contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "contenedor{" + "metodo=" + metodo + ", contenido=" + contenido + '}';
    }

}
