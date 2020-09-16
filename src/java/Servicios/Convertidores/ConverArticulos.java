/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios.Convertidores;

import Modelo.Bodega.Articulos;
import Servicios.Sistema.Seleccion;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author LENOVO
 */
@ManagedBean
@FacesConverter(value = "converarticulos")
public class ConverArticulos implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        System.out.println(".. : getAsObject -" + value);
        if (value != null && value.trim().length() > 1) {
            try {
                List<Articulos> list_articulos = new ArrayList();
                Seleccion selServ = new Seleccion();
                list_articulos = selServ.cargaArticulo(value, 1);
                System.out.println("listCiudades : " + list_articulos.size());
                if (list_articulos.size() <= 0) {
                    return null;
                } else {
                    return list_articulos.get(0);
                }
                //return listCiudades.get(0)==null? new Ciudades():listCiudades.get(0);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        System.out.println(".. : getAsString " + object.toString());
        if (object != null) {
            return String.valueOf(((Articulos) object).getCod_articulo());
        } else {
            return null;
        }
    }

}
