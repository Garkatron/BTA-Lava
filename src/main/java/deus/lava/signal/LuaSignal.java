package deus.lava.signal;

import org.luaj.vm2.LuaValue;
import java.util.HashMap;

public class LuaSignal {
	// Cambiar el tipo de oyentes a LuaValue
	private final HashMap<String, LuaValue> listeners = new HashMap<>();

	// Conectar una función Lua como oyente
	public void connect(String id, LuaValue listener) {
		if (!listener.isfunction()) {
			throw new IllegalArgumentException("El oyente debe ser una función Lua");
		}
		listeners.put(id, listener);
	}

	// Desconectar una función Lua como oyente
	public boolean disconnect(String id) {
		if (listeners.containsKey(id)) {
			listeners.remove(id); // Elimina el oyente de la lista
			return true; // Desconexión exitosa
		}
		return false; // El oyente no se encontró
	}

	// Emitir un evento y llamar a cada oyente
	public void emit(Object value) {
		for (LuaValue listener : listeners.values()) {
			if (listener.isfunction()) {
				try {
					listener.call(LuaValue.valueOf(value.toString()));  // Llama a la función Lua con el valor
				} catch (Exception e) {
					System.err.println("Error al llamar al oyente: " + e.getMessage());
				}
			}
		}
	}
}
