package ejercicio_02;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args){
        try {
            Path carpeta= Paths.get("data\\ejercicio02");
            Files.createDirectories(carpeta);

            List<Persona> personas = obtenerDatos();
            Path archivoTab = carpeta.resolve("personas_tab.txt");
            Path archivoTxt = carpeta.resolve("personas_pipe.txt");
            Path archivoCsv = carpeta.resolve("personas_coma.csv");

            escribirArchivo(personas,archivoTab, "\t");
            escribirArchivo(personas,archivoTxt, "|");
            escribirArchivo(personas,archivoCsv, ",");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<Persona> obtenerDatos(){
        return List.of(
                new Persona(1, "45037013", "Ana", "Ramos", "Av. Miraflores"),
                new Persona(2, "65037013", "Mario", "Perez", "Av. Ate"),
                new Persona(3, "34037013", "Madeleine", "Mamani", "Av. SJL")
        );
    }

    static void escribirArchivo( List<Persona> personas, Path archivo, String sep){
        try(BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)){
            bw.write("id" + sep + "dni" + sep + "nombres" + sep + "apellidos" + sep + "direccion");
            bw.newLine();
            for(Persona per: personas){
                bw.write(per.getId() + sep + per.getDni() + sep + per.getNombres()
                                + sep + per.getApellidos() + sep + per.getDireccion()
                        );
                bw.newLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
