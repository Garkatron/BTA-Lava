package deus.lava.tool;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassToTable {

	public static LuaTable convertToLuaTable(Class<?> clazz, Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("La instancia no puede ser nula");
		}
		LuaTable luaTable = new LuaTable();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
				try {
					luaTable.set(field.getName(), CoerceJavaToLua.coerce(field.get(instance)));
				} catch (IllegalAccessException e) {
					System.err.println("Error al acceder al campo: " + field.getName());
					e.printStackTrace();
				} catch (NullPointerException e) {
					System.err.println("Campo " + field.getName() + " es nulo en la instancia.");
				}
			}
		}

		// Obtener solo métodos públicos
		Method[] methods = clazz.getDeclaredMethods(); // Usar getDeclaredMethods para obtener todos los métodos
		for (Method method : methods) {
			if (java.lang.reflect.Modifier.isPublic(method.getModifiers()) && method.getDeclaringClass() == clazz) {
				luaTable.set(method.getName(), CoerceJavaToLua.coerce(method));
			}
		}

		System.out.println(luaTable);
		return luaTable;
	}

}
