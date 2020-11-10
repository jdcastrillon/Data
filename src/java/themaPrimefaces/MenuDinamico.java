/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package themaPrimefaces;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author LENOVO
 */
@Named(value = "menuDinamico")
@RequestScoped
public class MenuDinamico {

    private MenuModel model;

    public MenuDinamico() {
    }

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        //First submenu
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Dynamic Submenu");
        firstSubmenu.setIcon("home");

        DefaultMenuItem item = new DefaultMenuItem("External");
        item.setUrl("http://www.primefaces.org");
        item.setIcon("close");
        firstSubmenu.addElement(item);

        model.addElement(firstSubmenu);

        //Second submenu
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Dynamic Actions");
        secondSubmenu.setIcon("home");

        item = new DefaultMenuItem("Save");
        item.setIcon("home");
        item.setCommand("#{testView.save}");

        secondSubmenu.addElement(item);

        item = new DefaultMenuItem("Delete");
        item.setIcon("home");
        item.setCommand("#{testView.delete}");
        item.setAjax(false);
        secondSubmenu.addElement(item);

        item = new DefaultMenuItem("Redirect");
        item.setCommand("#{testView.redirect}");
        secondSubmenu.addElement(item);

        model.addElement(secondSubmenu);
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }

}
