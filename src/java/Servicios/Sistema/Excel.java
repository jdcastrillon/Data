package Servicios.Sistema;

import java.io.IOException;
import java.util.List;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.export.ExporterOptions;


public class Excel extends Exporter{

    @Override
    public void export(FacesContext fc, DataTable dt, String string, boolean bln, boolean bln1, String string1, MethodExpression me, MethodExpression me1, ExporterOptions eo, MethodExpression me2) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void export(FacesContext fc, List<String> list, String string, boolean bln, boolean bln1, String string1, MethodExpression me, MethodExpression me1, ExporterOptions eo, MethodExpression me2) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void export(FacesContext fc, String string, List<DataTable> list, boolean bln, boolean bln1, String string1, MethodExpression me, MethodExpression me1, ExporterOptions eo, MethodExpression me2) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void exportCells(DataTable dt, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
}
