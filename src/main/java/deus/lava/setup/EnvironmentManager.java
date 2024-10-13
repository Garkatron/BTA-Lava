package deus.lava.setup;

import org.luaj.vm2.Globals;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EnvironmentManager {
	private static final HashMap<String, Globals> user_environments = new HashMap<>();
	private static Globals currentEnvironment = null;

	public static void createUserEnvironment(String name) {
		user_environments.put(name, LuaSandbox.createUserGlobals());
	}

	public static void setCurrentEnvironment(String name) {
		currentEnvironment = user_environments.get(name);
	}

	public static void removeEnvironment(String name) {
		user_environments.remove(name);
	}


	public static Globals getCurrentEnvironment() {
		return currentEnvironment;
	}


	public static Globals getEnvironment(String name) {
		return user_environments.get(name);
	}

	public static Set<String> getEnvironmentsNames() {
		return user_environments.keySet();
	}

}
