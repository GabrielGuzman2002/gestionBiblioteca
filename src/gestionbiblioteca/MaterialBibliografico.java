package gestionbiblioteca;
/**
 *
 * @author guzman
 */

public abstract class MaterialBibliografico {
    
    //se declaran los atributos privados
    private String codigo;
    private String titulo;
    private String autor;
    private Boolean disponible;
    
    
    public MaterialBibliografico(){
        this.disponible = true;
    }
    
    //constructor de la clase
    public MaterialBibliografico(String codigo, String titulo, String autor){
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = true;
    }
    
    
    //metodo prestar libro fuera de la bilbioteca
    public void prestar(){
        if (disponible == false){
            System.out.println("Lo sentimos el libro ya ha sido prestado");
        }
        else 
        {
            System.out.println("El libro ha sido prestado con exito");
            this.disponible = false;
        }
    }
    
    //metodo devolver libro a la biblioteca 
    public void devolver(){
        if (disponible == false){
            this.disponible = true;
            System.out.println("El libro ha sido devuelto, y se encuentra disponible");
        }
        else {
            System.out.println("no ce puede devolver el libro, ya estaba en la biblioteca");
        }
    }
    
    
     //metodos get para los atributos de la clase MaterilBibliografico
    public String getCodigo(){
        return codigo;
    }
    
    public String getTitulo(){
        return titulo;
    }
    
    public String getAutor(){
        return autor;
    }
    
    public boolean isDisponible(){
        return disponible; 
    }
    
    //metodos set
    public void setCodigo(String codigo){
        this.codigo = codigo;
    }
    
    public void setTitulo(String titulo){
        this.titulo = titulo;
    }
    
    public void setAutor (String autor){
        this.autor = autor;
    }
    
    public void setDisponible(boolean disponible){
        this.disponible = disponible;
    }

    //se mostrara la informacion en consola del material
    public void mostrarInformacion(){
        System.out.println("Datos del material Bibliografico");
        System.out.println("Codigo :" + this.codigo);
        System.out.println("Titulo :" + this.titulo);
        System.out.println("Estado :" + (this.disponible ? "Disponible" : "Prestado"));
        System.out.println("Autor :" + this.autor);
    }
    
       
}
