package gestionbiblioteca;
import java.time.LocalDate;

/**
 *
 * @author guzman
 */
public class Prestamo {
    
    //atrbutos para la clase prestamo
    private String idPrestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private Boolean activo;
    private Usuario usuario;
    private MaterialBibliografico material;
    
    
    public Prestamo(){
        this.activo = false;
        this.fechaPrestamo = null;
        this.fechaDevolucion = null;
    }
    
    //constructor de la clase 
    public Prestamo(String idPrestamo, MaterialBibliografico material, Usuario usuario){
        this.idPrestamo = idPrestamo;
        this.material = material;
        this.usuario = usuario;
        //Activo se iniciara siempre en falso por defecto
        this.activo = false;
        //la fecha prestamo esta nula hasta realizar su primera prestacion
        this.fechaPrestamo = null;
    }
    
    //se crea el metodo 
    public void realizarPrestamo(){
        if (!this.material.isDisponible()){
            System.out.println("Error el material no ce puede prestar porque no ce encuentra disponible");
        }
        else{
             this.material.prestar();
            this.fechaPrestamo = LocalDate.now();
            this.activo = true;
           
        }        
    }
    
    public void registrarDevolucion(){
        
        //se valida que el material que se ingrese no este nulo
        if (this.material == null){
            System.out.println("Error, no hay maaterial que se asocie a este prestamo");
        }
        else if (!this.activo){
            System.out.println("El prestamo de este libro, (o ya se hizo la devolucion o unca se ha prestado)");
        }
        else {
          this.material.devolver();
          this.fechaDevolucion = LocalDate.now();
          this.activo = false;
        }
    }
    
    //metodos get para los atributos para actualizarlos
    public String getIdPrestamo(){
        return idPrestamo;
    }
    
    public LocalDate getFechaPrestamo(){
        return fechaPrestamo;
    }
    
    public LocalDate getFechaDevolucion(){
        return fechaDevolucion;
    }
    
    public boolean isActivo(){
        return activo;
    }    
    
    public Usuario getUsuario(){
        return usuario;
    }
    
    public MaterialBibliografico getMaterial(){
        return material;
    }
    
    
    //metodos set
    public void setIdPrestamo(String idPrestamo){
        this.idPrestamo = idPrestamo;
    }
    
    public void setFechaPrestamo(LocalDate fechaPrestamo){
        this.fechaPrestamo = fechaPrestamo;
    }
    
    public void setFechaDevolucion(LocalDate FechaDevolucion){
        this.fechaDevolucion = fechaDevolucion;
    }
    
    public void setActivo(boolean activo){
        this.activo = activo;
    }
    
    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }
    
    public void setMaterialBibliografico(MaterialBibliografico material){
        this.material = material;
    }
    
    
    
    public void mostrarInformacion(){
        System.out.println("IdPrestamo :" + this.idPrestamo);
        System.out.println("Fecha prestamo :" + this.fechaPrestamo);
        System.out.println("Activo :" + this.activo);
        System.out.println("Usuario: " + this.getUsuario().getNombre());
        System.out.println("Fecha de devolucion: " + fechaDevolucion);
    }
}
