package gestionbiblioteca;

/**
 *
 * @author guzman
 */
//herencia de la clase padre que es material bibliografico
public class Libro extends MaterialBibliografico {
    //atributos de la clase libro
    private String isbn;
    private int paginas;
    
    public Libro(){ 
 
    }


    //constructor recibe de la clase padre material bibliografico
    public Libro( String codigo, String titulo, String autor, String isbn,int paginas){
       super (codigo, titulo, autor);
        this.isbn = isbn;
        this.paginas = paginas;
        
    }

    //constructor completo: datos generales + datos propios del libro
    public Libro(String codigo, String titulo, String autor, int anioPublicacion,
            String editorial, int cantidad, String isbn,int paginas){
        super(codigo, titulo, autor, anioPublicacion,editorial, cantidad);
        this.isbn = isbn;
        this.paginas = paginas; 
    }
    
    @Override 
    public String getTipo(){
        return "LIBRO";
    }
    
    //metdo que mostrara la informacion que la trea de la clase padre y de la clase libro
    @Override
    public void mostrarInformacion(){
        super.mostrarInformacion();
        System.out.println("ISBN :" + this.isbn);
        System.out.println("Contiene la cantidad de :" +this.paginas + " paginas");
    }
    
    //metodos get de la clase libro
    public String getIsbn(){
        return isbn;
    }
    
    public int getPaginas(){
        return paginas;
    }
    
    
    //metodos set de la clase libro
    public void setIsbn(String isbn){
        this.isbn = isbn;
    }
    
    public void setPaginas(int paginas){
        this.paginas = paginas;
    }
}
