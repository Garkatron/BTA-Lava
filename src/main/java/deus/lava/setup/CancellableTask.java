package deus.lava.setup;

import deus.lava.Lava;
import org.luaj.vm2.Globals;

import java.util.HashMap;

public class CancellableTask implements Runnable {
	private volatile boolean running = true; // Flag para controlar la ejecución
	private final Runnable task; // Tarea que se va a ejecutar
	private static final HashMap<String, CancellableTask> user_tasks = new HashMap<>();

	// Constructor que acepta un Runnable
	public CancellableTask(Runnable task) {
		this.task = task; // Asigna la tarea proporcionada
	}

	@Override
	public void run() {
		while (running) {
			try {
				// Ejecuta la tarea proporcionada
				task.run();

				// Simulación de un trabajo que toma tiempo
				Thread.sleep(100); // Por ejemplo, dormir 100 ms
			} catch (InterruptedException e) {
				// El hilo fue interrumpido, salir del bucle
				Thread.currentThread().interrupt(); // Mantiene el estado de interrupción
				break; // Salir del bucle
			} catch (Exception e) {
				// Manejo de excepciones durante la ejecución de la tarea
				Lava.LOGGER.error("Error en la tarea: {}", e);
			}
		}
	}

	public void stop() {
		running = false; // Cambiar el flag para detener la ejecución
	}
}
