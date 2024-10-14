package deus.lava.command;

import deus.lava.setup.CancellableTask;
import deus.lava.setup.EnvironmentManager;
import deus.lava.setup.LuaSandbox;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import org.luaj.vm2.Globals;

import java.util.HashMap;
import java.util.List;

public class EnvironmentCommand extends Command {

	private final SubCommand rootCommand;

	public EnvironmentCommand(String... alts) {
		super("env", alts);

		// Definir el comando raíz para "env"
		rootCommand = new SubCommand("env", "Root command for environment operations",
			(commandSender, args) -> {
				commandSender.sendMessage("Usage: /env <subcommand> [options]");
				return false;
			});

		SubCommand createCommand = new SubCommand("create", "Create an environment",
			(commandSender, args) -> {
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env create <environment_name>");
					return false;
				}
				EnvironmentManager.createUserEnvironment(args[0], this);
				commandSender.sendMessage("Environment created.");
				return true;
			});

		SubCommand setCommand = new SubCommand("set", "set current a environment",
			(commandSender, args) -> {
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env set <environment_name>");
					return false;
				}
				EnvironmentManager.setCurrentEnvironment(args[0]);
				commandSender.sendMessage("Success.");
				return true;
			});

		SubCommand removeCommand = new SubCommand("remove", "Remove an environment",
			(commandSender, args) -> {
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env remove <environment_name>");
					return false;
				}
				if (EnvironmentManager.removeEnvironment(args[0])) {
					commandSender.sendMessage("Environment removed.");
					return true;
				} else {
					return false;
				}
			});

		SubCommand executeCommand = new SubCommand("execute", "Execute a function or script",
			(commandSender, args) -> {
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env execute <subcommand> [options]");
					return false;
				}
				commandSender.sendMessage("Executing subcommand...");
				return false;
			});

		SubCommand stop = new SubCommand("stop", "Stop a task",
			((commandSender, args) -> {
				commandSender.sendMessage("Trying to stop task: " + args[0]);
				return LuaSandbox.stopTask(args[0]);
			}));

		SubCommand test = new SubCommand("test", "Create and set 'test' environment and then use it with some command",
			((commandSender, args) -> {
				createCommand.execute(commandSender, new String[]{"test"});
				setCommand.execute(commandSender, new String[]{"test"});
				executeCommand.execute(commandSender, args);
				return false;
			}));

		executeCommand.addSubCommand(new SubCommand("fun", "Execute a function",
			(commandSender, args) -> {
				Globals env = EnvironmentManager.getCurrentEnvironment();
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env execute fun <function_name>");
					return false;
				}
				if (!env.get(args[0]).isfunction()) {
					commandSender.sendMessage("Function " + args[0] + " does not exist.");
					return false;
				}
				commandSender.sendMessage("Function " + args[0] + " called");
				env.get(args[0]).call();
				return true;
			}));

		executeCommand.addSubCommand(new SubCommand("file", "Run a Lua file",
			(commandSender, args) -> {
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env execute file <file_path>");
					return false;
				}
				CancellableTask myTask = new CancellableTask(() -> {
					LuaSandbox.runFileFromPath(args[0], EnvironmentManager.getCurrentEnvironment());

				});
				LuaSandbox.executeTask(args[0], myTask);
				return true;
			}));

		executeCommand.addSubCommand(new SubCommand("code", "Execute Lua code",
			(commandSender, args) -> {
				if (args.length < 1) {
					commandSender.sendMessage("Usage: /env execute code <lua_code>");
					return false;
				}
				Globals env = EnvironmentManager.getCurrentEnvironment();
				String script = String.join(" ", args);
				LuaSandbox.runScriptSandbox(script, env);
				return true;
			}));

		SubCommand helpCommand = new SubCommand(
			"help", "Help command for see env options",
			(commandSender, args) -> {

				printSubCommandHelp(SubCommand.getSubCommandHelpRecursiveNested(rootCommand),commandSender,0);

				return true;
			});

		rootCommand.addSubCommand(test);
		rootCommand.addSubCommand(stop);
		rootCommand.addSubCommand(setCommand);
		rootCommand.addSubCommand(helpCommand);
		rootCommand.addSubCommand(createCommand);
		rootCommand.addSubCommand(removeCommand);
		rootCommand.addSubCommand(executeCommand);
	}

	public void printSubCommandHelp(List<HashMap<String, Object>> subCommandList, CommandSender commandSender, int level) {
		for (HashMap<String, Object> subCommand : subCommandList) {
			// Obtener el nombre y la descripción
			String name = (String) subCommand.get("name");
			String description = (String) subCommand.get("description");

			// Imprimir con indentación basada en el nivel de profundidad
			commandSender.sendMessage("  ".repeat(level) + "- " + name + ": " + description);

			// Verificar si hay subcomandos anidados y llamar recursivamente
			if (subCommand.containsKey("subcommands")) {
				List<HashMap<String, Object>> nestedSubCommands =
					(List<HashMap<String, Object>>) subCommand.get("subcommands");
				printSubCommandHelp(nestedSubCommands, commandSender,level + 1);
			}
		}
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
		rootCommand.execute(commandSender, args);
		return true;
	}

	@Override
	public boolean opRequired(String[] args) {
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
		commandSender.sendMessage("Usage: /env <subcommand> [options]");
	}
}
