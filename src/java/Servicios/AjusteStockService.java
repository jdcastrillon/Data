/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.AjusteStock;
import Modelo.Bodega.AjusteStockDT;
import Modelo.Sistema.Log_Transaccion;
import ModeloService.imp_tablas;
import ModeloService.iniciarSesion;
import ModeloService.objsql;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author LENOVO
 */
@Named(value = "ajustestockservice")
@ApplicationScoped
public class AjusteStockService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public AjusteStockService() {
    }

    public Object[] Transaccion(AjusteStock obj, String accion) {
        System.out.println("Entro a Servicio " + obj.toString());
        Object Resulta[] = new Object[3];
        try {
            List<objsql> transacciones = new ArrayList();

            //Validacion Datos
            iniciarSesion login = (iniciarSesion) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("login");

            //Log Registro
            BigDecimal logproceso = new BigDecimal(obj.getCod_log());
            if (accion.equalsIgnoreCase("Nuevo")) {
                logproceso = Service.service.logProceso("logs");
                obj.setTrans(Service.service.logProceso("trans").intValue());
            }
            transacciones.add(Service.service.log("AjustStock", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
            obj.setCod_docum("AjustTK");
            obj.setCod_propietario("Empresa");
//            obj.setNro_docum(Service.service.logProceso("stockajuste").intValue());
            obj.setFec_doc("@fecha" + obj.getFecha().getTime());
//            obj.setSigno(obj.getSigno() == 0 ? 1 : -1);

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_ajuststock");
            o1.setBase(login.getBase());
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            //Detalle
            int linea = 1;
            for (AjusteStockDT ajusteStockDT : obj.getDetalleArt()) {
                ajusteStockDT.setTrans(obj.getTrans());
                ajusteStockDT.setLinea(linea);
                objsql o2 = new objsql();
                o2.setAccion(accion);
                o2.setTabla("td_ajuststock");
                o2.setBase(login.getBase());
                o2.setDatos(gson.toJson(ajusteStockDT));

                transacciones.add(o2);

                linea++;
            }

            //Impactos
            imp_tablas param = new imp_tablas();
            param.setCod_trans(obj.getTrans());
            param.setAccion(accion);
            objsql o3 = new objsql();
            o3.setAccion("Impacto");
            o3.setTabla("imp_AjusteStock");
            o3.setBase(login.getBase());
            o3.setDatos(gson.toJson(param));

            transacciones.add(o3);

            String resultado = dao.TransObj(gson.toJson(transacciones));
            Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
            }.getType());
            System.out.println(":" + map.get("mns").toString());
            Resulta[0] = map.get("estado");
            Resulta[1] = map.get("mns").toString().indexOf("#imp") > 0 ? map.get("mns").toString().substring(12, 100) : map.get("mns");
            Resulta[3] = obj.getTrans();
        } catch (JsonSyntaxException ex) {
            Resulta[0] = "Error";
            Resulta[1] = "Comuniquese con soporte";
            Resulta[3] = 0;
        }

        return Resulta;
    }

    public List<AjusteStock> Lista() {
        String respuesta = dao.QueryObj("select trans,cod_emp,fec_doc,nro_docum,cod_estado,cod_deposito,cod_propietario,cod_motivo,observacion,signo,cod_log from t_ajuststock A order by 1 desc");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<AjusteStock> listAjusteStock = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    AjusteStock obj = new AjusteStock();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                    obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                    obj.setCod_estado(map.get("cod_estado").toString());
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setCod_propietario(map.get("cod_propietario").toString());
                    obj.setCod_motivo(new BigDecimal(map.get("cod_motivo").toString()).intValue());
                    obj.setObservacion(map.get("observacion").toString());
                    obj.setSigno(new BigDecimal(map.get("signo").toString()).intValue());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    System.out.println("FEcha : " + obj.getFec_doc());
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setFecha(date1);

                    listAjusteStock.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(AjusteStockService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listAjusteStock;
    }

    public List<AjusteStock> ListaBusqueda(String busqueda) {
        String consulta = "select trans,cod_emp,fec_doc,nro_docum,cod_estado,cod_deposito,"
                + "cod_propietario,cod_motivo,observacion,signo,cod_log from t_ajuststock A WHERE";

        if (busqueda.contains("%")) {
            consulta += " cast(A.nro_docum as varchar(15)) like '" + busqueda + "' ";
        } else {
            consulta += " cast(A.nro_docum as varchar(15))='" + busqueda + "' ";
        }

        String respuesta = dao.QueryObj(consulta + " order by A.nro_docum desc Limit 50");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<AjusteStock> listAjusteStock = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    AjusteStock obj = new AjusteStock();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                    obj.setCod_estado(map.get("cod_estado").toString());
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setCod_propietario(map.get("cod_propietario").toString());
                    obj.setCod_motivo(new BigDecimal(map.get("cod_motivo").toString()).intValue());
                    obj.setObservacion(map.get("observacion").toString());
                    obj.setSigno(new BigDecimal(map.get("signo").toString()).intValue());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    System.out.println("FEcha : " + obj.getFec_doc());
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setFecha(date1);

                    listAjusteStock.add(obj);
                } catch (ParseException ex) {
                    Logger.getLogger(AjusteStockService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listAjusteStock;
    }

    public Object[] recuperarInfo(AjusteStock obj) {
        Object Resulta[] = new Object[2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        //Logs
        String respuesta = dao.QueryObj("select proceso , usuario , operacion , cast (fecha as varchar(20)) fecha from sys_logs_trans where cod_proceso=" + obj.getCod_log());
        obj.getLogs().clear();
        JsonArray JsonLog = parser.parse(respuesta).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Log_Transaccion log = new Log_Transaccion();
                Map<String, Object> mapcorreo = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                log.setOperacion(mapcorreo.get("operacion").toString());
                log.setProceso(mapcorreo.get("proceso").toString());
                log.setUsuario(mapcorreo.get("usuario").toString());
                log.setFecha(mapcorreo.get("fecha").toString());

                obj.getLogs().add(log);
            }
        }

        //Articulos
        String respuesta2 = dao.QueryObj("select A.*,B.codigo,B.nom_articulo from td_ajuststock A INNER JOIN  m_articulos B ON A.cod_articulo=B.cod_articulo where trans=" + obj.getTrans() + " order by linea");

        obj.getDetalleArt().clear();
        JsonArray JsonLog2 = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                AjusteStockDT obj2 = new AjusteStockDT();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                obj2.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                obj2.setCod_articulo(new BigDecimal(map.get("cod_articulo").toString()).intValue());
                obj2.setCod_ubicacion(map.get("cod_ubicacion").toString());
                obj2.setCod_unidad(new BigDecimal(map.get("cod_unidad").toString()).intValue());
                obj2.setCantidad(new BigDecimal(map.get("cantidad").toString()).intValue());
                obj2.setLinea(new BigDecimal(map.get("linea").toString()).intValue());
                obj2.setCodigo(map.get("codigo").toString());
                obj2.setNom_articulo(map.get("nom_articulo").toString());

                obj.getDetalleArt().add(obj2);
            }
        }

        Resulta[0] = obj;

        return Resulta;
    }

}
