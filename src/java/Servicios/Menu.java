/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicios;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author LENOVO
 */
public class Menu {

    private MenuModel model;

    public Menu() {
    }

    public void cargaMenu() {
        model = new DefaultMenuModel();

        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Dynamic Submenu");

        DefaultMenuItem item = new DefaultMenuItem("External");
        item.setUrl("http://www.primefaces.org");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);

        model.addElement(firstSubmenu);
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }

}
