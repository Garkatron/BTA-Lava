package deus.lava.command;

import net.minecraft.core.net.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommand {

	private final Map<String, SubCommand> subCommands = new HashMap<>();
	private final String description;
	private final String name;
	private final CommandRunnable commandRunnable;
	private boolean requireOp = false;

	public SubCommand(String name, String description, CommandRunnable commandRunnable) {
		this.name = name;
		this.description = description;
		this.commandRunnable = commandRunnable;
	}

	public SubCommand(boolean requireOp, String name, String description, CommandRunnable commandRunnable) {
		this.requireOp = requireOp;
		this.name = name;
		this.description = description;
		this.commandRunnable = commandRunnable;
	}

	public HashMap<String, String> getSubcommandsHelp() {
		HashMap<String, String> help = new HashMap<>();
		subCommands.forEach((k,v)->{
			help.put(v.getName(), v.getDescription());
		});
		return help;
	}

	public Map<String, SubCommand> getSubCommands() {
		return subCommands;
	}
	public static List<HashMap<String, String>> getSubCommandHelpRecursive(SubCommand subCommand) {
		List<HashMap<String, String>> helpList = new ArrayList<>();

		for (SubCommand subCommand2 : subCommand.getSubCommands().values()) {
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put(subCommand2.getName(), subCommand2.getDescription());
			helpList.add(hashMap);

			helpList.addAll(getSubCommandHelpRecursive(subCommand2));
		}

		return helpList;
	}

	public static List<HashMap<String, Object>> getSubCommandHelpRecursiveNested(SubCommand subCommand) {
		List<HashMap<String, Object>> helpList = new ArrayList<>();

		// Iterar a través de todos los subcomandos de "subCommand"
		for (SubCommand subCommand2 : subCommand.getSubCommands().values()) {
			// Crear un nuevo mapa para el subcomando actual
			HashMap<String, Object> subCommandMap = new HashMap<>();

			// Añadir la descripción del subcomando
			subCommandMap.put("name", subCommand2.getName());
			subCommandMap.put("description", subCommand2.getDescription());

			// Llamada recursiva para obtener subcomandos anidados
			List<HashMap<String, Object>> nestedSubCommands = getSubCommandHelpRecursiveNested(subCommand2);

			// Añadir subcomandos anidados si existen
			if (!nestedSubCommands.isEmpty()) {
				subCommandMap.put("subcommands", nestedSubCommands);
			}

			// Añadir el subcomando actual al listado de ayuda
			helpList.add(subCommandMap);
		}

		return helpList;
	}

	public void printSubCommandHelp(List<HashMap<String, Object>> subCommandList, int level) {
		for (HashMap<String, Object> subCommand : subCommandList) {
			// Obtener el nombre y la descripción
			String name = (String) subCommand.get("name");
			String description = (String) subCommand.get("description");

			// Imprimir con indentación basada en el nivel de profundidad
			System.out.println("  ".repeat(level) + "- " + name + ": " + description);

			// Verificar si hay subcomandos anidados y llamar recursivamente
			if (subCommand.containsKey("subcommands")) {
				List<HashMap<String, Object>> nestedSubCommands =
					(List<HashMap<String, Object>>) subCommand.get("subcommands");
				printSubCommandHelp(nestedSubCommands, level + 1);
			}
		}
	}


	public void addSubCommand(SubCommand subCommand) {
		subCommands.put(subCommand.getName(), subCommand);
	}

	public SubCommand getSubCommand(String name) {
		return subCommands.get(name);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public CommandRunnable getCommandRunnable() {
		return commandRunnable;
	}

	public void execute(CommandSender commandSender, String[] args) {
		if (args.length > 0 && subCommands.containsKey(args[0])) {
			String subCommandName = args[0];
			SubCommand subCommand = subCommands.get(subCommandName);
			subCommand.execute(commandSender, trimArgs(args, 1));
		} else {
			commandRunnable.run(commandSender, args);
		}
	}

	private String[] trimArgs(String[] args, int startIndex) {
		if (startIndex >= args.length) return new String[0];
		String[] trimmedArgs = new String[args.length - startIndex];
		System.arraycopy(args, startIndex, trimmedArgs, 0, trimmedArgs.length);
		return trimmedArgs;
	}
}
