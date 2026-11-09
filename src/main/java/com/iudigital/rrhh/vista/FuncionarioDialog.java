package com.iudigital.rrhh.vista;

import com.iudigital.rrhh.excepciones.ValidacionException;
import com.iudigital.rrhh.modelo.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FuncionarioDialog extends JDialog {
    private final JComboBox<Catalogo> cbTipoDoc, cbEstado;
    private final JComboBox<String> cbSexo = new JComboBox<>(new String[]{"M", "F"});
    private final JTextField txtNumero = new JTextField(), txtNombres = new JTextField(),
        txtApellidos = new JTextField(), txtDireccion = new JTextField(),
        txtTelefono = new JTextField(), txtFecha = new JTextField();
    private final DefaultTableModel mFam = new DefaultTableModel(
        new String[]{"Nombres", "Apellidos", "Fecha nac. (aaaa-mm-dd)", "Rol"}, 0);
    private final DefaultTableModel mAcad = new DefaultTableModel(
        new String[]{"Universidad", "Nivel de estudio", "Título"}, 0);
    private Funcionario resultado;

    public FuncionarioDialog(JFrame owner, List<Catalogo> tipos, List<Catalogo> estados, Funcionario f) {
        super(owner, f == null ? "Nuevo funcionario" : "Editar funcionario", true);
        cbTipoDoc = new JComboBox<>(tipos.toArray(Catalogo[]::new));
        cbEstado = new JComboBox<>(estados.toArray(Catalogo[]::new));

        JPanel datos = new JPanel(new GridLayout(0, 2, 6, 6));
        datos.add(new JLabel("Tipo de documento")); datos.add(cbTipoDoc);
        datos.add(new JLabel("Número de identificación")); datos.add(txtNumero);
        datos.add(new JLabel("Nombres")); datos.add(txtNombres);
        datos.add(new JLabel("Apellidos")); datos.add(txtApellidos);
        datos.add(new JLabel("Estado civil")); datos.add(cbEstado);
        datos.add(new JLabel("Sexo")); datos.add(cbSexo);
        datos.add(new JLabel("Dirección")); datos.add(txtDireccion);
        datos.add(new JLabel("Teléfono")); datos.add(txtTelefono);
        datos.add(new JLabel("Fecha de nacimiento (aaaa-mm-dd)")); datos.add(txtFecha);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Grupo familiar", panelTabla(mFam));
        tabs.add("Formación académica", panelTabla(mAcad));

        JButton ok = new JButton("Guardar"), cancelar = new JButton("Cancelar");
        ok.addActionListener(e -> guardar());
        cancelar.addActionListener(e -> dispose());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(ok); sur.add(cancelar);

        add(datos, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
        if (f != null) cargar(f);
        setSize(700, 600);
        setLocationRelativeTo(owner);
    }

    private JPanel panelTabla(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        JButton add = new JButton("Agregar fila"), del = new JButton("Quitar fila");
        add.addActionListener(e -> m.addRow(new Object[m.getColumnCount()]));
        del.addActionListener(e -> { if (t.getSelectedRow() >= 0) m.removeRow(t.getSelectedRow()); });
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT));
        b.add(add); b.add(del);
        JPanel p = new JPanel(new BorderLayout());
        p.add(b, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void cargar(Funcionario f) {   // modo edición: llena el formulario
        seleccionar(cbTipoDoc, f.getTipoDocumento().getId());
        seleccionar(cbEstado, f.getEstadoCivil().getId());
        cbSexo.setSelectedItem(f.getSexo());
        txtNumero.setText(f.getNumeroIdentificacion());
        txtNombres.setText(f.getNombres());
        txtApellidos.setText(f.getApellidos());
        txtDireccion.setText(f.getDireccion());
        txtTelefono.setText(f.getTelefono());
        txtFecha.setText(f.getFechaNacimiento().toString());
        for (GrupoFamiliar g : f.getGrupoFamiliar())
            mFam.addRow(new Object[]{g.getNombres(), g.getApellidos(),
                g.getFechaNacimiento() == null ? "" : g.getFechaNacimiento().toString(), g.getRol()});
        for (FormacionAcademica a : f.getFormaciones())
            mAcad.addRow(new Object[]{a.getUniversidad(), a.getNivelEstudio(), a.getTitulo()});
    }

    private void seleccionar(JComboBox<Catalogo> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++)
            if (cb.getItemAt(i).getId() == id) { cb.setSelectedIndex(i); return; }
    }

    private void guardar() {
        try {
            resultado = construir();
            dispose();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Funcionario construir() throws ValidacionException {
        if (txtNumero.getText().isBlank() || txtNombres.getText().isBlank() || txtApellidos.getText().isBlank())
            throw new ValidacionException("Número de identificación, nombres y apellidos son obligatorios.");
        if (!txtNumero.getText().trim().matches("[A-Za-z0-9]{5,20}"))
            throw new ValidacionException("El número de identificación debe tener entre 5 y 20 caracteres alfanuméricos.");
        if (!txtTelefono.getText().isBlank() && !txtTelefono.getText().trim().matches("[0-9]{7,15}"))
            throw new ValidacionException("El teléfono debe tener solo números (7 a 15 dígitos).");
        Funcionario f = new Funcionario();
        f.setTipoDocumento((Catalogo) cbTipoDoc.getSelectedItem());
        f.setEstadoCivil((Catalogo) cbEstado.getSelectedItem());
        f.setSexo((String) cbSexo.getSelectedItem());
        f.setNumeroIdentificacion(txtNumero.getText().trim());
        f.setNombres(txtNombres.getText().trim());
        f.setApellidos(txtApellidos.getText().trim());
        f.setDireccion(txtDireccion.getText().trim());
        f.setTelefono(txtTelefono.getText().trim());
        f.setFechaNacimiento(fecha(txtFecha.getText(), true));
        for (int i = 0; i < mFam.getRowCount(); i++) {
            String nom = texto(mFam, i, 0), rol = texto(mFam, i, 3);
            if (nom.isEmpty() && rol.isEmpty()) continue;   // fila vacía
            if (nom.isEmpty() || rol.isEmpty())
                throw new ValidacionException("En el grupo familiar, nombres y rol son obligatorios (fila " + (i + 1) + ").");
            GrupoFamiliar g = new GrupoFamiliar();
            g.setNombres(nom);
            g.setApellidos(texto(mFam, i, 1));
            g.setFechaNacimiento(fecha(texto(mFam, i, 2), false));
            g.setRol(rol);
            f.getGrupoFamiliar().add(g);
        }
        for (int i = 0; i < mAcad.getRowCount(); i++) {
            String uni = texto(mAcad, i, 0), niv = texto(mAcad, i, 1), tit = texto(mAcad, i, 2);
            if (uni.isEmpty() && niv.isEmpty() && tit.isEmpty()) continue;
            if (uni.isEmpty() || niv.isEmpty() || tit.isEmpty())
                throw new ValidacionException("En formación académica complete universidad, nivel y título (fila " + (i + 1) + ").");
            FormacionAcademica a = new FormacionAcademica();
            a.setUniversidad(uni); a.setNivelEstudio(niv); a.setTitulo(tit);
            f.getFormaciones().add(a);
        }
        return f;
    }

    private String texto(DefaultTableModel m, int fila, int col) {
        Object v = m.getValueAt(fila, col);
        return v == null ? "" : v.toString().trim();
    }

    private LocalDate fecha(String s, boolean obligatoria) throws ValidacionException {
        if (s == null || s.isBlank()) {
            if (obligatoria) throw new ValidacionException("La fecha de nacimiento es obligatoria.");
            return null;
        }
        try {
            LocalDate d = LocalDate.parse(s.trim());
            if (d.isAfter(LocalDate.now())) throw new ValidacionException("La fecha no puede ser futura.");
            return d;
        } catch (DateTimeParseException e) {
            throw new ValidacionException("Fecha inválida: use el formato aaaa-mm-dd (ejemplo 1990-05-31).");
        }
    }

    public Funcionario getResultado() { return resultado; }
}
