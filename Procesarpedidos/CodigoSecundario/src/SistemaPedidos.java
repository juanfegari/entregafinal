package src;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class SistemaPedidos {
    private final ExecutorService threadPool;
    private final List<Long> tiemposProcesamiento = new ArrayList<>();
    private final PriorityBlockingQueue<Pedido> colaPedidos = new PriorityBlockingQueue<>();

    public SistemaPedidos(int numHilos) {
        this.threadPool = Executors.newFixedThreadPool(numHilos);
    }

    public CompletableFuture<Void> procesarPedido(Pedido pedido) {
        // Almacenar el tiempo de inicio del pedido
        long inicio = System.nanoTime();
        
        // Secuencia de procesamiento: Pago -> Empaquetado -> Envío 
        //"maneja estados"
        return CompletableFuture
                .runAsync(new ProcesamientoPago(pedido), threadPool)
                .thenRunAsync(new EmpaquetadoPedido(pedido), threadPool)
                .thenRunAsync(new EnvioPedido(pedido), threadPool)
                .thenRun(() -> {
                    long fin = System.nanoTime();
                    long duracion = fin - inicio; // Tiempo en nanosegundos
                    synchronized (tiemposProcesamiento) {
                        tiemposProcesamiento.add(duracion); // Añadir el tiempo de procesamiento
                    }
                    System.out.println("Tiempo de procesamiento del " + pedido + ": " + duracion / 1_000_000 + " ms");
                })
                .exceptionally(ex -> {
                    System.err.println("Error procesando el pedido " + pedido.getId() + ": " + ex.getMessage());
                    return null;
                });
    }

    // Agregar pedidos a la cola de prioridad
    public void agregarPedido(Pedido pedido) {
        colaPedidos.add(pedido);
    }

    public void procesarColaPedidos() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        while (!colaPedidos.isEmpty()) {
            Pedido pedido = colaPedidos.poll();
            futures.add(procesarPedido(pedido));
        }
        cerrarSistema(futures);
    }

    // Calcular la media de los tiempos de procesamiento
    public void calcularMediaTiempoProcesamiento() {
        long sumaTotal = tiemposProcesamiento.stream().mapToLong(Long::longValue).sum();
        double media = (double) sumaTotal / tiemposProcesamiento.size();
        System.out.println("Tiempo medio de procesamiento: " + media / 1_000_000 + " ms");
    }

    public void cerrarSistema(List<CompletableFuture<Void>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        
        calcularMediaTiempoProcesamiento();
    }

    public static void main(String[] args) {
        SistemaPedidos sistema = new SistemaPedidos(30);  
        
        for (int i = 1; i <= 100; i++) {
            sistema.agregarPedido(new Pedido(i, 10));
        }
        for (int i = 101; i <= 200; i++) {
            sistema.agregarPedido(new Pedido(i, 5));
        }
        for (int i = 201; i <= 300; i++) {
            sistema.agregarPedido(new Pedido(i, 1));
        }
        /* 
        for (int i = 301; i <= 320; i++) {
            sistema.agregarPedido(new Pedido(i, 1));  
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        */
        sistema.procesarColaPedidos();
    }
}