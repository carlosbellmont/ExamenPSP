import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {

        Semaphore semaforoPrueba1 = new Semaphore(10);
        Semaphore semaforoPrueba2 = new Semaphore(0, true);
        AtomicInteger espaciosLibes = new AtomicInteger(4);

        ArrayList<Jugador> lJugadores = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Jugador j = new Jugador(String.valueOf(i), semaforoPrueba1, semaforoPrueba2, espaciosLibes);
            lJugadores.add(j);
        }

        for (int i = 0; i < 20; i++) {
            lJugadores.get(i).start();
        }
        try {
            for (int i = 0; i < 20; i++) {
                lJugadores.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Jugador extends Thread {

    private final String nombre;
    private final Semaphore semaforoPrueba1;
    private final Semaphore semaforoPrueba2;
    private AtomicInteger especiosLibes;
    private final Random r = new Random();

    Jugador(String nombre, Semaphore semaforoPrueba1, Semaphore semaforoPrueba2, AtomicInteger especiosLibes) {
        this.nombre = nombre;
        this.semaforoPrueba1 = semaforoPrueba1;
        this.semaforoPrueba2 = semaforoPrueba2;
        this.especiosLibes = especiosLibes;
    }

    @Override
    public void run() {
        try {
            if (realizarPrueba(1)) {
                if (semaforoPrueba1.tryAcquire()){
                    System.out.println("El jugador " + nombre + " ha completado a tiempo a la prueba 1");

                    if (semaforoPrueba1.availablePermits() == 0){
                        semaforoPrueba2.release(10);
                    }
                    semaforoPrueba2.acquire();
                    if (realizarPrueba(2)) {
                        int posicion = especiosLibes.getAndDecrement();
                        if (posicion >= 0) {
                            if (posicion == 4)
                                System.out.println("El jugador " + nombre + " ha ganado");
                            else
                                System.out.println("El jugador " + nombre + " ha quedado en la posición " + (5 - posicion));
                        } else {
                            System.out.println("El jugador " + nombre + " no ha completado a tiempo a la prueba 2 y ha sido descalificado");
                        }
                    }
                } else {
                    System.out.println("El jugador " + nombre + " no ha completado a tiempo a la prueba 1 y ha sido descalificado");
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean realizarPrueba(int numPrueba) {
        try {
            Thread.sleep((r.nextInt(3) + 1) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean superado = (r.nextInt(10) != 9);
        if (superado) {
            System.out.println("El jugador " + nombre + " ha superado la prueba " + numPrueba);
        } else {
            System.out.println("El jugador " + nombre + " ha sido descalificado en la prueba " + numPrueba);
        }
        return (r.nextInt(10) != 9);
    }
}