package deus.lava.setup;

import deus.lava.Lava;
import org.luaj.vm2.Globals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnvironmentManager {
	private static final HashMap<String, Globals> user_environments = new HashMap<>();
	private static final HashMap<String, Object> envVars = new HashMap<>();
	private static volatile Globals currentEnvironment = null;

	public static void addEnvVar(String name, Object o) {
		envVars.put(name, o);
	}

	public static Object getEnvVar(String name) {
		return envVars.get(name);
	}

	public static boolean createUserEnvironment(String name, Object caller) {
		if (!user_environments.containsKey(name)) {
			user_environments.put(name, LuaSandbox.createUserGlobals(caller));
			Lava.LOGGER.info("The {} Environment exists", name);
			return true;
		} else {
			Lava.LOGGER.info("The {} Environment has been created", name);
			return false;
		}
	}

	public static HashMap<String, String> getVarsAndTypes() {
		HashMap<String, String> info = new HashMap<>();

		for (Map.Entry<String, Object> entry : envVars.entrySet()) {
			info.put(entry.getKey(), entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null");
		}

		return info;
	}

	public static void loadFilesFromLavaFolder() {
		try {
			File[] luaFiles = Lava.lavaFolder.listFiles((file) -> getFileExtension(file.getName()).equals("lua"));
			if (luaFiles != null) {
				for (File file : luaFiles) {
					Lava.LOGGER.info("Lava Loaded: {}", file.getName());
					envVars.put(file.getName(), file.getPath());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf('.');
		if (lastIndexOfDot != -1 && lastIndexOfDot < fileName.length() - 1) {
			return fileName.substring(lastIndexOfDot + 1);
		}
		return "";
	}

	public static synchronized boolean removeEnvironment(String name) {
		if (!user_environments.containsKey(name)) {
			return false;
		}
		user_environments.remove(name);
		currentEnvironment = null;

		if (user_environments.containsKey(name)) return false;
		if (currentEnvironment != null) return false;

		Lava.LOGGER.info("Deleted environment: {}", name);

		System.gc();
		return true;
	}

	public static Globals getCurrentEnvironment() {
		return currentEnvironment;
	}

	public static void setCurrentEnvironment(String name) {
		currentEnvironment = user_environments.get(name);
	}

	public static Globals getEnvironment(String name) {
		return user_environments.get(name);
	}

	public static Set<String> getEnvironmentsNames() {
		return user_environments.keySet();
	}

}
