import java.util.concurrent.ThreadLocalRandom;

public class EjecutableEjemplo implements Runnable {

    @Override
    public void run(){
        try{
            int randomNum = ThreadLocalRandom.current().nextInt(3000);
            System.out.println(Thread.currentThread().getName() + " - Inicio");
            Thread.sleep(randomNum);
            System.out.println(Thread.currentThread().getName() + " - Finaliza  " + randomNum + "ms");
        } catch ( InterruptedException e){
            e.printStackTrace();
        }
    }
}
