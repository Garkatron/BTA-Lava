package deus.lava.command;

import deus.lava.setup.CancellableTask;
import deus.lava.setup.EnvironmentManager;
import deus.lava.setup.LuaSandbox;
import net.minecraft.core.net.command.CommandSender;
import org.luaj.vm2.Globals;

import java.util.HashMap;
import java.util.List;

public class SubCommands {

	public static final SubCommand env = new SubCommand("env", "Root command for environment operations",
		(commandSender, args) -> {
			commandSender.sendMessage("Usage: /env <subcommand> [options]");
			return false;
		});
	// Creación de subcomandos
	public static SubCommand createCommand = new SubCommand("create", "Create an environment",
		(commandSender, args) -> {
			if (args.length < 1) {
				commandSender.sendMessage("Usage: /env create <environment_name>");
				return false;
			}

			boolean result = EnvironmentManager.createUserEnvironment(args[0], SubCommands.class);
			if (result) {
				commandSender.sendMessage("Environment created.");
				return true;
			} else {
				return false;
			}
		});
	public static SubCommand setCommand = new SubCommand("set", "Set current environment",
		(commandSender, args) -> {
			if (args.length < 1) {
				commandSender.sendMessage("Usage: /env set <environment_name>");
				return false;
			}
			EnvironmentManager.setCurrentEnvironment(args[0]);
			commandSender.sendMessage("Success.");
			return true;
		});
	public static SubCommand stopCommand = new SubCommand("stop", "Stop a task",
		(commandSender, args) -> {
			if (args.length < 1) {
				commandSender.sendMessage("Usage: /env stop <task_name>");
				return false;
			}
			commandSender.sendMessage("Trying to stop task: " + args[0]);
			return LuaSandbox.stopTask(args[0]);
		});
	public static SubCommand removeCommand = new SubCommand("remove", "Remove an environment",
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
	public static SubCommand executeCommand = new SubCommand("execute", "Execute a function or script",
		(commandSender, args) -> {
			if (args.length < 1) {
				commandSender.sendMessage("Usage: /env execute <subcommand> [options]");
				return false;
			}
			commandSender.sendMessage("Executing subcommand...");
			return false;
		});
	public static SubCommand test = new SubCommand("test", "Create and set 'test' environment and then use it with some command",
		(commandSender, args) -> {
			createCommand.execute(commandSender, new String[]{"test"});
			setCommand.execute(commandSender, new String[]{"test"});
			executeCommand.execute(commandSender, args);
			return false;
		});
	public static SubCommand funSubCommand = new SubCommand("fun", "Execute a function",
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
		});
	public static SubCommand fileSubcommand = new SubCommand("file", "Run a Lua file",
		(commandSender, args) -> {
			String filePath;
			if (args.length < 1) {
				commandSender.sendMessage("Usage: /env execute file <file_path>");
				return false;
			} else if ("from".equals(args[0]) && args.length > 2) {
				Object envVar = EnvironmentManager.getEnvVar(args[1]);
				if (envVar instanceof String) {
					filePath = (String) envVar;
				} else {
					return false;
				}
			} else {
				filePath = args[0];
			}

			CancellableTask myTask = new CancellableTask(() -> {
				LuaSandbox.runFileFromPath(filePath, EnvironmentManager.getCurrentEnvironment());
			});
			LuaSandbox.executeTask(filePath, myTask);
			return true;
		});
	// Subcomando para ejecutar código
	public static SubCommand codeSubCommand = new SubCommand("code", "Execute Lua code",
		(commandSender, args) -> {
			String script;
			Globals env = EnvironmentManager.getCurrentEnvironment();

			if (args.length < 1) {
				commandSender.sendMessage("Usage: /env execute code <lua_code>");
				return false;
			} else if ("from".equals(args[0]) && args.length > 2) {
				Object envVar = EnvironmentManager.getEnvVar(args[1]);
				if (envVar instanceof String) {
					script = (String) envVar;
					LuaSandbox.runScriptSandbox(script, env);
					return true;
				} else {
					return false;
				}
			} else {
				script = String.join(" ", args);
				LuaSandbox.runScriptSandbox(script, env);
				return true;
			}
		});
	public static SubCommand addEnvVarSubCommand = new SubCommand("addVar",
		"Add a variable to the environment dictionary, which can be accessed using the 'from' keyword in 'code' or 'file' subcommands of 'execute'.",
		(commandSender, args) -> {
			if (args.length < 2) {
				commandSender.sendMessage("Usage: /env addVar <var_name> <var_value>");
				return false;
			}

			EnvironmentManager.addEnvVar(args[0], args[1]);
			commandSender.sendMessage("Variable added: " + args[0]);
			return true;
		});

	public static SubCommand listEnvVars = new SubCommand("listVars",
		"List the vars of the nve var list",
		(commandSender, args) -> {
			HashMap<String, String> vars = EnvironmentManager.getVarsAndTypes();

			if (vars.isEmpty()) {
				commandSender.sendMessage("No environment variables found.");
			} else {
				vars.forEach((key, value) -> {
					commandSender.sendMessage(key + ": " + value);
				});
			}

			return true;
		});

	static {
		executeCommand.addSubCommand(fileSubcommand);
		executeCommand.addSubCommand(codeSubCommand);
		executeCommand.addSubCommand(funSubCommand);
		executeCommand.addSubCommand(SubCommands.getHelpSubCommand(SubCommands.executeCommand));

		env.addSubCommand(listEnvVars);
		env.addSubCommand(createCommand);
		env.addSubCommand(setCommand);
		env.addSubCommand(removeCommand);
		env.addSubCommand(executeCommand);
		env.addSubCommand(test);
		env.addSubCommand(addEnvVarSubCommand);
		env.addSubCommand(SubCommands.getHelpSubCommand(env));

	}

	public static SubCommand getHelpSubCommand(SubCommand rootCommand) {
		return new SubCommand(
			"help", "Help command for see options",
			(commandSender, args) -> {
				printSubCommandHelp(SubCommand.getSubCommandHelpRecursiveNested(rootCommand), commandSender, 0);
				return true;
			});
	}

	public static void printSubCommandHelp(List<HashMap<String, Object>> subCommandList, CommandSender commandSender, int level) {
		for (HashMap<String, Object> subCommand : subCommandList) {
			String name = (String) subCommand.get("name");
			String description = (String) subCommand.get("description");

			commandSender.sendMessage("  ".repeat(level) + "§e " + name + ": §3" + description);

			if (subCommand.containsKey("subcommands")) {
				List<HashMap<String, Object>> nestedSubCommands =
					(List<HashMap<String, Object>>) subCommand.get("subcommands");
				printSubCommandHelp(nestedSubCommands, commandSender, level + 1);
			}
		}
	}
}
