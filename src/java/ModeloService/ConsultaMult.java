/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModeloService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class ConsultaMult {
    String consulta;
    List<String> Respuesta=new ArrayList();

    public ConsultaMult() {
    }
    
    

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public List<String> getRespuesta() {
        return Respuesta;
    }

    public void setRespuesta(List<String> Respuesta) {
        this.Respuesta = Respuesta;
    }
    
    
}
