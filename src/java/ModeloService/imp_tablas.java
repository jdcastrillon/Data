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
public class imp_tablas implements Serializable {

    //Parametros de impacto
    private int cod_trans;
    private String accion;

    public imp_tablas() {
    }

    public int getCod_trans() {
        return cod_trans;
    }

    public void setCod_trans(int cod_trans) {
        this.cod_trans = cod_trans;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

}
