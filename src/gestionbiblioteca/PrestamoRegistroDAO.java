package gestionbiblioteca;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Proceso 3 del enunciado: registrar y consultar prestamos.
 * Es una clase aparte de PrestamoDAO (que se usa para las devoluciones)
 * para no mezclar ni pisar el trabajo de los demas.
 */
public class PrestamoRegistroDAO {

    // restriccion definida para validar el prestamo (3.3)
    public static final int MAX_PRESTAMOS_ACTIVOS = 3;

    // elemento simple para los combos: muestra un texto y guarda el codigo
    public static class Opcion {

        private final String id;
        private final String texto;

        public Opcion(String id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    // consulta base: el estado se calcula (Activo, Vencido o Devuelto)
    private static final String CONSULTA_BASE
            = "SELECT t.id_prestamo, t.usuario, t.material, t.tipo, t.fecha_prestamo, "
            + "t.fecha_prevista, t.fecha_devolucion, t.estado FROM ("
            + "SELECT p.id_prestamo AS id_prestamo, "
            + "CONCAT(u.id_usuario, ' - ', u.nombre, ' ', u.apellido) AS usuario, "
            + "CONCAT(m.id_codigo, ' - ', m.titulo) AS material, m.tipo AS tipo, "
            + "p.fecha_prestamo AS fecha_prestamo, "
            + "p.fecha_devolucion_prevista AS fecha_prevista, "
            + "p.fecha_devolucion AS fecha_devolucion, "
            + "CASE WHEN p.activo = 1 THEN "
            + "(CASE WHEN p.fecha_devolucion_prevista < CURDATE() THEN 'Vencido' ELSE 'Activo' END) "
            + "ELSE 'Devuelto' END AS estado "
            + "FROM prestamo p "
            + "INNER JOIN usuario u ON p.id_usuario = u.id_usuario "
            + "INNER JOIN material_bibliografico m ON p.id_codigo = m.id_codigo) t ";

    //3.1 usuarios que pueden pedir prestado
    public List<Opcion> listarUsuarios() {
        List<Opcion> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, apellido FROM usuario ORDER BY nombre, apellido";
        try (Connection con = Conexion.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id_usuario");
                lista.add(new Opcion(id, id + " - " + rs.getString("nombre") + " " + rs.getString("apellido")));
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Error no se han podido cargar los usuarios: " + e.getMessage());
        }
        return lista;
    }

    //3.2 materiales que todavia tienen copias disponibles
    public List<Opcion> listarMaterialesDisponibles() {
        List<Opcion> lista = new ArrayList<>();
        String sql = "SELECT id_codigo, titulo, tipo, cantidad_disponible FROM material_bibliografico "
                + "WHERE cantidad_disponible > 0 AND disponible = true ORDER BY titulo";
        try (Connection con = Conexion.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id_codigo");
                lista.add(new Opcion(id, id + " - " + rs.getString("titulo")
                        + " (" + rs.getString("tipo") + ") · disponibles: " + rs.getInt("cantidad_disponible")));
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Error no se han podido cargar los materiales: " + e.getMessage());
        }
        return lista;
    }

    private int contar(Connection con, String sql, String... parametros) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    //3.3, 3.4, 3.5 valida, registra el prestamo y descuenta una copia del material.
    // Devuelve null si todo salio bien, o el mensaje de por que no se pudo.
    public String registrar(String idPrestamo, String idUsuario, String idCodigo,
            LocalDate fechaPrestamo, LocalDate fechaPrevista) {
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            if (con == null) {
                return "No se pudo conectar con la base de datos.";
            }
            con.setAutoCommit(false); // o se hace todo o no se hace nada

            // el usuario debe existir
            if (contar(con, "SELECT COUNT(*) FROM usuario WHERE id_usuario = ?", idUsuario) == 0) {
                con.rollback();
                return "El usuario seleccionado no existe.";
            }

            // restriccion: maximo de prestamos activos por usuario
            int activos = contar(con,
                    "SELECT COUNT(*) FROM prestamo WHERE id_usuario = ? AND activo = true", idUsuario);
            if (activos >= MAX_PRESTAMOS_ACTIVOS) {
                con.rollback();
                return "El usuario ya tiene " + activos + " préstamos activos (máximo permitido: "
                        + MAX_PRESTAMOS_ACTIVOS + ").";
            }

            // no prestar dos veces el mismo material a la misma persona
            if (contar(con, "SELECT COUNT(*) FROM prestamo WHERE id_usuario = ? AND id_codigo = ? AND activo = true",
                    idUsuario, idCodigo) > 0) {
                con.rollback();
                return "El usuario ya tiene prestado este material.";
            }

            // se bloquea la fila del material para que nadie mas lo preste al mismo tiempo
            int cantidad;
            boolean disponible;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT cantidad_disponible, disponible FROM material_bibliografico "
                    + "WHERE id_codigo = ? FOR UPDATE")) {
                ps.setString(1, idCodigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return "El material seleccionado no existe.";
                    }
                    cantidad = rs.getInt(1);
                    disponible = rs.getBoolean(2);
                }
            }
            if (cantidad <= 0 || !disponible) {
                con.rollback();
                return "El material ya no tiene copias disponibles.";
            }

            // 3.4 registrar el prestamo
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO prestamo (id_prestamo, fecha_prestamo, fecha_devolucion_prevista, "
                    + "activo, id_usuario, id_codigo) VALUES (?,?,?,true,?,?)")) {
                ps.setString(1, idPrestamo);
                ps.setDate(2, Date.valueOf(fechaPrestamo));
                ps.setDate(3, Date.valueOf(fechaPrevista));
                ps.setString(4, idUsuario);
                ps.setString(5, idCodigo);
                ps.executeUpdate();
            }

            // 3.5 actualizar la disponibilidad
            int nueva = cantidad - 1;
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE material_bibliografico SET cantidad_disponible = ?, disponible = ? "
                    + "WHERE id_codigo = ?")) {
                ps.setInt(1, nueva);
                ps.setBoolean(2, nueva > 0);
                ps.setString(3, idCodigo);
                ps.executeUpdate();
            }

            con.commit();
            return null;
        } catch (SQLException e) {
            System.out.println("Error no se ha podido registrar el prestamo: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al deshacer los cambios: " + ex.getMessage());
                }
            }
            return "No se pudo registrar el préstamo. Revise la conexión con la base de datos.";
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    System.out.println("Error al cerrar la conexion: " + ex.getMessage());
                }
            }
        }
    }

    //3.6 todos los prestamos
    public List<Object[]> listarTodos() {
        return consultar("", null);
    }

    //3.7 prestamos pendientes de devolucion (activos y vencidos)
    public List<Object[]> listarActivos() {
        return consultar("WHERE t.estado IN ('Activo', 'Vencido') ", null);
    }

    //3.6 buscar por codigo, usuario, material o estado
    public List<Object[]> buscar(String filtro, String criterio) {
        String columna;
        switch (filtro) {
            case "Código":
                columna = "t.id_prestamo";
                break;
            case "Usuario":
                columna = "t.usuario";
                break;
            case "Material":
                columna = "t.material";
                break;
            default:
                columna = "t.estado";
        }
        return consultar("WHERE " + columna + " LIKE ? ", "%" + criterio + "%");
    }

    private List<Object[]> consultar(String where, String parametro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = CONSULTA_BASE + where + "ORDER BY t.fecha_prestamo DESC, t.id_prestamo DESC";

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String devolucion = rs.getString("fecha_devolucion");
                    lista.add(new Object[]{
                        rs.getString("id_prestamo"),
                        rs.getString("usuario"),
                        rs.getString("material"),
                        rs.getString("tipo"),
                        rs.getString("fecha_prestamo"),
                        rs.getString("fecha_prevista"),
                        devolucion != null ? devolucion : "Pendiente",
                        rs.getString("estado")
                    });
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Error no se han podido consultar los prestamos: " + e.getMessage());
        }
        return lista;
    }
}
