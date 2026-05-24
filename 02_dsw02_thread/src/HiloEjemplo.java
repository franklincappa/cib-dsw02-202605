import java.util.concurrent.ThreadLocalRandom;

public class HiloEjemplo extends Thread {

    @Override
    public void run(){
        try{
            int randomNum = ThreadLocalRandom.current().nextInt(5000);
            System.out.println("Hilo - Inicio");
            Thread.sleep(randomNum);
            System.out.println("Hilo - Finalizado");
        } catch ( InterruptedException e){
            e.printStackTrace();
        }
    }
}
