package ejercicio_01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        try {
            Path carpeta= Paths.get("data");
            //Path carpeta= Paths.get("C:\\temp\\cibertec\\files");
            Files.createDirectories(carpeta);

            Path archivoTxt = carpeta.resolve("salida.txt");
            Path archivoCsv = carpeta.resolve("salida.csv");

            //Files.deleteIfExists(archivoTxt);
            if(Files.notExists(archivoTxt)) Files.createFile(archivoTxt);
            if(Files.notExists(archivoCsv)) Files.createFile(archivoCsv);

            System.out.println("Carpeta: " + carpeta.toAbsolutePath());
            System.out.println("Paths Txt: " + archivoTxt.toAbsolutePath());
            System.out.println("Paths Csv: " + archivoCsv.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
