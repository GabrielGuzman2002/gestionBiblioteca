package gestionbiblioteca;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * author Fernando Garcia
 * Operaciones de la base de datos para los materiales (libros y revistas).
 * Guarda siempre el tipo, asi el programa sabe si es libro o revista.
 */
public class MaterialDAO {

    // una operacion que se ejecuta dentro de una transaccion
    private interface Operacion {
        void ejecutar(Connection con) throws SQLException;
    }

    // si algo falla se deshace todo (no queda guardado a medias)
    private boolean enTransaccion(Operacion operacion, String mensajeError) {
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            if (con == null) {
                return false;
            }
            con.setAutoCommit(false);
            operacion.ejecutar(con);
            con.commit();
            return true;
        } catch (SQLException e) {
            System.out.println(mensajeError + ": " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al deshacer los cambios: " + ex.getMessage());
                }
            }
            return false;
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

    private void setAutor(PreparedStatement ps, int indice, String autor) throws SQLException {
        if (autor == null || autor.trim().isEmpty()) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, autor);
        }
    }

    private void setVolumen(PreparedStatement ps, int indice, int volumen) throws SQLException {
        if (volumen > 0) {
            ps.setInt(indice, volumen);
        } else {
            ps.setNull(indice, Types.INTEGER);
        }
    }

    // devuelve la cantidad que cuenta la consulta, o -1 si hubo un error
    private int contar(String sql, String codigo) {
        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
            return -1;
        }
    }

    //1.1 verifica que el codigo no se repita
    public boolean existeCodigo(String codigo) {
        return contar("SELECT COUNT(*) FROM material_bibliografico WHERE id_codigo = ?", codigo) > 0;
    }

    //1.7 el material tiene prestamos activos (si hay error se asume que si, por seguridad)
    public boolean tienePrestamosActivos(String codigo) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE id_codigo = ? AND activo = true", codigo) != 0;
    }

    //1.7 el material tiene algun prestamo en el historial (hay que conservar el registro)
    public boolean tienePrestamos(String codigo) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE id_codigo = ?", codigo) != 0;
    }

    //1.1, 1.2, 1.3 registra un libro o una revista (tabla general + tabla especifica)
    public boolean registrar(MaterialBibliografico m) {
        return enTransaccion(con -> {
            String sqlBase = "INSERT INTO material_bibliografico (id_codigo, titulo, autor, tipo, "
                    + "anio_publicacion, editorial, cantidad_disponible, fecha_registro, disponible) "
                    + "VALUES (?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlBase)) {
                ps.setString(1, m.getCodigo());
                ps.setString(2, m.getTitulo());
                setAutor(ps, 3, m.getAutor());
                ps.setString(4, m.getTipo());
                ps.setInt(5, m.getAnioPublicacion());
                ps.setString(6, m.getEditorial());
                ps.setInt(7, m.getCantidad());
                ps.setDate(8, Date.valueOf(LocalDate.now()));
                ps.setBoolean(9, m.getCantidad() > 0);
                ps.executeUpdate();
            }

            if (m instanceof Libro) {
                Libro l = (Libro) m;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO libro (id_codigo, isbn, paginas) VALUES (?,?,?)")) {
                    ps.setString(1, l.getCodigo());
                    ps.setString(2, l.getIsbn());
                    ps.setInt(3, l.getPaginas());
                    ps.executeUpdate();
                }
            } else if (m instanceof Revista) {
                Revista r = (Revista) m;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO revista (id_codigo, numero_edicion, periodicidad, volumen) VALUES (?,?,?,?)")) {
                    ps.setString(1, r.getCodigo());
                    ps.setInt(2, r.getNumeroEdicion());
                    ps.setString(3, r.getPeriodicidad());
                    setVolumen(ps, 4, r.getVolumen());
                    ps.executeUpdate();
                }
            } else {
                throw new SQLException("Tipo de material desconocido");
            }
        }, "Error no se ha podido registrar el material");
    }

    //1.6 modifica los datos permitidos (el codigo y el tipo no se cambian)
    public boolean modificar(MaterialBibliografico m) {
        return enTransaccion(con -> {
            String sqlBase = "UPDATE material_bibliografico SET titulo = ?, autor = ?, "
                    + "anio_publicacion = ?, editorial = ?, cantidad_disponible = ?, disponible = ? "
                    + "WHERE id_codigo = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlBase)) {
                ps.setString(1, m.getTitulo());
                setAutor(ps, 2, m.getAutor());
                ps.setInt(3, m.getAnioPublicacion());
                ps.setString(4, m.getEditorial());
                ps.setInt(5, m.getCantidad());
                ps.setBoolean(6, m.getCantidad() > 0);
                ps.setString(7, m.getCodigo());
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("El material " + m.getCodigo() + " no existe");
                }
            }

            if (m instanceof Libro) {
                Libro l = (Libro) m;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE libro SET isbn = ?, paginas = ? WHERE id_codigo = ?")) {
                    ps.setString(1, l.getIsbn());
                    ps.setInt(2, l.getPaginas());
                    ps.setString(3, l.getCodigo());
                    ps.executeUpdate();
                }
            } else if (m instanceof Revista) {
                Revista r = (Revista) m;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE revista SET numero_edicion = ?, periodicidad = ?, volumen = ? WHERE id_codigo = ?")) {
                    ps.setInt(1, r.getNumeroEdicion());
                    ps.setString(2, r.getPeriodicidad());
                    setVolumen(ps, 3, r.getVolumen());
                    ps.setString(4, r.getCodigo());
                    ps.executeUpdate();
                }
            }
        }, "Error no se ha podido modificar el material");
    }

    //1.7 elimina el material (el formulario revisa antes que no tenga prestamos)
    public boolean eliminar(String codigo) {
        return enTransaccion(con -> {
            for (String sql : new String[]{
                "DELETE FROM libro WHERE id_codigo = ?",
                "DELETE FROM revista WHERE id_codigo = ?",
                "DELETE FROM material_bibliografico WHERE id_codigo = ?"}) {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, codigo);
                    ps.executeUpdate();
                }
            }
        }, "Error no se ha podido eliminar el material");
    }

    //1.5 consultar el catalogo completo
    public List<MaterialBibliografico> listarTodos() {
        return consultar("", null);
    }

    //1.4 buscar por codigo, titulo, autor o tipo
    public List<MaterialBibliografico> buscar(String criterio, String filtro) {
        String columna;
        switch (filtro) {
            case "Código":
                columna = "m.id_codigo";
                break;
            case "Título":
                columna = "m.titulo";
                break;
            case "Autor":
                columna = "m.autor";
                break;
            default:
                columna = "m.tipo";
        }
        return consultar("WHERE " + columna + " LIKE ? ", "%" + criterio + "%");
    }

    // arma objetos Libro o Revista segun el tipo guardado
    private List<MaterialBibliografico> consultar(String where, String parametro) {
        List<MaterialBibliografico> lista = new ArrayList<>();

        String sql = "SELECT m.id_codigo, m.titulo, m.autor, m.tipo, m.anio_publicacion, "
                + "m.editorial, m.cantidad_disponible, l.isbn, l.paginas, "
                + "r.numero_edicion, r.periodicidad, r.volumen "
                + "FROM material_bibliografico m "
                + "LEFT JOIN libro l ON m.id_codigo = l.id_codigo "
                + "LEFT JOIN revista r ON m.id_codigo = r.id_codigo "
                + where
                + "ORDER BY m.fecha_registro DESC, m.id_codigo";

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("id_codigo");
                    String titulo = rs.getString("titulo");
                    String autor = rs.getString("autor") == null ? "" : rs.getString("autor");
                    int anio = rs.getInt("anio_publicacion");
                    String editorial = rs.getString("editorial");
                    int cantidad = rs.getInt("cantidad_disponible");

                    if ("REVISTA".equals(rs.getString("tipo"))) {
                        lista.add(new Revista(codigo, titulo, autor, anio, editorial, cantidad,
                                rs.getInt("numero_edicion"), rs.getString("periodicidad"),
                                rs.getInt("volumen")));
                    } else {
                        lista.add(new Libro(codigo, titulo, autor, anio, editorial, cantidad,
                                rs.getString("isbn"), rs.getInt("paginas")));
                    }
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Error no se ha podido consultar el catalogo: " + e.getMessage());
        }
        return lista;
    }
}