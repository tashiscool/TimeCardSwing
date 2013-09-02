/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gui.controller;

/**
 *
 * @author John
 */
public class Routes {
    static Routes INSTANCE = new Routes();
    
    private Routes()
    {
        
    }

    public static Routes getINSTANCE() {
        return INSTANCE;
    }

    public void showAdminMenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void showClockMenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
