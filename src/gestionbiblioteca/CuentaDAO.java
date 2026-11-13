package gestionbiblioteca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Gabriel GuzZman
 */
public class CuentaDAO {
    
    //se valida el inicio de sesion desde mysql
    public boolean validarLogin(String usuario, String contrasena){
        
        
      String sql = "Select nombre_usuario, cargo, estado from cuentas where nombre_usuario = ? and contrasena = ?";
        
      try(Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)){
          ps.setString(1, usuario);
          ps.setString(2, contrasena);
          
          try(ResultSet rs = ps.executeQuery()){
              if (rs.next()){
                  int estado = rs.getInt("estado");
              
                  // se valida que la cuenta este inactiva o bloqueada
                  if (estado != 1){
                      javax.swing.JOptionPane.showMessageDialog(null, "Esta cuenta se encuentra bloqueada o inactiva, conectese al administrador", "Cuenta deshabilitada", javax.swing.JOptionPane.WARNING_MESSAGE);
                      return false;
                  }
                  //hace el guardado de los datos en la sesion
                  Sesion.usuarioActual = rs.getString("nombre_usuario");
                  Sesion.cargoActual = rs.getString("cargo");
                  return true;
              }
          }
      }
      catch (SQLException e){
          System.err.println("Erro no ce ha podido validar el login: " + e.getMessage());
      }
      return false;
    }
    
    //metodo para registrar una nueva uenta de bibliotecario
    public boolean registrarCuenta(String usuario, String contrasena, String cargo){
      
        String sql = "Insert into cuentas (nombre_usuario, contrasena,cargo, estado, fecha_creacion)"
                + "values (?,?,?, 1, CURRENT_DATE())";
        try(Connection con = Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ps.setString(3, cargo.toUpperCase());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e){
            System.err.println("Error al haer el registro" + e.getMessage());
            return false;
        }
    }
    
    //se restablecene elc ambiar la contrasena por nombre
    public boolean restablecerContrasena(String usuario, String nuevaContrasena){
        String sql = "Update cuentas set contrasena = ? where nombre_usuario = ?";
        
        try (Connection con =  Conexion.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, nuevaContrasena);
            ps.setString(2, usuario);
            return ps.executeUpdate() > 0;
        }
        
        catch (SQLException e){
            System.err.println("Error no ce ha podido restablecer la contraseña" + e.getMessage());
            return false;
        }
    }
}
