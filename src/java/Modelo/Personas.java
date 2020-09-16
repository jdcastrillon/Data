package Modelo;

import Modelo.Sistema.Log_Transaccion;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Personas implements Serializable {

    public BigDecimal cod_persona;
    public String cod_documento;
    public String cod_tipodoc;
    public String primernombre;
    public String segundonombre;
    public String primerapellido;
    public String segundoapellido;
    public String nombrecompleto;
    public String sexo;
    public transient Date T_fec_nacimiento;
    public String fec_nacimiento;
    public String direccion;
    public int cod_ciudad;
    public int cod_telefono;
    public int cod_email;
    public BigDecimal cod_log;

    transient List<Telefonos> listTele = new ArrayList();
    transient List<Correos> listCorreos = new ArrayList();
    transient List<Log_Transaccion> logs = new ArrayList();

    public Personas() {

    }

    public BigDecimal getCod_persona() {
        return cod_persona;
    }

    public void setCod_persona(BigDecimal cod_persona) {
        this.cod_persona = cod_persona;
    }

    public String getCod_documento() {
        return cod_documento;
    }

    public void setCod_documento(String cod_documento) {
        this.cod_documento = cod_documento;
    }

    public String getCod_tipodoc() {
        return cod_tipodoc;
    }

    public void setCod_tipodoc(String cod_tipodoc) {
        this.cod_tipodoc = cod_tipodoc;
    }

    public String getPrimernombre() {
        return primernombre;
    }

    public void setPrimernombre(String primernombre) {
        this.primernombre = primernombre;
    }

    public String getSegundonombre() {
        return segundonombre;
    }

    public void setSegundonombre(String segundonombre) {
        this.segundonombre = segundonombre;
    }

    public String getPrimerapellido() {
        return primerapellido;
    }

    public void setPrimerapellido(String primerapellido) {
        this.primerapellido = primerapellido;
    }

    public String getSegundoapellido() {
        return segundoapellido;
    }

    public void setSegundoapellido(String segundoapellido) {
        this.segundoapellido = segundoapellido;
    }

    public String getNombrecompleto() {
        return nombrecompleto;
    }

    public void setNombrecompleto(String nombrecompleto) {
        this.nombrecompleto = nombrecompleto;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFec_nacimiento() {
        return fec_nacimiento;
    }

    public void setFec_nacimiento(String fec_nacimiento) {
        this.fec_nacimiento = fec_nacimiento;
    }

    public Date getT_fec_nacimiento() {
        return T_fec_nacimiento;
    }

    public void setT_fec_nacimiento(Date T_fec_nacimiento) {
        this.T_fec_nacimiento = T_fec_nacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getCod_ciudad() {
        return cod_ciudad;
    }

    public void setCod_ciudad(int cod_ciudad) {
        this.cod_ciudad = cod_ciudad;
    }

    public int getCod_telefono() {
        return cod_telefono;
    }

    public void setCod_telefono(int cod_telefono) {
        this.cod_telefono = cod_telefono;
    }

    public int getCod_email() {
        return cod_email;
    }

    public void setCod_email(int cod_email) {
        this.cod_email = cod_email;
    }

    public BigDecimal getCod_log() {
        return cod_log;
    }

    public void setCod_log(BigDecimal cod_log) {
        this.cod_log = cod_log;
    }

    public List<Telefonos> getListTele() {
        return listTele;
    }

    public void setListTele(List<Telefonos> listTele) {
        this.listTele = listTele;
    }

    public List<Correos> getListCorreos() {
        return listCorreos;
    }

    public void setListCorreos(List<Correos> listCorreos) {
        this.listCorreos = listCorreos;
    }

    public List<Log_Transaccion> getLogs() {
        return logs;
    }

    public void setLogs(List<Log_Transaccion> logs) {
        this.logs = logs;
    }    

    @Override
    public String toString() {
        return "Personas{" + "cod_persona=" + cod_persona + ", cod_documento=" + cod_documento + ", cod_tipodoc=" + cod_tipodoc + ", primernombre=" + primernombre + ", segundonombre=" + segundonombre + ", primerapellido=" + primerapellido + ", segundoapellido=" + segundoapellido + ", nombrecompleto=" + nombrecompleto + ", sexo=" + sexo + ", T_fec_nacimiento=" + T_fec_nacimiento + ", fec_nacimiento=" + fec_nacimiento + ", direccion=" + direccion + ", cod_ciudad=" + cod_ciudad + ", cod_telefono=" + cod_telefono + ", cod_email=" + cod_email + ", cod_log=" + cod_log + ", listTele=" + listTele + ", listCorreos=" + listCorreos + '}';
    }

}
