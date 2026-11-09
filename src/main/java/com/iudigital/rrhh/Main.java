package com.iudigital.rrhh;

import com.iudigital.rrhh.excepciones.DaoException;
import com.iudigital.rrhh.vista.FuncionarioFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FuncionarioFrame().setVisible(true);
            } catch (DaoException ex) {
                System.getLogger(Main.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
}