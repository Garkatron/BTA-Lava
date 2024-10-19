package deus.lava.signal;

import org.luaj.vm2.LuaValue;
import java.util.HashMap;

public class LuaSignal {
	private final HashMap<String, LuaValue> listeners = new HashMap<>();

	public void connect(String id, LuaValue listener) {
		if (!listener.isfunction()) {
			throw new IllegalArgumentException("El oyente debe ser una función Lua");
		}
		listeners.put(id, listener);
	}

	public boolean disconnect(String id) {
		if (listeners.containsKey(id)) {
			listeners.remove(id);
			return true;
		}
		return false;
	}

	public void emit(Object value) {
		for (LuaValue listener : listeners.values()) {
			if (listener.isfunction()) {
				try {
					listener.call(LuaValue.valueOf(value.toString()));
				} catch (Exception e) {
					System.err.println("Error al llamar al oyente: " + e.getMessage());
				}
			}
		}
	}
}
