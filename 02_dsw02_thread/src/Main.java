public class Main {
    public static void main(String[] args){
        System.out.println("Main - inicio");

        //opcion A
        HiloEjemplo hilo = new HiloEjemplo();
        hilo.start();

        //opcion B
        EjecutableEjemplo ejecutable = new EjecutableEjemplo();
        Thread thread01 = new Thread(ejecutable,"Hilo 01");
        Thread thread02 = new Thread(ejecutable,"Hilo 02");
        Thread thread03 = new Thread(ejecutable,"Hilo 03");
        Thread thread04 = new Thread(ejecutable,"Hilo 04");
        Thread thread05 = new Thread(ejecutable,"Hilo 05");

        thread01.start();
        thread02.start();
        thread03.start();
        thread04.start();
        thread05.start();

        System.out.println("Main - Fin");

    }

}
