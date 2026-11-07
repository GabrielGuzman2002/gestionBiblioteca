package gestionbiblioteca;

/**
 *
 * @author guzman
 */
public class Revista extends MaterialBibliografico {

    //Atributos de la clase revista
    private int numeroEdicion;
    private String periodicidad;
    private int volumen; // 0 = sin volumen

    //constructor de la clase tomando como clase padre la clase materialBibliografico
    public Revista(String codigo, String titulo, String autor, int numeroEdicion, String periodicidad) {
        super(codigo, titulo, autor);
        this.numeroEdicion = numeroEdicion;
        this.periodicidad = periodicidad;
    }

    //constructor completo: datos generales + datos propios de la revista
    public Revista(String codigo, String titulo, String autor, int anioPublicacion,
            String editorial, int cantidad, int numeroEdicion, String periodicidad, int volumen) {
        super(codigo, titulo, autor, anioPublicacion, editorial, cantidad);
        this.numeroEdicion = numeroEdicion;
        this.periodicidad = periodicidad;
        this.volumen = volumen;
    }

    @Override
    public String getTipo() {
        return "REVISTA";
    }

    @Override
    public void mostrarInformacion() {
        super.mostrarInformacion();
        System.out.println("El numero de edicion de la revista es :" + this.numeroEdicion);
        System.out.println("El periodo seleccionado es :" + this.periodicidad);
        if (this.volumen > 0) {
            System.out.println("Volumen :" + this.volumen);
        }
    }

    //metodos get de la clase revista
    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    public String getPeriodicidad() {
        return periodicidad;
    }

    public int getVolumen() {
        return volumen;
    }

    //metodos set de la clase
    public void setnumeroEdicion(int numeroEdicion) {
        this.numeroEdicion = numeroEdicion;
    }

    public void setNumeroEdicion(int numeroEdicion) {
        this.numeroEdicion = numeroEdicion;
    }

    public void setPeriodicidad(String periodicidad) {
        this.periodicidad = periodicidad;
    }

    public void setVolumen(int volumen) {
        this.volumen = volumen;
    }
}