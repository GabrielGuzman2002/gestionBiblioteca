package gestionbiblioteca;

/**
 *
 * @author guzman
 */
public class Revista extends MaterialBibliografico {

    //Atributos de la clase revista
    private int numeroEdicion;
    private String periodicidad;

//constructor de la clase tomando como clase padre la clase materialBibliografico
    public Revista(String codigo, String titulo, String autor, int numeroEdicion, String periodicidad) {
        super(codigo, titulo, autor);
        this.numeroEdicion = numeroEdicion;
        this.periodicidad = periodicidad;
    }

    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("El numero de edicion del libro es :" + this.numeroEdicion);
        System.out.println("El periodo seleccionado es :" + this.periodicidad);
    }

    //metodos get de la clase revista
    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    public String getPeriodicidad() {
        return periodicidad;
    }

//metodos set de la clase
    public void setnumeroEdicion(int numeroEdicion) {
        this.numeroEdicion = numeroEdicion;
    }

    public void setPeriodicidad(String periodicidad) {
        this.periodicidad = periodicidad;
    }
}
