package gestionbiblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabriel GuzZman
 */
public class PrestamoDAO {

    //4.1, 4.2 se obtiene los prestamos que estan activos
    public List<Object[]> listarPrestamosActivos() {
        List<Object[]> lista = new ArrayList<>();

        String sql = "Select  p.id_prestamo, p.id_codigo, m.titulo, p.id_usuario, CONCAT(u.nombre, u.apellido) AS usuario , "
                + "p.fecha_prestamo, p.fecha_devolucion_prevista From prestamo p "
                + "inner join material_bibliografico m on p.id_codigo = m.id_codigo "
                + "inner join usuario u on p.id_usuario = u.id_usuario where p.activo = true ";

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("id_prestamo"),
                    rs.getString("id_codigo"),
                    rs.getString("titulo"),
                    rs.getString("id_usuario"),
                    rs.getString("usuario"),
                    rs.getString("fecha_prestamo"),
                    rs.getString("fecha_devolucion_prevista")

                });
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar la lista de prestamos activos :" + e.getMessage());
        }
        return lista;
    }

    //4.3,4.4 se hace el registro de las devoluciones y actualiza lo que esta disponible
    public boolean registrarDevolucion(String idPrestamo, String idCodigoMaterial) {
        String sqlPrestamo = "update prestamo Set fecha_devolucion = ?, activo = false where id_prestamo = ? and activo = true";
        String sqlMaterial = "update material_bibliografico  set disponible = true where id_codigo = ?";

        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            //Desactivamos el autocommit para que no choquen las actualizaciones
            con.setAutoCommit(false);

            //Actualizamos el prestamo
            try (PreparedStatement psP = con.prepareCall(sqlPrestamo)) {
                psP.setDate(1, Date.valueOf(LocalDate.now()));
                psP.setString(2, idPrestamo);
                int filaPrestamo = psP.executeUpdate();

                if (filaPrestamo == 0) {
                    con.rollback();
                    //si no ce logra actualizar el prestamo se cancela
                    return false;
                }
            }
            try (PreparedStatement psM = con.prepareStatement(sqlMaterial)) {
                psM.setString(1, idCodigoMaterial);
                psM.executeUpdate();
            }
            con.commit();// se hace la confirmacion de los cambios 
            return true;
        } catch (SQLException e) {
            System.out.println("Error no ce ha podido registrar la devolucion: " + e.getMessage());

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error no ce ha podido actualizar: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    System.out.println("Error no ce ha podido cerrar la conexion: " + ex.getMessage());
                }
            }
        }
    }

    //4.5 consultar el historial de prestamos y actvios
    public List<Object[]> listaHistorialCompleta() {
        List<Object[]> lista = new ArrayList<>();

        String sql = "Select p.id_prestamo, m.titulo, Concat(u.nombre, u.apellido) AS usurio"
                + "p.fecha_devolucion_prevista, p.fecha_devolucion "
                + "case then when p.activo = true 'Activo' else 'Devuelto' end as Estado "
                + "inner join material_bibliografico m on p.id_codigo = m.id_codigo"
                + "inner join usuario u on p.id_usuario = u.id_usuario order by p.fecha_prestamo desc";

        try (Connection con = Conexion.obtenerConexion(); 
                PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("id_prestamo"),
                    rs.getString("titulo"),
                    rs.getString("usuario"),
                    rs.getDate("fecha_prestamo"),
                    rs.getDate("fecha_devolucion_prevista"),
                    rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion") : "Pendiente",
                    rs.getString("estado")
                });
            }
        } catch(SQLException e){
            System.out.println(" Error el historial no ce ha podido obtener: " + e.getMessage());
        }
    return lista ;

    }
}
