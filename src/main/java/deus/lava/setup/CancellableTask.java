package deus.lava.setup;

import deus.lava.Lava;

import java.util.HashMap;

public class CancellableTask implements Runnable {
	private final Runnable task; // Tarea que se va a ejecutar
	private volatile boolean running = true; // Flag para controlar la ejecución

	// Constructor que acepta un Runnable
	public CancellableTask(Runnable task) {
		this.task = task; // Asigna la tarea proporcionada
	}

	@Override
	public void run() {
		boolean hasRun = false; // Variable para rastrear si la tarea ya se ha ejecutado
		while (running) {
			if (!hasRun) { // Verifica si la tarea ya se ejecutó
				try {
					task.run(); // Ejecuta la tarea solo una vez
					hasRun = true; // Marca que la tarea ya se ha ejecutado
				} catch (Exception e) {
					Lava.LOGGER.error("Error on task: {}", e);
				}
			}
			// Mantiene el bucle activo hasta que se llame a stop()
		}
	}


	public void stop() {
		running = false; // Cambiar el flag para detener la ejecución
	}
}
