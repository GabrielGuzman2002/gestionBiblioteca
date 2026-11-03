package gestionbiblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class MaterialDAO {

    // Comprueba si un material ya está registrado en MySQL.
    public boolean existeMaterial(String codigo) throws SQLException {

        String sql = "SELECT 1 FROM material_bibliografico "
                   + "WHERE id_codigo = ?";

        try (Connection con = Conexion.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                    "No se pudo establecer conexión con MySQL."
                );
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, codigo);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    // Registra un libro en la base de datos MySQL.
    public boolean registrarLibro(Libro libro) throws SQLException {

        String sqlMaterial = "INSERT INTO material_bibliografico "
                + "(id_codigo, titulov, autor, categoria, "
                + "fecha_registro, disponible) "
                + "VALUES (?, ?, ?, 'LIBRO', CURRENT_DATE(), ?)";

        String sqlLibro = "INSERT INTO libro "
                + "(id_codigo, isbn, paginas) VALUES (?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con MySQL.");
            }

            con.setAutoCommit(false);

            try (PreparedStatement psMaterial =
                    con.prepareStatement(sqlMaterial);
                 PreparedStatement psLibro =
                    con.prepareStatement(sqlLibro)) {

                psMaterial.setString(1, libro.getCodigo());
                psMaterial.setString(2, libro.getTitulo());
                psMaterial.setString(3, libro.getAutor());
                psMaterial.setBoolean(4, libro.isDisponible());

                psMaterial.executeUpdate();

                psLibro.setString(1, libro.getCodigo());
                psLibro.setString(2, libro.getIsbn());
                psLibro.setInt(3, libro.getPaginas());

                psLibro.executeUpdate();

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Registra una revista en la base de datos MySQL.
    public boolean registrarRevista(Revista revista) throws SQLException {

        String sqlMaterial = "INSERT INTO material_bibliografico "
                + "(id_codigo, titulov, autor, categoria, "
                + "fecha_registro, disponible) "
                + "VALUES (?, ?, ?, 'REVISTA', CURRENT_DATE(), ?)";

        String sqlRevista = "INSERT INTO revista "
                + "(id_codigo, numero_edicion, periodicidad) "
                + "VALUES (?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con MySQL.");
            }

            con.setAutoCommit(false);

            try (PreparedStatement psMaterial =
                    con.prepareStatement(sqlMaterial);
                 PreparedStatement psRevista =
                    con.prepareStatement(sqlRevista)) {

                psMaterial.setString(1, revista.getCodigo());
                psMaterial.setString(2, revista.getTitulo());
                psMaterial.setString(3, revista.getAutor());
                psMaterial.setBoolean(4, revista.isDisponible());

                psMaterial.executeUpdate();

                psRevista.setString(1, revista.getCodigo());
                psRevista.setInt(2, revista.getNumeroEdicion());
                psRevista.setString(3, revista.getPeriodicidad());

                psRevista.executeUpdate();

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Consulta todos los libros y revistas registrados.
    public List<MaterialBibliografico> listarMateriales()
            throws SQLException {

        List<MaterialBibliografico> materiales = new ArrayList<>();

        String sql = "SELECT m.id_codigo, m.titulov, m.autor, "
                + "m.categoria, m.disponible, "
                + "l.isbn, l.paginas, "
                + "r.numero_edicion, r.periodicidad "
                + "FROM material_bibliografico m "
                + "LEFT JOIN libro l ON m.id_codigo = l.id_codigo "
                + "LEFT JOIN revista r ON m.id_codigo = r.id_codigo "
                + "ORDER BY m.titulov";

        try (Connection con = Conexion.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con MySQL.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String codigo = rs.getString("id_codigo");
                    String titulo = rs.getString("titulov");
                    String autor = rs.getString("autor");
                    String categoria = rs.getString("categoria");
                    boolean disponible = rs.getBoolean("disponible");

                    if ("LIBRO".equalsIgnoreCase(categoria)) {

                        Libro libro = new Libro(
                                codigo,
                                titulo,
                                autor,
                                rs.getString("isbn"),
                                rs.getInt("paginas")
                        );

                        libro.setDisponible(disponible);
                        materiales.add(libro);

                    } else if ("REVISTA".equalsIgnoreCase(categoria)) {

                        Revista revista = new Revista(
                                codigo,
                                titulo,
                                autor,
                                rs.getInt("numero_edicion"),
                                rs.getString("periodicidad")
                        );

                        revista.setDisponible(disponible);
                        materiales.add(revista);
                    }
                }
            }
        }

        return materiales;
    }

    // Modifica los datos generales de un material bibliografico.
    public boolean modificarMaterial(String codigo, String titulo,
            String autor) throws SQLException {

        // Validar que los campos no esten vacios.
        if (codigo == null || codigo.isBlank()
                || titulo == null || titulo.isBlank()
                || autor == null || autor.isBlank()) {

            throw new IllegalArgumentException(
                    "Todos los campos son obligatorios.");
        }

        String sql = "UPDATE material_bibliografico "
                + "SET titulov = ?, autor = ? "
                + "WHERE id_codigo = ?";

        try (Connection con = Conexion.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con MySQL.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, titulo.trim());
                ps.setString(2, autor.trim());
                ps.setString(3, codigo.trim());

                return ps.executeUpdate() > 0;
            }
        }
    }

    // Elimina un material si no tiene prestamos registrados.
    public boolean eliminarMaterial(String codigo) throws SQLException {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el codigo del material.");
        }

        String sqlPrestamos = "SELECT 1 FROM prestamo "
                + "WHERE id_codigo = ? LIMIT 1";

        String sqlLibro = "DELETE FROM libro WHERE id_codigo = ?";
        String sqlRevista = "DELETE FROM revista WHERE id_codigo = ?";
        String sqlMaterial = "DELETE FROM material_bibliografico "
                + "WHERE id_codigo = ?";

        try (Connection con = Conexion.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con MySQL.");
            }

            con.setAutoCommit(false);

            try {
                // Comprobar si existen prestamos asociados.
                try (PreparedStatement ps =
                        con.prepareStatement(sqlPrestamos)) {

                    ps.setString(1, codigo);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException(
                                    "No se puede eliminar el material "
                                    + "porque tiene prestamos registrados.");
                        }
                    }
                }

                // Eliminar datos especificos del libro.
                try (PreparedStatement ps =
                        con.prepareStatement(sqlLibro)) {

                    ps.setString(1, codigo);
                    ps.executeUpdate();
                }

                // Eliminar datos especificos de la revista.
                try (PreparedStatement ps =
                        con.prepareStatement(sqlRevista)) {

                    ps.setString(1, codigo);
                    ps.executeUpdate();
                }

                // Eliminar el registro principal.
                int filas;

                try (PreparedStatement ps =
                        con.prepareStatement(sqlMaterial)) {

                    ps.setString(1, codigo);
                    filas = ps.executeUpdate();
                }

                con.commit();
                return filas > 0;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
        
}
