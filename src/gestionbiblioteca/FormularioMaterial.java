package gestionbiblioteca;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario Swing para gestionar libros y revistas:
 * registrar (1.1, 1.2, 1.3), buscar (1.4), consultar (1.5), modificar (1.6) y eliminar (1.7).
 * Para modificar o eliminar se selecciona una fila de la tabla.
 */
public class FormularioMaterial extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient MaterialDAO dao = new MaterialDAO();

    // datos generales
    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtTitulo = new JTextField(25);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Libro", "Revista"});
    private final JTextField txtAnio = new JTextField(6);
    private final JTextField txtEditorial = new JTextField(20);
    private final JTextField txtCantidad = new JTextField(6);

    // datos del libro
    private final JTextField txtAutor = new JTextField(20);
    private final JTextField txtIsbn = new JTextField(15);
    private final JTextField txtPaginas = new JTextField(6);

    // datos de la revista
    private final JTextField txtEdicion = new JTextField(6);
    private final JComboBox<String> cmbPeriodicidad = new JComboBox<>(
            new String[]{"Semanal", "Quincenal", "Mensual", "Bimestral", "Trimestral", "Anual"});
    private final JTextField txtVolumen = new JTextField(6);

    // el panel cambia segun el tipo elegido (los nombres coinciden con el combo)
    private final CardLayout cartas = new CardLayout();
    private final JPanel panelEspecifico = new JPanel(cartas);

    private final JButton btnGuardar = new JButton("Guardar nuevo");
    private final JButton btnModificar = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    // busqueda
    private final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Código", "Título", "Autor", "Tipo"});
    private final JTextField txtBuscar = new JTextField(15);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Código", "Título", "Tipo", "Autor", "Año", "Editorial", "Cantidad", "Detalle"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    // materiales que se ven en la tabla (misma posicion que las filas) y el seleccionado
    private transient List<MaterialBibliografico> mostrados = new ArrayList<>();
    private transient MaterialBibliografico seleccionado = null;

    public FormularioMaterial() {
        super("Gestión de material bibliográfico");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel general = crearPanel("Datos generales");
        agregarFila(general, 0, "Código:", txtCodigo);
        agregarFila(general, 1, "Título:", txtTitulo);
        agregarFila(general, 2, "Tipo de material:", cmbTipo);
        agregarFila(general, 3, "Año de publicación:", txtAnio);
        agregarFila(general, 4, "Editorial:", txtEditorial);
        agregarFila(general, 5, "Cantidad disponible:", txtCantidad);

        JPanel libro = crearPanel("Datos del libro");
        agregarFila(libro, 0, "Autor:", txtAutor);
        agregarFila(libro, 1, "ISBN:", txtIsbn);
        agregarFila(libro, 2, "Páginas:", txtPaginas);

        JPanel revista = crearPanel("Datos de la revista");
        agregarFila(revista, 0, "Número de edición:", txtEdicion);
        agregarFila(revista, 1, "Periodicidad:", cmbPeriodicidad);
        agregarFila(revista, 2, "Volumen (opcional):", txtVolumen);

        panelEspecifico.add(libro, "Libro");
        panelEspecifico.add(revista, "Revista");
        cmbTipo.addActionListener(e -> cartas.show(panelEspecifico, (String) cmbTipo.getSelectedItem()));

        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        botones.add(btnGuardar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.add(general);
        arriba.add(panelEspecifico);
        arriba.add(botones);

        // barra de busqueda + tabla del catalogo
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todo");
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> {
            txtBuscar.setText("");
            mostrar(dao.listarTodos());
        });
        JPanel barraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barraBusqueda.add(new JLabel("Buscar por:"));
        barraBusqueda.add(cmbFiltro);
        barraBusqueda.add(txtBuscar);
        barraBusqueda.add(btnBuscar);
        barraBusqueda.add(btnTodos);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < mostrados.size()) {
                cargarEnFormulario(mostrados.get(fila));
            }
        });

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createTitledBorder("Catálogo (seleccione una fila para modificar o eliminar)"));
        centro.add(barraBusqueda, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        setLayout(new BorderLayout(8, 8));
        add(arriba, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        setSize(860, 760);
        setLocationRelativeTo(null);
        setModoEdicion(false);
        mostrar(dao.listarTodos());
    }

    private JPanel crearPanel(String titulo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        return p;
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

    // ---------- validaciones ----------
    private String leerTexto(JTextField campo, String nombre, int maximo) {
        String valor = campo.getText().trim();
        if (valor.isEmpty()) {
            campo.requestFocus();
            throw new IllegalArgumentException(nombre + " es obligatorio.");
        }
        if (valor.length() > maximo) {
            campo.requestFocus();
            throw new IllegalArgumentException(nombre + " no puede pasar de " + maximo + " caracteres.");
        }
        return valor;
    }

    private int leerEntero(JTextField campo, String nombre, int minimo, int maximo) {
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            campo.requestFocus();
            throw new IllegalArgumentException(nombre + " debe ser un número entero.");
        }
        if (valor < minimo || valor > maximo) {
            campo.requestFocus();
            throw new IllegalArgumentException(nombre + " debe estar entre " + minimo + " y " + maximo + ".");
        }
        return valor;
    }

    // valida lo escrito y arma un Libro o una Revista segun el tipo elegido
    private MaterialBibliografico leerFormulario(String codigo) {
        String titulo = leerTexto(txtTitulo, "El título", 150);
        String editorial = leerTexto(txtEditorial, "La editorial", 100);
        int anio = leerEntero(txtAnio, "El año de publicación", 1000, Year.now().getValue() + 1);
        int cantidad = leerEntero(txtCantidad, "La cantidad disponible", 0, 100000);

        if ("Libro".equals(cmbTipo.getSelectedItem())) {
            String autor = leerTexto(txtAutor, "El autor", 100);
            String isbn = leerTexto(txtIsbn, "El ISBN", 20);
            int paginas = leerEntero(txtPaginas, "El número de páginas", 1, 100000);
            return new Libro(codigo, titulo, autor, anio, editorial, cantidad, isbn, paginas);
        }
        int edicion = leerEntero(txtEdicion, "El número de edición", 1, 100000);
        String periodicidad = (String) cmbPeriodicidad.getSelectedItem();
        int volumen = txtVolumen.getText().trim().isEmpty()
                ? 0 : leerEntero(txtVolumen, "El volumen", 1, 100000);
        return new Revista(codigo, titulo, "", anio, editorial, cantidad, edicion, periodicidad, volumen);
    }

    // ---------- acciones ----------
    private void guardar() {
        try {
            String codigo = leerTexto(txtCodigo, "El código", 20);
            if (dao.existeCodigo(codigo)) {
                throw new IllegalArgumentException("Ya existe un material con el código " + codigo + ".");
            }
            MaterialBibliografico material = leerFormulario(codigo);

            if (dao.registrar(material)) {
                JOptionPane.showMessageDialog(this, "Material registrado correctamente.");
                limpiar();
                mostrar(dao.listarTodos());
            } else {
                errorBD("No se pudo guardar el material. Revise la conexión con la base de datos.");
            }
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void modificar() {
        if (seleccionado == null) {
            return;
        }
        try {
            MaterialBibliografico material = leerFormulario(seleccionado.getCodigo());
            if (dao.modificar(material)) {
                JOptionPane.showMessageDialog(this, "Material modificado correctamente.");
                limpiar();
                mostrar(dao.listarTodos());
            } else {
                errorBD("No se pudo modificar el material. Revise la conexión con la base de datos.");
            }
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionado == null) {
            return;
        }
        String codigo = seleccionado.getCodigo();
        if (dao.tienePrestamosActivos(codigo)) {
            aviso("No se puede eliminar: el material tiene préstamos activos (o no se pudo comprobar).");
            return;
        }
        if (dao.tienePrestamos(codigo)) {
            aviso("No se puede eliminar: el material tiene historial de préstamos que debe conservarse.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el material " + codigo + " - " + seleccionado.getTitulo() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.eliminar(codigo)) {
            JOptionPane.showMessageDialog(this, "Material eliminado correctamente.");
            limpiar();
            mostrar(dao.listarTodos());
        } else {
            errorBD("No se pudo eliminar el material. Revise la conexión con la base de datos.");
        }
    }

    private void buscar() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            mostrar(dao.listarTodos());
        } else {
            mostrar(dao.buscar(criterio, (String) cmbFiltro.getSelectedItem()));
        }
    }

    private void limpiar() {
        tabla.clearSelection();
        seleccionado = null;
        txtCodigo.setText("");
        txtTitulo.setText("");
        txtAnio.setText("");
        txtEditorial.setText("");
        txtCantidad.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtPaginas.setText("");
        txtEdicion.setText("");
        txtVolumen.setText("");
        cmbPeriodicidad.setSelectedIndex(0);
        cmbTipo.setSelectedIndex(0);
        cartas.show(panelEspecifico, "Libro");
        setModoEdicion(false);
        txtCodigo.requestFocus();
    }

    // al elegir una fila se llenan los campos; el codigo y el tipo no se pueden cambiar
    private void cargarEnFormulario(MaterialBibliografico m) {
        seleccionado = m;
        txtCodigo.setText(m.getCodigo());
        txtTitulo.setText(m.getTitulo());
        txtAnio.setText(String.valueOf(m.getAnioPublicacion()));
        txtEditorial.setText(m.getEditorial());
        txtCantidad.setText(String.valueOf(m.getCantidad()));

        if (m instanceof Libro) {
            Libro l = (Libro) m;
            cmbTipo.setSelectedItem("Libro");
            txtAutor.setText(l.getAutor());
            txtIsbn.setText(l.getIsbn());
            txtPaginas.setText(String.valueOf(l.getPaginas()));
        } else {
            Revista r = (Revista) m;
            cmbTipo.setSelectedItem("Revista");
            txtEdicion.setText(String.valueOf(r.getNumeroEdicion()));
            cmbPeriodicidad.setSelectedItem(r.getPeriodicidad());
            txtVolumen.setText(r.getVolumen() > 0 ? String.valueOf(r.getVolumen()) : "");
        }
        setModoEdicion(true);
    }

    // true = hay un material seleccionado (se puede modificar o eliminar, no guardar como nuevo)
    private void setModoEdicion(boolean edicion) {
        txtCodigo.setEnabled(!edicion);
        cmbTipo.setEnabled(!edicion);
        btnGuardar.setEnabled(!edicion);
        btnModificar.setEnabled(edicion);
        btnEliminar.setEnabled(edicion);
    }

    private void mostrar(List<MaterialBibliografico> lista) {
        mostrados = lista;
        modelo.setRowCount(0);
        for (MaterialBibliografico m : lista) {
            String detalle;
            if (m instanceof Libro) {
                Libro l = (Libro) m;
                detalle = "ISBN " + l.getIsbn() + " · " + l.getPaginas() + " págs.";
            } else {
                Revista r = (Revista) m;
                detalle = "Edición " + r.getNumeroEdicion() + " · " + r.getPeriodicidad()
                        + (r.getVolumen() > 0 ? " · Vol. " + r.getVolumen() : "");
            }
            modelo.addRow(new Object[]{m.getCodigo(), m.getTitulo(), m.getTipo(), m.getAutor(),
                m.getAnioPublicacion(), m.getEditorial(), m.getCantidad(), detalle});
        }
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void errorBD(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // para probar el formulario solo (clic derecho > Run File en NetBeans)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioMaterial().setVisible(true));
    }
}