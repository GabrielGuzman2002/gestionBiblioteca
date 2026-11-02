package gestionbiblioteca;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;

/**
 *
 * @author guzman
 */
public class Main {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);
        
        ArrayList<Usuario> usuarios = new ArrayList<>();
        ArrayList<Libro> libro = new ArrayList<>();
        ArrayList<Prestamo> prestamos = new ArrayList<>();
        int opcion;

        do {

            System.out.println("\n======================================");
            System.out.println("   SISTEMA DE GESTION DE BIBLIOTECA");
            System.out.println("======================================");
            System.out.println("1. Registrar material");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Realizar prestamo");
            System.out.println("4. Registrar devolucion");
            System.out.println("5. Consultar materiales disponibles");
            System.out.println("6. Consultar prestamos activos");
            System.out.println("7. Consultar usuarios registrados");
            System.out.println("0. Salir");
            System.out.println("======================================");
            System.out.print("Seleccione una opcion: ");

            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {

                case 1:
                    System.out.println("\nRegistro de libros en disponibles");
                    
                   
                    Libro nuevoL = new Libro();
                    
                    System.out.print("Ingrese el codigo del libro: ");
                    nuevoL.setCodigo(teclado.nextLine());

                    System.out.print("Ingrese el titulo del libro: ");
                    nuevoL.setTitulo(teclado.nextLine());

                    System.out.print("Ingrese el autor del libro: ");
                    nuevoL.setAutor (teclado.nextLine());

                    System.out.print("Ingrese el identificador: ");
                    nuevoL.setIsbn (teclado.nextLine());

                    System.out.print("Ingrese la cantidad de paginas del libro: ");
                    nuevoL.setPaginas(teclado.nextInt());
                    
                    libro.add(nuevoL);

                    System.out.println("\nLibro registrado correctamente.");

                    break;

                case 2:

                    //se piden los datos del usuario para poder agregarlo
                    System.out.println("\n--- REGISTRAR USUARIO ---");
                    
                    Usuario nuevoUsuario = new Usuario();

                    System.out.print("Ingrese ID: ");
                     nuevoUsuario.setId_Usuario(teclado.nextLine());

                    System.out.print("Ingrese nombre: ");
                    nuevoUsuario.setNombre(teclado.nextLine());
                    
                    System.out.println("Ingrese el apellido del usuario :");
                    nuevoUsuario.setApellido(teclado.nextLine());

                    System.out.print("Ingrese telefono: ");
                    nuevoUsuario.setTelefono( teclado.nextLine());
                    
                    System.out.println("Ingrese el correo electronico del usuario ;");
                    nuevoUsuario.setCorreoElectronico(teclado.nextLine());
                    
                    nuevoUsuario.setFechaRegistro(LocalDate.now());
                    System.out.println(nuevoUsuario.getFechaRegistro());
               
                    System.out.println("");
                    usuarios.add(nuevoUsuario);

                    System.out.println("\nUsuario registrado correctamente.");

                    break;

                case 3:
                    
                    
                    System.out.println("---Registrar Prestamo");
                    
                    System.out.println("Ingrese el Id del usuario");
                    String idUsuario = teclado.nextLine();
                    
                    Usuario usuarioEncontrado = null;
                   

                    //buscar el usuario por id
                    for (Usuario usua : usuarios){
                        if (usua.getId_Usuario().equals(idUsuario)){
                           usuarioEncontrado = usua;
                           break;
                        }
                    }
                    
                    //validacion del usuario
                    if(usuarioEncontrado == null)
                    {
                        System.out.println(" Error el usuario no se ha encontrado o no existe");
                        break;
                    }
                     
                    System.out.println("Ingrese el codigo del libro a prestar");
                    String codigoLibro = teclado.nextLine();
                    
                    MaterialBibliografico libroEncontrado = null;
                    
                    //buscar por codigo de libro si se encuentra
                    for(Libro libr : libro ){
                        if (libr.getCodigo().equals(codigoLibro)){
                            libroEncontrado = libr; 
                            break;
                        }
                    }
                    //validacion del libro
                    if (libroEncontrado == null){
                        System.out.println("Error el libro no se ha encontrado o no existe");
                        break;
                    }
                    
                    //Registracion del prestamo 
                    Prestamo prestar = new Prestamo();
                    
                    System.out.println("Ingrese el Id de prestamo");
                    prestar.setIdPrestamo(teclado.nextLine());
                    
                    
                    prestar.setUsuario(usuarioEncontrado);
                    prestar.setMaterialBibliografico(libroEncontrado);
                    prestar.setFechaPrestamo(LocalDate.now());
                    prestar.realizarPrestamo();
                    
                    prestamos.add(prestar);
                    
                    break;

                case 4:
                    System.out.println("---- Registrar devolucion -----");
                    
                  if (prestamos.isEmpty()){
                      System.out.println("No hay prestamos, para devolver");
                      break;
                    }
                  
                    System.out.println("Ingrese el ID, del prestamo");
                    String idPrestamo = teclado.nextLine();
                    
                    Prestamo prestamoEncontrado = null;
                    
                    for (Prestamo pres : prestamos ){
                        if (pres.getIdPrestamo().equals(idPrestamo)){
                            prestamoEncontrado = pres;
                        }
                    }
                    
                    if (prestamoEncontrado == null ){
                        System.out.println("Error el prestamo no existe");
                        break;
                    }
                    if (!prestamoEncontrado.isActivo()){
                        System.out.println("El prestamo no esta activo");
                        break;
                    }
                    
                    prestamoEncontrado.registrarDevolucion();
                     
                     
                    break;

                case 5:
                    System.out.println("\n--- Libros en existencia ---");

                    if (libro.isEmpty()) {

                        System.out.println("Lo sentimos no exiten libros");

                    } else {

                        for (Libro lib : libro) {

                            System.out.println("-----------------------");
                            lib.mostrarInformacion();
                        }
                    }
                    break;

                case 6:
                    
                    System.out.println("---Prestamos en existencia---");
                    
                    if (prestamos.isEmpty())
                    {
                        System.out.println("Lo sentimos la lista de prestamos esta vacia");
                    }
                    else {
                        for (Prestamo pres : prestamos){
                            System.out.println("----------------------");
                            pres.mostrarInformacion();
                        }
                    }
                    break;
                case 7:

                    System.out.println("\n--- USUARIOS REGISTRADOS ---");

                    if (usuarios.isEmpty()) {

                        System.out.println("No hay usuarios registrados.");

                    } else {

                        for (Usuario usuario : usuarios) {

                            System.out.println("-----------------------");
                            usuario.mostrarInformacion();
                        }
                    }

                    break;

                case 0:
                    System.out.println("\nSaliendo del sistema...");
                    break;

                default:
                    System.out.println("\nOpcion no valida.");
            }

        } while (opcion != 0);

        teclado.close();
    }
}
