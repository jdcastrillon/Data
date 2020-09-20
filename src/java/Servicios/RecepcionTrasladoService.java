/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import ConecionService.Service;
import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.RecepcionTraslado;
import Modelo.Bodega.TrasladoBodega;
import Modelo.Bodega.TrasladoBodegaDT;
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
@Named(value = "recepciontrasladoservice")
@ApplicationScoped
public class RecepcionTrasladoService implements Serializable {

    ObjectoDao dao = new ObjectoImp();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    /**
     * Creates a new instance of ArticuloService
     */
    public RecepcionTrasladoService() {
    }

    public Object[] Transaccion(RecepcionTraslado obj, String accion) {
        System.out.println("Entro a Servicio " + obj.toString());
        Object Resulta[] = new Object[2];
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
            transacciones.add(Service.service.log("RecepTraslado", login.getUsuario(), accion, logproceso.intValue(), login.getBase()));

            //Registro Transaccion
            obj.setCod_log(logproceso.intValue());
//            obj.setNro_docum(Service.service.logProceso("stockajuste").intValue());
            obj.setFec_doc("@fecha" + obj.getFecha().getTime());

            objsql o1 = new objsql();
            o1.setAccion(accion);
            o1.setTabla("t_receptraslado");
            o1.setBase(login.getBase());
            o1.setDatos(gson.toJson(obj));

            transacciones.add(o1);

            //Detalle
            int linea = 1;
            for (TrasladoBodegaDT articulos : obj.getDetalleArt()) {
                articulos.setTrans(obj.getTrans());
                articulos.setLinea(linea);
                objsql o2 = new objsql();
                o2.setAccion(accion);
                o2.setTabla("td_receptraslado");
                o2.setBase(login.getBase());
                o2.setDatos(gson.toJson(articulos));

                transacciones.add(o2);

                linea++;
            }

            //Impactos
            imp_tablas param = new imp_tablas();
            param.setCod_trans(obj.getTrans());
            param.setAccion(accion);
            objsql o3 = new objsql();
            o3.setAccion("Impacto");
            o3.setTabla("imp_receptraslado");
            o3.setBase(login.getBase());
            o3.setDatos(gson.toJson(param));

            transacciones.add(o3);

            String resultado = dao.TransObj(gson.toJson(transacciones));
            Map<String, Object> map = gson.fromJson(resultado, new TypeToken<Map<String, Object>>() {
            }.getType());
            System.out.println(":" + map.get("mns").toString());
            Resulta[0] = map.get("estado");
            Resulta[1] = map.get("mns").toString().indexOf("#imp") > 0 ? map.get("mns").toString().substring(12, 100) : map.get("mns");
        } catch (JsonSyntaxException ex) {
            Resulta[0] = "Error";
            Resulta[1] = "Comuniquese con soporte";
        }

        return Resulta;
    }

    public List<RecepcionTraslado> Lista() {
        String respuesta = dao.QueryObj("SELECT trans, A.cod_emp, fec_doc, nro_docum, nro_doca, A.cod_deposito, A.cod_estado, \n"
                + "cod_deposito2, cod_estado2, observacion, A.cod_log,\n"
                + "B.nom_deposito,C.nom_deposito as nom_deposito2,\n"
                + "D.nom_estado,E.cod_estado as nom_estado2\n"
                + "FROM public.t_receptraslado A \n"
                + "left join m_depositos B on A.cod_deposito=B.cod_deposito\n"
                + "left join m_depositos C on A.cod_deposito2=C.cod_deposito\n"
                + "left join m_estados D on A.cod_estado=D.cod_estado\n"
                + "left join m_estados E on A.cod_estado=E.cod_estado\n"
                + "order by trans desc limit 500");
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<RecepcionTraslado> listRecepcionTraslado = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                try {
                    RecepcionTraslado obj = new RecepcionTraslado();
                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    System.out.println("cod_tipodoc : " + map.get("cod_tipodoc"));
                    obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
                    obj.setCod_emp(map.get("cod_emp").toString());
                    obj.setFec_doc(map.get("fec_doc").toString());
                    obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
                    obj.setNro_doca(new BigDecimal(map.get("nro_doca").toString()).intValue());
                    obj.setCod_estado(map.get("cod_estado").toString());
                    obj.setCod_deposito(map.get("cod_deposito").toString());
                    obj.setCod_deposito2(map.get("cod_deposito2").toString());
                    obj.setCod_estado2(map.get("cod_estado2").toString());
                    obj.setObservacion(map.get("observacion").toString());
                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());

                    obj.setNom_deposito(map.get("nom_deposito").toString());
                    obj.setNom_deposito2(map.get("nom_deposito2").toString());
                    obj.setNom_estado(map.get("nom_estado").toString());
                    obj.setNom_estado2(map.get("nom_estado2").toString());

                    System.out.println("FEcha : " + obj.getFec_doc());
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
                    obj.setFecha(date1);

                    listRecepcionTraslado.add(obj);
                } catch (ParseException ex) {
                    System.out.println("Error Lista Recepcion " + ex.getMessage());
                }
            }
        }
        return listRecepcionTraslado;
    }

    public List<RecepcionTraslado> ListaBusqueda(String busqueda) {
//        String consulta = "select trans,cod_emp,fec_doc,nro_docum,cod_estado,cod_deposito,"
//                + "cod_propietario,cod_motivo,observacion,signo,cod_log from t_ajuststock A WHERE";
//
//        if (busqueda.contains("%")) {
//            consulta += " cast(A.nro_docum as varchar(15)) like '" + busqueda + "' ";
//        } else {
//            consulta += " cast(A.nro_docum as varchar(15))='" + busqueda + "' ";
//        }
//
//        String respuesta = dao.QueryObj(consulta + " order by A.nro_docum desc Limit 50");
//        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        List<RecepcionTraslado> listRecepcionTraslado = new ArrayList();
//        for (JsonElement jsonElement : Jelementos) {
//            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
//                try {
//                    RecepcionTraslado obj = new RecepcionTraslado();
//                    Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
//                    }.getType());
//                    obj.setTrans(new BigDecimal(map.get("trans").toString()).intValue());
//                    obj.setCod_emp(map.get("cod_emp").toString());
//                    obj.setFec_doc(map.get("fec_doc").toString());
//                    obj.setNro_docum(new BigDecimal(map.get("nro_docum").toString()).intValue());
//                    obj.setCod_estado(map.get("cod_estado").toString());
//                    obj.setCod_deposito(map.get("cod_deposito").toString());
//                    obj.setCod_propietario(map.get("cod_propietario").toString());
//                    obj.setCod_motivo(new BigDecimal(map.get("cod_motivo").toString()).intValue());
//                    obj.setObservacion(map.get("observacion").toString());
//                    obj.setSigno(new BigDecimal(map.get("signo").toString()).intValue());
//                    obj.setCod_log(new BigDecimal(map.get("cod_log").toString()).intValue());
//
//                    System.out.println("FEcha : " + obj.getFec_doc());
//                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getFec_doc());
//                    obj.setFecha(date1);
//
//                    listRecepcionTraslado.add(obj);
//                } catch (ParseException ex) {
//                    Logger.getLogger(RecepcionTrasladoService.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
        return listRecepcionTraslado;
    }

    public Object[] recuperarInfo(RecepcionTraslado obj) {
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
        obj.getDetalleArt().clear();
        String respuesta2 = dao.QueryObj("SELECT trans, A.cod_articulo, cod_ubicacion, A.cod_unidad, cant_enviada, \n"
                + "cant_recibida, linea,B.codigo,B.nom_articulo\n"
                + "FROM public.td_receptraslado A inner join m_articulos B \n"
                + "on A.cod_articulo=B.cod_articulo where trans=" + obj.getTrans());

        JsonArray JsonLog2 = parser.parse(respuesta2).getAsJsonArray();
        for (JsonElement jsonElement : JsonLog2) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                TrasladoBodegaDT articulo = new TrasladoBodegaDT();
                Map<String, Object> maparticulo = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                articulo.setTrans(new BigDecimal(maparticulo.get("trans").toString()).intValue());
                articulo.setCod_articulo(new BigDecimal(maparticulo.get("cod_articulo").toString()).intValue());
                articulo.setCod_ubicacion(maparticulo.get("cod_ubicacion").toString());
                articulo.setCod_unidad(maparticulo.get("cod_unidad").toString());
                articulo.setCant_enviada(new BigDecimal(maparticulo.get("cant_enviada").toString()).intValue());
                articulo.setCant_recibida(new BigDecimal(maparticulo.get("cant_recibida").toString()).intValue());
                articulo.setTrans(new BigDecimal(maparticulo.get("linea").toString()).intValue());

                articulo.setCodigo(maparticulo.get("codigo").toString());
                articulo.setNom_articulo(maparticulo.get("nom_articulo").toString());

                obj.getDetalleArt().add(articulo);
            }
        }

        //objecto de traslado
        Resulta[0] = obj;

        return Resulta;
    }

}
