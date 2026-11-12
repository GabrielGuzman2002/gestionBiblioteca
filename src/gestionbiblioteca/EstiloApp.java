package gestionbiblioteca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Estilo visual de la aplicacion (colores, letras y botones).
 *
 * Se usa con UNA sola linea en el constructor de VentanaMenuPrincipal, sin tocar
 * el codigo que genera NetBeans (initComponents). Ver aplicarMenuPrincipal.
 * Para ver como queda sin abrir todo el sistema: clic derecho > Run File.
 */
public final class EstiloApp {

    // ---------- paleta de colores ----------
    // >>> Para probar otro color de fondo, cambie SOLO esta linea (los textos se adaptan solos) <<<
    public static final Color FONDO = new Color(0x26, 0x3E, 0x4E);

    private static final boolean FONDO_OSCURO = esOscuro(FONDO);

    // colores que dependen del fondo (letras claras si el fondo es oscuro y al reves)
    public static final Color ENCABEZADO = FONDO_OSCURO ? FONDO : Color.WHITE;
    public static final Color LATERAL = FONDO_OSCURO ? oscurecer(FONDO, 0.40) : new Color(0x12, 0x26, 0x3F);
    public static final Color LINEA_LATERAL = mezclar(LATERAL, Color.WHITE, 0.14);
    public static final Color BORDE = FONDO_OSCURO ? mezclar(FONDO, Color.WHITE, 0.14) : new Color(0xE2, 0xE8, 0xF0);
    public static final Color TITULO = FONDO_OSCURO ? Color.WHITE : new Color(0x12, 0x26, 0x3F);
    public static final Color TEXTO_PRINCIPAL = FONDO_OSCURO ? new Color(0xF1, 0xF5, 0xF9) : new Color(0x1E, 0x29, 0x3B);
    public static final Color TEXTO_SUAVE = FONDO_OSCURO ? new Color(0xA9, 0xBC, 0xCB) : new Color(0x64, 0x74, 0x8B);
    public static final Color TEXTO_LATERAL = new Color(0x94, 0xA3, 0xB8);

    public static final Color AZUL = new Color(0x25, 0x63, 0xEB);
    public static final Color VIOLETA = new Color(0x7C, 0x3A, 0xED);
    public static final Color AMBAR = new Color(0xD9, 0x77, 0x06);
    public static final Color TURQUESA = new Color(0x0D, 0x94, 0x88);
    public static final Color VERDE = new Color(0x16, 0xA3, 0x4A);
    public static final Color PIZARRA = new Color(0x47, 0x55, 0x69);

    private static final Color DESHABILITADO_FONDO = FONDO_OSCURO ? mezclar(FONDO, Color.WHITE, 0.10) : new Color(0xD5, 0xDB, 0xE5);
    private static final Color DESHABILITADO_TEXTO = FONDO_OSCURO ? mezclar(FONDO, Color.WHITE, 0.45) : new Color(0x8A, 0x96, 0xA8);

    private EstiloApp() {
    }

    private static Font fuente(int estilo, int tamano) {
        return new Font("Segoe UI", estilo, tamano);
    }

    private static Border relleno(int arriba, int izq, int abajo, int der) {
        return BorderFactory.createEmptyBorder(arriba, izq, abajo, der);
    }

    private static boolean esOscuro(Color c) {
        return (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) < 140;
    }

    private static Color mezclar(Color a, Color b, double t) {
        return new Color((int) Math.round(a.getRed() * (1 - t) + b.getRed() * t),
                (int) Math.round(a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) Math.round(a.getBlue() * (1 - t) + b.getBlue() * t));
    }

    private static Color oscurecer(Color c, double factor) {
        return new Color((int) Math.round(c.getRed() * (1 - factor)),
                (int) Math.round(c.getGreen() * (1 - factor)),
                (int) Math.round(c.getBlue() * (1 - factor)));
    }

    // ---------- botones ----------
    // boton redondeado de color: se oscurece al pasar el mouse y se apaga si esta deshabilitado
    private static class UiBoton extends BasicButtonUI {

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel modelo = b.getModel();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color fondo = b.getBackground();
            Color texto = b.getForeground();
            if (!b.isEnabled()) {
                fondo = DESHABILITADO_FONDO;
                texto = DESHABILITADO_TEXTO;
            } else if (modelo.isPressed()) {
                fondo = oscurecer(fondo, 0.25);
            } else if (modelo.isRollover()) {
                fondo = oscurecer(fondo, 0.12);
            }
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 18, 18);

            String t = b.getText();
            if (t != null) {
                g2.setFont(b.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (c.getWidth() - fm.stringWidth(t)) / 2;
                int y = (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(texto);
                g2.drawString(t, x, y);
            }
            g2.dispose();
        }
    }

    // se puede usar tambien en otras ventanas: EstiloApp.estilizarBoton(miBoton, EstiloApp.AZUL);
    public static void estilizarBoton(AbstractButton b, Color color) {
        b.setUI(new UiBoton());
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(fuente(Font.BOLD, 15));
        b.setBorder(relleno(0, 0, 0, 0));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(b.isEnabled() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        b.setPreferredSize(new Dimension(200, 96));
    }

    // ---------- menu principal ----------
    /**
     * Cambia el aspecto del menu principal. Se llama una vez, al FINAL del
     * constructor de VentanaMenuPrincipal (despues de las validaciones de administrador):
     *
     * EstiloApp.aplicarMenuPrincipal(this, pnlMenu, pnlEncabezado, pnlContenido,
     *     new javax.swing.JLabel[]{jLabel3, jLabel9},
     *     new javax.swing.JLabel[]{jLabel1, jLabel2},
     *     new javax.swing.JLabel[]{jLabel4, jLabel5},
     *     new javax.swing.JLabel[]{jLabel6, jLabel7, jLabel8},
     *     new javax.swing.JButton[]{btnRegistrar, btnPrestar, btnDevoluciones,
     *                               btnGestionar, btnAgregarUsuario, btnGenerarReporte});
     *
     * No cambia ninguna accion de los botones. Tambien corrige algunos textos
     * (acentos y ortografia) y los textos cortados por los anchos fijos del diseno.
     */
    public static void aplicarMenuPrincipal(JFrame ventana, JPanel pnlMenu, JPanel pnlEncabezado,
            JPanel pnlContenido, JLabel[] lateral, JLabel[] encabezado, JLabel[] bienvenida,
            JLabel[] pie, JButton[] botones) {
        construirMenuPrincipal(ventana.getContentPane(), pnlMenu, pnlEncabezado, pnlContenido,
                lateral, encabezado, bienvenida, pie, botones);
        ventana.setMinimumSize(new Dimension(900, 560));
        ventana.setSize(1050, 650);
        ventana.setLocationRelativeTo(null);
        ventana.revalidate();
        ventana.repaint();
    }

    static void construirMenuPrincipal(Container raiz, JPanel pnlMenu, JPanel pnlEncabezado,
            JPanel pnlContenido, JLabel[] lateral, JLabel[] encabezado, JLabel[] bienvenida,
            JLabel[] pie, JButton[] botones) {

        // ----- barra lateral (azul marino) -----
        pnlMenu.removeAll();
        pnlMenu.setLayout(new BorderLayout());
        pnlMenu.setOpaque(true);
        pnlMenu.setBackground(LATERAL);
        pnlMenu.setBorder(relleno(0, 0, 0, 0));
        pnlMenu.setPreferredSize(new Dimension(230, 0));

        JLabel marca = lateral[0];
        marca.setFont(fuente(Font.BOLD, 22));
        marca.setForeground(Color.WHITE);
        marca.setHorizontalAlignment(SwingConstants.LEFT);
        marca.setBorder(relleno(28, 24, 24, 24));
        JPanel zonaMarca = new JPanel(new BorderLayout());
        zonaMarca.setOpaque(false);
        zonaMarca.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA_LATERAL));
        zonaMarca.add(marca, BorderLayout.CENTER);

        JLabel pieLateral = lateral[1];
        pieLateral.setText("Sistema de gestión");
        pieLateral.setFont(fuente(Font.PLAIN, 12));
        pieLateral.setForeground(TEXTO_LATERAL);
        pieLateral.setHorizontalAlignment(SwingConstants.LEFT);
        pieLateral.setBorder(relleno(16, 24, 16, 24));
        JPanel zonaPie = new JPanel(new BorderLayout());
        zonaPie.setOpaque(false);
        zonaPie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LINEA_LATERAL));
        zonaPie.add(pieLateral, BorderLayout.CENTER);

        pnlMenu.add(zonaMarca, BorderLayout.NORTH);
        pnlMenu.add(zonaPie, BorderLayout.SOUTH);

        // ----- encabezado (blanco) -----
        pnlEncabezado.removeAll();
        pnlEncabezado.setLayout(new BoxLayout(pnlEncabezado, BoxLayout.Y_AXIS));
        pnlEncabezado.setOpaque(true);
        pnlEncabezado.setBackground(ENCABEZADO);
        pnlEncabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE), relleno(22, 32, 22, 32)));

        JLabel titulo = encabezado[0];
        titulo.setFont(fuente(Font.BOLD, 24));
        titulo.setForeground(TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = encabezado[1];
        subtitulo.setFont(fuente(Font.PLAIN, 14));
        subtitulo.setForeground(TEXTO_SUAVE);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlEncabezado.add(titulo);
        pnlEncabezado.add(Box.createVerticalStrut(4));
        pnlEncabezado.add(subtitulo);

        // ----- contenido: saludo, botones en rejilla y pie -----
        pnlContenido.removeAll();
        pnlContenido.setLayout(new BorderLayout(0, 24));
        pnlContenido.setOpaque(true);
        pnlContenido.setBackground(FONDO);
        pnlContenido.setBorder(relleno(28, 32, 24, 32));

        JLabel saludo = bienvenida[0];
        saludo.setText("Bienvenido al sistema");
        saludo.setFont(fuente(Font.BOLD, 22));
        saludo.setForeground(TEXTO_PRINCIPAL);
        saludo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel instruccion = bienvenida[1];
        instruccion.setText("Seleccione una opción para poder continuar");
        instruccion.setFont(fuente(Font.PLAIN, 14));
        instruccion.setForeground(TEXTO_SUAVE);
        instruccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel zonaSaludo = new JPanel();
        zonaSaludo.setLayout(new BoxLayout(zonaSaludo, BoxLayout.Y_AXIS));
        zonaSaludo.setOpaque(false);
        zonaSaludo.add(saludo);
        zonaSaludo.add(Box.createVerticalStrut(4));
        zonaSaludo.add(instruccion);

        Color[] colores = {AZUL, VIOLETA, AMBAR, TURQUESA, VERDE, PIZARRA};
        JPanel rejilla = new JPanel(new GridLayout(2, 3, 18, 18));
        rejilla.setOpaque(false);
        for (int i = 0; i < botones.length; i++) {
            estilizarBoton(botones[i], colores[i % colores.length]);
            rejilla.add(botones[i]);
        }
        JPanel zonaBotones = new JPanel(new BorderLayout());
        zonaBotones.setOpaque(false);
        zonaBotones.add(rejilla, BorderLayout.NORTH);

        pie[0].setFont(fuente(Font.BOLD, 13));
        pie[0].setForeground(TEXTO_PRINCIPAL);
        pie[1].setText("Administra libros, revistas y usuarios");
        pie[2].setText("de forma sencilla y eficiente");
        JPanel zonaPieContenido = new JPanel();
        zonaPieContenido.setLayout(new BoxLayout(zonaPieContenido, BoxLayout.Y_AXIS));
        zonaPieContenido.setOpaque(false);
        zonaPieContenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE), relleno(14, 0, 0, 0)));
        for (int i = 0; i < pie.length; i++) {
            if (i > 0) {
                pie[i].setFont(fuente(Font.PLAIN, 12));
                pie[i].setForeground(TEXTO_SUAVE);
            }
            pie[i].setHorizontalAlignment(SwingConstants.CENTER);
            pie[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            zonaPieContenido.add(pie[i]);
        }

        pnlContenido.add(zonaSaludo, BorderLayout.NORTH);
        pnlContenido.add(zonaBotones, BorderLayout.CENTER);
        pnlContenido.add(zonaPieContenido, BorderLayout.SOUTH);

        // ----- la ventana: barra lateral a la izquierda, encabezado y contenido a la derecha -----
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.add(pnlEncabezado, BorderLayout.NORTH);
        derecha.add(pnlContenido, BorderLayout.CENTER);

        raiz.removeAll();
        raiz.setLayout(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.add(pnlMenu, BorderLayout.WEST);
        raiz.add(derecha, BorderLayout.CENTER);
    }

    // ---------- vista previa: clic derecho > Run File ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Vista previa del menú");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JButton[] botones = {new JButton("Registrar Libro"), new JButton("Realizar Préstamo"),
                new JButton("Devoluciones"), new JButton("Gestionar Usuarios"),
                new JButton("Crear usuario"), new JButton("Generar Reporte")};
            botones[5].setEnabled(false); // asi se ve cuando el usuario no es administrador
            botones[5].setToolTipText("Acceso denegado, solo el administrador puede generar reportes");
            aplicarMenuPrincipal(f, new JPanel(), new JPanel(), new JPanel(),
                    new JLabel[]{new JLabel("BIBLIOTECA"), new JLabel("BIBLIOTECA")},
                    new JLabel[]{new JLabel("GESTION DE BIBLIOTECA"), new JLabel("UNIVERSIDAD DE EL SALVADOR UES")},
                    new JLabel[]{new JLabel("BIENVENIDO AL SISTEMA"), new JLabel("Seleccione una opcion")},
                    new JLabel[]{new JLabel("Biblioteca"), new JLabel(), new JLabel()},
                    botones);
            f.setVisible(true);
        });
    }
}
