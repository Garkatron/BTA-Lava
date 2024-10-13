package deus.lava.tool;

import net.minecraft.core.item.Item;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import java.lang.reflect.Field;

public class ItemRegistry {

	public static LuaTable getAllItemsAsLuaTable() {
		LuaTable luaTable = new LuaTable();

		// Obtener todos los campos de la clase Item
		Field[] fields = Item.class.getDeclaredFields();

		for (Field field : fields) {
			// Verificar si el campo es estático y de tipo Item
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
				&& field.getType() == Item.class) {

				try {
					String itemName = field.getName();
					Item item = (Item) field.get(null); // Obtener el valor del campo estático
					if (item != null) {
						luaTable.set(itemName, CoerceJavaToLua.coerce(item));
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return luaTable;
	}
}
