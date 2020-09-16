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
public class imp_tablas_numerador implements Serializable {
    
    private String cod_emp;
    private String numerador;
    private String codigo;
    private String codigo2;
    private String accion;    

    public imp_tablas_numerador() {
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getNumerador() {
        return numerador;
    }

    public void setNumerador(String numerador) {
        this.numerador = numerador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo2() {
        return codigo2;
    }

    public void setCodigo2(String codigo2) {
        this.codigo2 = codigo2;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
    
    
    
    
}
