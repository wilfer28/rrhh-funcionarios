package com.iudigital.rrhh.vista;

import com.iudigital.rrhh.dao.*;
import com.iudigital.rrhh.dao.impl.*;
import com.iudigital.rrhh.excepciones.DaoException;
import com.iudigital.rrhh.modelo.Funcionario;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FuncionarioFrame extends JFrame {
    private final FuncionarioDao dao = new FuncionarioDaoImpl();
    private final CatalogoDao catalogos = new CatalogoDaoImpl();
    private final DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"ID", "Tipo doc.", "Número", "Nombres", "Apellidos", "Estado civil", "Teléfono"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public FuncionarioFrame() throws DaoException {
        super("Gestión de funcionarios - Recursos Humanos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 450);
        setLocationRelativeTo(null);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> {
            try {
                editar();
            } catch (DaoException ex) {
                System.getLogger(FuncionarioFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
        btnEliminar.addActionListener(e -> {
            try {
                eliminar();
            } catch (DaoException ex) {
                System.getLogger(FuncionarioFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
        btnRefrescar.addActionListener(e -> {
            try {
                cargar();
            } catch (DaoException ex) {
                System.getLogger(FuncionarioFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnNuevo); botones.add(btnEditar); botones.add(btnEliminar); botones.add(btnRefrescar);
        add(botones, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        cargar();
    }

    private void cargar() throws DaoException {
        modelo.setRowCount(0);
        for (Funcionario f : dao.listar()) {
            modelo.addRow(new Object[]{f.getId(), f.getTipoDocumento(), f.getNumeroIdentificacion(),
                f.getNombres(), f.getApellidos(), f.getEstadoCivil(), f.getTelefono()});
        }
    }

    private void nuevo() {
        try { abrir(null); } catch (DaoException e) { error(e.getMessage()); }
    }

    private void editar() throws DaoException {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { aviso("Seleccione un funcionario de la tabla."); return; }
        abrir(dao.buscarPorId((int) modelo.getValueAt(fila, 0)));
    }

    private void eliminar() throws DaoException {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { aviso("Seleccione un funcionario de la tabla."); return; }
        int op = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al funcionario y todo su grupo familiar y formación académica?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        dao.eliminar((int) modelo.getValueAt(fila, 0));
        cargar();
    }

    private void abrir(Funcionario f) throws DaoException {
        FuncionarioDialog d = new FuncionarioDialog(this,
            catalogos.listar("tipo_documento"), catalogos.listar("estado_civil"), f);
        d.setVisible(true);
        Funcionario r = d.getResultado();
        if (r == null) return;            // el usuario canceló
        if (f == null) dao.crear(r);
        else { r.setId(f.getId()); dao.actualizar(r); }
        cargar();
    }

    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private void aviso(String m) { JOptionPane.showMessageDialog(this, m, "Aviso", JOptionPane.WARNING_MESSAGE); }
}