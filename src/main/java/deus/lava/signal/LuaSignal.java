package deus.lava.signal;

import org.luaj.vm2.LuaValue;
import java.util.ArrayList;
import java.util.List;

public class LuaSignal {
	// Cambiar el tipo de oyentes a LuaValue
	private final List<LuaValue> listeners = new ArrayList<>();

	// Conectar una función Lua como oyente
	public void connect(LuaValue listener) {
		listeners.add(listener);
	}

	// Emitir un evento y llamar a cada oyente
	public void emit(Object value) {
		for (LuaValue listener : listeners) {
			if (listener.isfunction()) {
				listener.call(LuaValue.valueOf(value.toString()));  // Llama a la función Lua con el valor
			}
		}
	}
}
