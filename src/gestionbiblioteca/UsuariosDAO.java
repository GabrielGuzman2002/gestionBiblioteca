package gestionbiblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es para poder manejar las operaciones que se van a realizar en la
 * tabla de usuarios a la base de datos
 *
 * @author Gabriel GuzZman
 */
public class UsuariosDAO {

    //2.1 Registrar usuario a la base de datos
    public boolean registrar(Usuario usu) {
        String sql = "INSERT INTO usuario (id_usuario,nombre, apellido,"
                + "telefono,correo_electronico, fecha_registro) values (?,?,?,?,?,?)";
        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usu.getId_Usuario());
            ps.setString(2, usu.getNombre());
            ps.setString(3, usu.getApellido());
            ps.setString(4, usu.getTelefono());
            ps.setString(5, usu.getCorreoElectronico());
            ps.setDate(6, Date.valueOf(usu.getFechaRegistro()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error, el usurio no ce ha podido registrar :" + e.getMessage());
            return false;
        }
    }

    //2.2 buscar usuario por documento/codigo o por su nombre
    public List<Usuario> buscar(String criterio, String filtrarPor) {
        List<Usuario> lista = new ArrayList<>();

        String sql;
        if ("Codigo".equals(filtrarPor) || "Documento".equals(filtrarPor)) {
            sql = "SELECT * from usuario where id_usuario like ?";
        } else {
            sql = "SELECT * from usuario where nombre like ? or apellido like ?";
        }

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            String valor = "%" + criterio + "%";
            ps.setString(1, valor);

            if (!"Codigo".equals(filtrarPor) && !"Documento".equals(filtrarPor)) {
                ps.setString(2, valor);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usua = new Usuario(
                    rs.getString("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("correo_electronico"),
                    rs.getDate("fecha_registro").toLocalDate());
                    
                    lista.add(usua);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al buscar al usuario");
        }
        return lista;
    }

    //2.3 Consultar todos los usuarios de la tabla
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM usuario order by fecha_registro desc ";

        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usua = new Usuario(
                        rs.getString("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico"),
                        rs.getDate("fecha_registro").toLocalDate());

                lista.add(usua);
            }

        } catch (SQLException e) {
            System.out.println("Error; no ce ha podido mostrar la lista de los usuarios "
                    + e.getMessage());
        }

        return lista;
    }
    
    //2.4 modificar los usuarios
    public boolean modificar(Usuario u){
     String sql = "Update usuario set nombre = ?, apellido = ?, telefono = ?, correo_electronico = ? where id_usuario = ? ";
      try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)){
          
           ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getCorreoElectronico());
            ps.setString(5, u.getId_Usuario());

            return ps.executeUpdate() > 0;
      }catch (SQLException e)
        {
          System.out.println("Error el usuario no ce ha podido modificar");
          return false;
        }
    }  
    
    //2.5 validar si el usuario tiene algun prestamos activos
    public boolean prestamosActivos(String idUsuario){
        String sql = "Select count (*) from prestamo where id_usuario = ? and activo = true";
        
        try (Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                {
                    return rs.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e ){
            System.out.println("Error no ce han podido comprobar los prestamos activos: " + e.getMessage());
        }
        return false;
    }
    
    //2.5 Eliminar de la lista al usuario
    public boolean eliminar(String idUsuario){
       String sql = "Delete from usuario where id_usuario = ? ";
       
       try(Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)){
           ps.setString(1, idUsuario);
           return ps.executeUpdate() > 0;
       }
       catch (SQLException e){
           System.out.println("Error el usuario no ce ha podido eliminar: " + e.getMessage());
           return false;
       }
    }
}
