package Servicios.Sistema;

import Dao.ObjectoDao;
import Dao.ObjectoImp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author LENOVO
 */
@Named(value = "validaciones")
@ApplicationScoped
public class Validaciones {

    ObjectoDao ObjVal = new ObjectoImp();
    Gson gson = new Gson();

    public Validaciones() {
        System.out.println("Contruccion Validaciones");
    }

    public boolean ValPrimaryKey(String consulta) {
        String respuesta = ObjVal.QueryObj(consulta);
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        int valorCount = 0;
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                valorCount = new BigDecimal(map.get("count").toString()).intValue();
            }
        }
        return valorCount > 0; //Si es mayor a cero devuelve true
    }
    
    //ValConsulta  : Si trae datos true
    public boolean Valconsulta(String consulta) {
        String respuesta = ObjVal.QueryObj(consulta);
        JsonParser parser = new JsonParser();
        JsonArray Jelementos = parser.parse(respuesta).getAsJsonArray();
        int valorCount = 0;
        for (JsonElement jsonElement : Jelementos) {
            if (!jsonElement.getAsString().equalsIgnoreCase("No hay Datos")) {
                Map<String, Object> map = gson.fromJson(jsonElement.getAsString(), new TypeToken<Map<String, Object>>() {
                }.getType());
                valorCount = new BigDecimal(map.get("count").toString()).intValue();
            }
        }
        return valorCount > 0; //Si es mayor a cero devuelve true
    }

}
