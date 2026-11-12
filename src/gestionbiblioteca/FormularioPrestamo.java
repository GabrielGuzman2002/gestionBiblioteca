package gestionbiblioteca;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario Swing para el Proceso 3: registrar prestamos (3.1 a 3.5)
 * y consultarlos (3.6 y 3.7).
 */
public class FormularioPrestamo extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient PrestamoRegistroDAO dao = new PrestamoRegistroDAO();

    // datos del prestamo
    private final JComboBox<PrestamoRegistroDAO.Opcion> cmbUsuario = new JComboBox<>();
    private final JComboBox<PrestamoRegistroDAO.Opcion> cmbMaterial = new JComboBox<>();
    private final JTextField txtCodigo = new JTextField(18);
    private final JLabel lblFechaPrestamo = new JLabel();
    private final JSpinner spnDias = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
    private final JLabel lblFechaPrevista = new JLabel();

    // busqueda
    private final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Código", "Usuario", "Material", "Estado"});
    private final JTextField txtBuscar = new JTextField(15);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Código", "Usuario", "Material", "Tipo", "F. préstamo", "F. prevista", "F. devolución", "Estado"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public FormularioPrestamo() {
        super("Gestión de préstamos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        PrestamoRegistroDAO.Opcion prototipo
                = new PrestamoRegistroDAO.Opcion("", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        cmbUsuario.setPrototypeDisplayValue(prototipo);
        cmbMaterial.setPrototypeDisplayValue(prototipo);
        txtCodigo.setEditable(false);

        JPanel datos = new JPanel(new GridBagLayout());
        datos.setBorder(BorderFactory.createTitledBorder("Nuevo préstamo"));
        agregarFila(datos, 0, "Usuario:", cmbUsuario);
        agregarFila(datos, 1, "Material disponible:", cmbMaterial);
        agregarFila(datos, 2, "Código de préstamo:", txtCodigo);
        agregarFila(datos, 3, "Fecha de préstamo:", lblFechaPrestamo);
        agregarFila(datos, 4, "Días de préstamo:", spnDias);
        agregarFila(datos, 5, "Fecha prevista de devolución:", lblFechaPrevista);
        spnDias.addChangeListener(e -> actualizarFechaPrevista());

        JButton btnRegistrar = new JButton("Registrar préstamo");
        JButton btnNuevo = new JButton("Nuevo / Limpiar");
        btnRegistrar.addActionListener(e -> registrar());
        btnNuevo.addActionListener(e -> prepararNuevoPrestamo());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        botones.add(btnRegistrar);
        botones.add(btnNuevo);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(datos, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        // barra de busqueda + tabla de prestamos
        JButton btnBuscar = new JButton("Buscar");
        JButton btnActivos = new JButton("Ver activos");
        JButton btnTodos = new JButton("Mostrar todos");
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        btnActivos.addActionListener(e -> mostrar(dao.listarActivos()));
        btnTodos.addActionListener(e -> {
            txtBuscar.setText("");
            mostrar(dao.listarTodos());
        });
        JPanel barraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barraBusqueda.add(new JLabel("Buscar por:"));
        barraBusqueda.add(cmbFiltro);
        barraBusqueda.add(txtBuscar);
        barraBusqueda.add(btnBuscar);
        barraBusqueda.add(btnActivos);
        barraBusqueda.add(btnTodos);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createTitledBorder("Préstamos registrados"));
        centro.add(barraBusqueda, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        setLayout(new BorderLayout(8, 8));
        add(arriba, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        setSize(1000, 640);
        setLocationRelativeTo(null);
        prepararNuevoPrestamo();
        mostrar(dao.listarTodos());
    }

    private void agregarFila(JPanel panel, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = fila;
        gbc.gridx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    // recarga las listas (para ver las copias que quedan) y genera un codigo nuevo
    private void prepararNuevoPrestamo() {
        cmbUsuario.removeAllItems();
        for (PrestamoRegistroDAO.Opcion u : dao.listarUsuarios()) {
            cmbUsuario.addItem(u);
        }
        cmbMaterial.removeAllItems();
        for (PrestamoRegistroDAO.Opcion m : dao.listarMaterialesDisponibles()) {
            cmbMaterial.addItem(m);
        }
        // 3.4 codigo unico: P + fecha y hora
        txtCodigo.setText("P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        lblFechaPrestamo.setText(LocalDate.now().toString());
        spnDias.setValue(7);
        actualizarFechaPrevista();
    }

    private void actualizarFechaPrevista() {
        int dias = (Integer) spnDias.getValue();
        lblFechaPrevista.setText(LocalDate.now().plusDays(dias).toString());
    }

    private void registrar() {
        PrestamoRegistroDAO.Opcion usuario = (PrestamoRegistroDAO.Opcion) cmbUsuario.getSelectedItem();
        PrestamoRegistroDAO.Opcion material = (PrestamoRegistroDAO.Opcion) cmbMaterial.getSelectedItem();

        if (usuario == null) {
            aviso("No hay un usuario seleccionado. Registre primero a los usuarios.");
            return;
        }
        if (material == null) {
            aviso("No hay materiales disponibles para prestar.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate prevista = hoy.plusDays((Integer) spnDias.getValue());
        String codigo = txtCodigo.getText();

        String error = dao.registrar(codigo, usuario.getId(), material.getId(), hoy, prevista);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Préstamo " + codigo + " registrado.\n"
                    + "Debe devolverse antes del " + prevista + ".");
            prepararNuevoPrestamo();
            mostrar(dao.listarTodos());
        } else {
            aviso(error);
        }
    }

    private void buscar() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            mostrar(dao.listarTodos());
        } else {
            mostrar(dao.buscar((String) cmbFiltro.getSelectedItem(), criterio));
        }
    }

    private void mostrar(List<Object[]> filas) {
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    // para probar el formulario solo (clic derecho > Run File en NetBeans)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioPrestamo().setVisible(true));
    }
}
