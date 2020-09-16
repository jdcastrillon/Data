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
public class JsonResponseTrans implements Serializable {

    private String proceso;
    private String estado;
    private String mns;

    public JsonResponseTrans() {
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMns() {
        return mns;
    }

    public void setMns(String mns) {
        this.mns = mns;
    }

}
