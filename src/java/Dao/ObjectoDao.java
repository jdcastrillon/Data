/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dao;

/**
 *
 * @author LENOVO
 */
public interface ObjectoDao {

    public String TransObj(String jsonUser);

    public String QueryObj(String query);
    
    public String QueryReport(String query);
    
    public String QueryMultiObj(String query);
    
    
    
}
