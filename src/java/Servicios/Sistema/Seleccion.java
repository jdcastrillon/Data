/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios.Sistema;

import Dao.ObjectoDao;
import Dao.ObjectoImp;
import Modelo.Bodega.Articulos;
import Modelo.Ciudades;
import Modelo.Comercial.Clientes;
import Modelo.Bodega.Estados;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;

/**
 *
 * @author LENOVO
 */
@Named(value = "seleccion")
@ApplicationScoped
public class Seleccion {

    ObjectoDao objIni = new ObjectoImp();
    Gson gson = new Gson();

    public Seleccion() {
    }

    //Seleccion De Ciudades
    public List<Ciudades> cargaCiudad(String query, int condicion) {
        System.out.println("------------Service------------");
        String respuesta = "";
        if (condicion == 1) {
            respuesta = objIni.QueryObj("select C.cod_ciudad,nombre_ciudad,nombre_dpto,nombre_pais,'('||nombre_ciudad||')-'||nombre_dpto CiudadDpto from m_pais P inner join m_dpto D on P.cod_pais=D.cod_pais \n"
                    + "inner join m_ciudad C on D.cod_dpto=C.cod_dpto and C.cod_ciudad='" + query + "'");
        } else {
            respuesta = objIni.QueryObj("select C.cod_ciudad,nombre_ciudad,nombre_dpto,nombre_pais,'('||nombre_ciudad||')-'||nombre_dpto CiudadDpto from m_pais P inner join m_dpto D on P.cod_pais=D.cod_pais \n"
                    + "inner join m_ciudad C on D.cod_dpto=C.cod_dpto where nombre_ciudad ilike '%" + query + "%'");
        }

        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();

        List<Ciudades> listCiudades = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Ciudades objCiudad = new Ciudades();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                objCiudad.setCod_ciudad(new BigDecimal(map.get("cod_ciudad").toString()).intValue());
                objCiudad.setCiudadDpto(map.get("ciudaddpto").toString());
                System.out.println("--  : " + objCiudad.toString());
                listCiudades.add(objCiudad);
            }
        }

        return listCiudades;
    }

    //Seleccion De Estadps
    public List<Estados> selEstaddos(String query, int condicion) {
        System.out.println("------------Service Estados------------");
        String respuesta = "";
        if (condicion == 1) {
            respuesta = objIni.QueryObj("select cod_estado,nom_estado from m_estados where cod_Categoria='Stock' "
                    + "and activo='S' and cod_estado='" + query + "'");
        } else {
            respuesta = objIni.QueryObj("select cod_estado,nom_estado from m_estados where cod_Categoria='Stock' "
                    + "and activo='S' and nom_estado ilike '%" + query + "%'");
        }

        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();

        List<Estados> listEstados = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Estados obj = new Estados();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                obj.setCod_estado(map.get("cod_estado").toString());
                obj.setNom_estado(map.get("nom_estado").toString());
                System.out.println("--  : " + obj.toString());
                listEstados.add(obj);
            }
        }

        return listEstados;
    }

    //Seleccion Articulo
    public List<Articulos> cargaArticulo(String query, int condicion) {
        System.out.println("------------Service cargaArticulo------------");
        String respuesta = "";

        if (condicion == 1) {
            respuesta = objIni.QueryObj("select cod_articulo as cod_articulo2,codigo as codigo2,nom_Articulo as nom_articulo2"
                    + " from m_articulos where cod_articulo='" + query + "'");
        } else {
            respuesta = objIni.QueryObj("select * from articulos_buscar('" + query + "')");
        }

        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();

        List<Articulos> listArticulos = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Articulos obj = new Articulos();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                obj.setCod_articulo(new BigDecimal(map.get("cod_articulo2").toString()).intValue());
                obj.setCodigo(map.get("codigo2").toString());
                obj.setNom_articulo(map.get("nom_articulo2").toString());
                listArticulos.add(obj);
            }
        }

        return listArticulos;
    }

    //Seleccion Clientes
    public List<Clientes> SelClientes(String query, int condicion) {
        System.out.println("------------Service cargaArticulo------------");
        String respuesta = "";
        if (condicion == 1) {
            respuesta = objIni.QueryObj("select cod_cliente,cod_documento,(nombre||' '||apellido) Nombre, (cod_documento||'-'||nombre||' '||apellido) doc_nombre from m_clientes  where cod_cliente='" + query + "'");
        } else {
            respuesta = objIni.QueryObj("select cod_cliente,cod_documento,(nombre||' '||apellido) Nombre, (cod_documento||'-'||nombre||' '||apellido) doc_nombre from m_clientes \n"
                    + "where cod_documento ilike '%" + query + "%'");
        }

        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();

        List<Clientes> listClientes = new ArrayList();
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Clientes obj = new Clientes();
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                obj.setCod_cliente(new BigDecimal(map.get("cod_cliente").toString()).intValue());
                obj.setCod_documento(map.get("cod_documento").toString());
                obj.setNombre(map.get("nombre").toString());
                obj.setDoc_nombre(map.get("doc_nombre").toString());
                listClientes.add(obj);
            }
        }

        return listClientes;
    }

    public String ConsultaIreport(String consulta) {
        String json = "";
        json = objIni.QueryReport(consulta);
        System.out.println("Json : " + json.replace("\\", ""));
        return json;
    }

//    public List<String> condicionesFiltro() {
//        List<String> condiciones = new ArrayList();
//        condiciones.add("(..)");
//        condiciones.add("=");
//        condiciones.add(">");
//
//        return condiciones;
//    }
    public Object[] PDFDescargar2(String path) throws IOException {
        System.out.println("Reporte*********************************************");
        Object Resulta[] = new Object[2];
        String ruta = "C:\\Users\\LENOVO\\Desktop\\JD\\Proyectos\\Sistema Data\\Reportes\\";
        String rawJsonData = "{\"cod_deposito\":\"01\",\"nom_deposito\":\"Deposito Principal\",\"cod_emp\":\"Data\"}";

        String ru = (ruta + "Blank_A4_1" + ".jasper");
        try {
            //Load compiled jasper report that we created on first section.
//            JasperReport ds = (JasperReport) JRLoader.loadObject(new File("C:\\Users\\LENOVO\\Desktop\\JD\\Proyectos\\Sistema Data\\DataPymes\\DataPyme\\web\\WEB-INF\\reportes\\Blank_A4_1.jasper"));
            JsonDataSource datasource = new JsonDataSource(new ByteArrayInputStream(rawJsonData.getBytes("UTF-8")));
//            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());
//            JsonDataSource ds = new JsonDataSource(jsonDataStream);
            Map parameters = new HashMap();
            System.out.println("-- " + ru);
            System.out.println("-- " + parameters.toString());
            System.out.println("-- " + datasource.toString());

            JasperPrint jasperPrint = JasperFillManager.fillReport(new FileInputStream(new File(ru)), parameters, datasource);

            //PDF 
//            OutputStream outStream = response.getOutputStream();
//            System.out.println(":: " + outStream);
//            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            byte[] exportReportToPdf = JasperExportManager.exportReportToPdf(jasperPrint);

            FileOutputStream pdf = new FileOutputStream("C:\\Nodum\\" + "prueba.pdf");
            pdf.write(exportReportToPdf);
            pdf.close();
//            FacesContext.getCurrentInstance().responseComplete();

        } catch (JRException ex) {
            System.out.println("Error Reporte ex : " + ex.toString());
            Logger.getLogger(Seleccion.class.getName()).log(Level.SEVERE, null, ex);
        }

        Resulta[0] = "OK";
        Resulta[1] = "Reporte";
        return Resulta;
    }

    public Object[] PDFDescargar(String path) throws IOException {
        Object Resulta[] = new Object[2];
//        JsonDataSource datasource;
//        try {
//            String ruta = "C:\\Users\\LENOVO\\Desktop\\JD\\Proyectos\\Sistema Data\\Reportes";
//            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//            ServletContext context = (ServletContext) externalContext.getContext();
//            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
//
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + path + ".pdf\";");
//            response.setHeader("Cache-Control", "no-cache");
//            response.setHeader("Pragma", "no-cache");
//            response.setDateHeader("Expires", 0);
//            response.setContentType("application/pdf");
//
////            String ru = context.getRealPath(ruta + path + ".jasper");
//            String ru = ruta + "\\" + path + ".jasper";
//            System.out.println("Ru : " + ru);
//            if (new File(ru).exists()) {
//                System.out.println("Archivo Existe");
//            }
//            String rawJsonData = "{\"cod_deposito\":\"01\",\"nom_deposito\":\"Deposito Principal\",\"cod_emp\":\"Data\"}";
//
//            datasource = new JsonDataSource(new ByteArrayInputStream(rawJsonData.getBytes("UTF-8")));
//
//            Map parameters = new HashMap();
//            JasperPrint jasperPrint = JasperFillManager.fillReport(new FileInputStream(new File(ru)), parameters, datasource);
//
////            JasperPrint jasperPrint = JasperFillManager.fillReport(ru, consultaXopciones(path), con);
//            OutputStream outStream = response.getOutputStream();
//            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
//            FacesContext.getCurrentInstance().responseComplete();
//        } catch (JRException ex) {
//            Logger.getLogger(Seleccion.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("Error Reporte : " + ex.toString());
//        }

        Resulta[0] = "OK";
        Resulta[1] = "Reporte";
        return Resulta;
    }

    public void ReporteView() throws JRException, IOException {
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/Bodega.jasper"));

        String rawJsonData = "[{\"cod_deposito\":\"01\",\"nom_deposito\":\"Deposito Principal\",\"cod_emp\":\"Data\"}]";

        System.out.println(rawJsonData);
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes());

        JsonDataSource ds = new JsonDataSource(jsonDataStream);

        byte[] jp = JasperRunManager.runReportToPdf(jasper.getPath(), null, ds);
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(jp.length);
        try (ServletOutputStream outStream = response.getOutputStream()) {
            outStream.write(jp, 0, jp.length);
            outStream.flush();
            outStream.close();
        }
        FacesContext.getCurrentInstance().responseComplete();
    }

}
