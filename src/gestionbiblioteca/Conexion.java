package gestionbiblioteca;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author GuzZman
 */
public class Conexion {
    
    private static final String NOMBRE_BD = "gestionBiblioteca";
    
    private static final String URL = "jdbc:mysql://localhost:3306/" + NOMBRE_BD
            + "?useSSL=false&serverTimezone=UTC";
    
    private static final String USUARIO = "root";
    private static final String PASSWORD = "admin";
    
    //metodo por la cual se conectara a la base de datos
    public static Connection obtenerConexion(){
        Connection conexion = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL,USUARIO,PASSWORD);
        }
        catch(ClassNotFoundException e){
            System.out.println("Error: no ce ha encontrado los driver de mysql (falta el archivo. jar en libreries");
        } 
        catch (SQLException e) {
            System.out.println(" Error no ce ha podido conectar con la base de datos, intente de nuevo");
            
        }
        return conexion;
    }
    
    public static void main(String[] args){
        Connection con = Conexion.obtenerConexion();
        if(con != null){
            System.out.println("Se ha conectado con exito");
            try {
                con.close();
            }catch (SQLException ex){
                System.out.println("Error al cerrar la base de datos: " + ex.getMessage());
            }
        }
        else {
            System.out.println("Error, Fallo al conectar");
        }
    }
    
}