package gestionbiblioteca;
import java.time.LocalDate;

/**
 *
 * @author Fernando Ruano
 */
public class Usuario {

    private String id_Usuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correoElectronico;
    private LocalDate fechaRegistro;

    public Usuario() {
        this.fechaRegistro = LocalDate.now();
    }

    // Constructor 1
    public Usuario(String id, String nombre) {
        this.id_Usuario = id_Usuario;
        this.nombre = nombre;
        this.telefono = "";
    }

    // Constructor 2 - Sobrecarga
    public Usuario(String id, String nombre, String telefono) {
        this.id_Usuario = id_Usuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaRegistro = LocalDate.now();
    }
    

    public Usuario(String id, String nombre, String apellido, String telefono, String correoElectronico, LocalDate fechaRegistro) {
        this.id_Usuario = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.fechaRegistro = fechaRegistro;
    }

    

//metodos get
    public String getId_Usuario() {
        return id_Usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
    public String getTelefono() {
        return telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    //Metodos set
    public void setId_Usuario(String id_Usuario) {
        this.id_Usuario = id_Usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    
    //metodo de mostrar informacion
    public void mostrarInformacion() {
        System.out.println("ID: " + id_Usuario);
        System.out.println("Nombre: " + nombre + " " + apellido);
        System.out.println("Telefono: " + telefono);
        System.out.println("Correo: " + correoElectronico);
        System.out.println("Fecha de Registro: " + fechaRegistro);
        
    }
}
