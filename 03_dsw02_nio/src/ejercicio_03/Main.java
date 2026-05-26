package ejercicio_03;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        try {
            Path carpeta= Paths.get("data\\ejercicio02");

            Path archivoTab = carpeta.resolve("personas_tab.txt");
            Path archivoTxt = carpeta.resolve("personas_pipe.txt");
            Path archivoCsv = carpeta.resolve("personas_coma.csv");

            leerMostrar(archivoTab, "\t");
            leerMostrar(archivoTxt, "|");
            leerMostrar(archivoCsv, ",");

        } catch (Exception e) {
        e.printStackTrace();
        }

    }

    static void leerMostrar(Path archivo, String sep){
        String regexSep = sep.equals("|") ? "\\|" : sep;

        try(BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)){
            String linea;
            boolean primera = true;

            while ( (linea = br.readLine()) != null){
                if(primera){
                    primera= false;
                    continue;
                }

                String[] c = linea.split(regexSep);

                if(c.length<5){
                    System.out.println("Linea inválida:" + linea);
                    continue;
                }

                System.out.println(
                        "id:" + c[0] + " dni: " +  c[1]+ " nombres: " +  c[2]
                                + " apellidos: " +  c[3]+ " direccion: " +  c[4]
                );

            }

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
