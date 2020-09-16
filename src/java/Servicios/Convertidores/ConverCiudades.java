/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios.Convertidores;

import Modelo.Ciudades;
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
@FacesConverter(value = "converciudad")
public class ConverCiudades implements Converter {
    

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        System.out.println(".. : getAsObject -" + value);
        if (value != null && value.trim().length() > 0) {
            try {
                List<Ciudades> listCiudades = new ArrayList();
                Seleccion selServ=new Seleccion();
                listCiudades = selServ.cargaCiudad(value, 1);
                System.out.println("listCiudades : " + listCiudades.size());
                if(listCiudades.size()<=0){
                    return null;
                }else{
                    return listCiudades.get(0);
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
            return String.valueOf(((Ciudades) object).getCod_ciudad());
        } else {
            return null;
        }
    }

}
