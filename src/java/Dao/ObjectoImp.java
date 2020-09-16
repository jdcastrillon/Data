/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dao;

import ConecionService.Service;

/**
 *
 * @author LENOVO
 */
public class ObjectoImp implements ObjectoDao {

    @Override
    public String TransObj(String jsonUser) {
        return Service.service.jsonTrans(jsonUser);
    }

    @Override
    public String QueryObj(String query) {
        return Service.service.jsonquery(query);
    }

    @Override
    public String QueryMultiObj(String query) {
        return Service.service.jsonqueryMulti(query);
    }

    @Override
    public String QueryReport(String query) {
        return Service.service.jsonreporte(query);
    }

}
