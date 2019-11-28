/*
 * consumidor= si hay tarta coge un trozo, -1 a tarta
consumidor= si no hay tarta despertar al cocinero y dormir
cocinero = si le despiertan sumar 10 tartas;
 */
package productorconsumidor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dam2
 */
public class ProductorConsumidor implements Runnable {

    //bandera que dice si es consumidor o productor
    private boolean consumidor;

    //consumible
    private static int tarta = 0;

    //candado
    private static Object lock = new Object();

    //constructor
    public ProductorConsumidor(boolean con) {
        this.consumidor = con;
    }

    @Override
    public void run() {

        //bucle infinito para que no se pare la ejecucion
        while (true) {
            //cliente
            if (consumidor) {
                comprar();
            } else {//cocinero
                cocinando();
            }

        }

    }

    public static void main(String[] args) {
        //indica el numero de hilos que se quiere crear
        int numHilos = 11;
        //crea un array con el numero de hilos indicado
        Thread[] hilos = new Thread[numHilos];

        for (int i = 0; i < hilos.length; i++) {
            Runnable runnable = null;
            if (i != 0) {
                //crea los hilos consumidores 
                runnable = new ProductorConsumidor(true);
            } else {
                //crea el cocinero si es el primerhilo del array
                runnable = new ProductorConsumidor(false);
            }
            hilos[i] = new Thread(runnable);
            hilos[i].start();
        }

        for (int i = 0; i < hilos.length; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ProductorConsumidor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    //metodos del run
    private void cocinando() {
        /*metemos el codigo de añadir tarta a syncronice y se usa el candado para 
        que no se nadie pueda acceder a ella si lo usa el cocinero
         */
        synchronized (lock) {
            //no hay tarta
            if (tarta == 0) {
                tarta = 10;
                System.out.println("Soy el cocinero hay " + tarta + " tartas");

                //despertar a los consumidores
                lock.notifyAll();
            }
            try {
                //una vez cocinadas las tartas duerme el cocinero
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ProductorConsumidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void comprar() {

        //que solo un hilo pueda acceder a la tarta para que se resten bien los trozos
        synchronized (lock) {
            //si hay tarta
            if (tarta > 0) {
                //coge un trozo
                tarta--;
                System.out.println("Aun quedan " + tarta + "tarta");
                try {
                    //el hilo se duerme para ver mejor como consume
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProductorConsumidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            //si no queda tarta se despiertan a los hilos
            //hay que despertar todos los hilos y si no hay tarta se despertara el cocinero
            lock.notifyAll();
                try {
                    //si no es el cocinero que se duerma
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProductorConsumidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        }
    }
}
