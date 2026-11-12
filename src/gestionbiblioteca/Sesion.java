package gestionbiblioteca;

/**
 *
 * @author GuzZman
 */
public class Sesion {

    //Atributos staticos que no cambiaran 
    public static String usuarioActual = "";
    public static String cargoActual = "";
    
    //se hace el guardado del valor de la columna cargo en la base de datos
    public static boolean esAdmin(){
        return "ADMINISTRADOR".equalsIgnoreCase(cargoActual)|| " ADMIN".equalsIgnoreCase(cargoActual);
    }
    
    public static void cerrarSesion(){
        usuarioActual = "";
        cargoActual = "";
    }
}
