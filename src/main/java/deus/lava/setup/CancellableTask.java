package deus.lava.setup;

import deus.lava.Lava;

public class CancellableTask implements Runnable {
	private final Runnable task;
	private volatile boolean running = true;

	public CancellableTask(Runnable task) {
		this.task = task;
	}

	@Override
	public void run() {
		boolean hasRun = false;
		while (running) {
			if (!hasRun) {
				try {
					task.run();
					hasRun = true;
				} catch (Exception e) {
					Lava.LOGGER.error("Error on task: {}", e);
				}
			}
		}
	}


	public void stop() {
		running = false;
	}
}
