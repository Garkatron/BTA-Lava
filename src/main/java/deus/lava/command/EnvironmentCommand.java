package deus.lava.command;

import deus.lava.setup.EnvironmentManager;
import deus.lava.setup.LuaSandbox;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import org.luaj.vm2.Globals;

import java.util.Arrays;
import java.util.HashMap;

public class EnvironmentCommand extends Command {

	//private static final String[] opsL0 = new String[]{"create", "del", "set", "exec"};
	//private static final String[] opsL1 = new String[]{"code", "file", "fun"};

	private static final HashMap<String, CommandRunnable> opsL0 = new HashMap<>();
	private static final HashMap<String, String> opsShort = new HashMap<>();
	private static final HashMap<String, String> desc = new HashMap<>();



	private static void addSubCommand(String name, String sname, String description, CommandRunnable runnable) {
		HashMap<String, String> desc = new HashMap<>();
		opsL0.put(name, runnable);
		opsShort.put(name, sname);
		desc.put(name, description);
	}

	private static boolean executeSubcommand(String name, String[] args, CommandSender commandSender) {
		return opsL0.get(name).run(commandSender, args);
	}

	public EnvironmentCommand(String... alts) {
		super("env", alts);

		addSubCommand("create", "c", "create a enviroment, second arg is the name",
			(commandSender, args)->{
				EnvironmentManager.createUserEnvironment(args[1]);
				commandSender.sendMessage(String.valueOf(EnvironmentManager.getEnvironmentsNames()));
				return true;
			});

		addSubCommand("remove", "rem", "remove a environment",
			(commandSender, args)->{
				EnvironmentManager.removeEnvironment(args[1]);
				return true;
			});


		addSubCommand("set", "set", "set a environment",
			(commandSender, args)->{
				EnvironmentManager.setCurrentEnvironment(args[1]);
				return true;
			});

		addSubCommand("execute", "exec", "exec something",
			(commandSender, args)->{
				Globals currentEnvironment = EnvironmentManager.getCurrentEnvironment();

				if (currentEnvironment == null) {
					commandSender.sendMessage("The environment doesn't exists");
					return false;
				}

					executeSubcommand(args[1], args, commandSender);

				return true;
			});

		addSubCommand("fun", "exec", "exect something",
			(commandSender, args)->{
				Globals currentEnvironment = EnvironmentManager.getCurrentEnvironment();

				if (!currentEnvironment.get(args[2]).isfunction()) {
					commandSender.sendMessage("The function: " + args[2] + " doesn't exists");
					return false;
				}

				currentEnvironment.get(args[2]).call();

				return true;
			});

		addSubCommand("file", "exec", "exect something",
			(commandSender, args)->{
				Globals currentEnvironment = EnvironmentManager.getCurrentEnvironment();

				LuaSandbox.runFileFromPath(args[2], currentEnvironment);

				return true;
			});

		addSubCommand("code", "exec", "exect something",
			(commandSender, args)->{
				Globals currentEnvironment = EnvironmentManager.getCurrentEnvironment();

				LuaSandbox.runScriptSandbox(args[3], currentEnvironment);

				return true;
			});

	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		return executeSubcommand(strings[0], strings, commandSender);



//		if (arg0.equals(opsL0[0])) {
//
//			return true;
//		} else if (arg0.equals(opsL0[1])) {
//			return true;
//		} else if (arg0.equals(opsL0[2])) {
//
//		} else if (arg0.equals(opsL0[3])) {
//			String arg2 = strings[2].trim().toLowerCase();
//			//String arg3 = strings[3].trim().toLowerCase();
//
//
//			switch (arg1) {
//				case "code": {
//					LuaSandbox.runScriptSandbox(arg2, currentEnvironment);
//					return true;
//				}
//				case "file": {
//					LuaSandbox.runFileFromPath(arg2, currentEnvironment);
//					return true;
//				}
//				case "fun": {
//					LuaValue fun = EnvironmentManager.getCurrentEnvironment().get(arg2);
//					if (!fun.isfunction()) {
//						commandSender.sendMessage("This function " + arg2 + " doesn't exists in this environment");
//						return false;
//					}
//					fun.call();
//					return true;
//				}
//			}
//
//			return false;
//		}
//		return false;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {

	}
}
