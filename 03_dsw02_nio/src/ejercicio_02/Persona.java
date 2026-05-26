package ejercicio_02;

public class Persona {
    private int id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String direccion;

    public Persona(int id,  String dni, String nombres, String apellidos, String direccion){
        this.id =id;
        this.dni= dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
    }
    public int getId(){ return id;}
    public String getDni() {return dni;}
    public String getNombres() {return nombres;}
    public String getApellidos() {return apellidos;}
    public String getDireccion() {return direccion;}

}
