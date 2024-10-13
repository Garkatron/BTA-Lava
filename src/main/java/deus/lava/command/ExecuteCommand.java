package deus.lava.command;

import deus.lava.Lava;
import deus.lava.setup.LuaSandbox;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecuteCommand extends Command {

	private String regex = "^[a-zA-Z0-9_\\-]+\\.[a-zA-Z0-9]{2,6}$";
	private Pattern pattern = Pattern.compile(regex);

	public ExecuteCommand(String... alts) {
		super("execute", alts);
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		if (strings.length < 2) {
			commandSender.sendMessage("§cError: A second argument is required.");
			return false;
		}

		switch (strings[0].trim().toLowerCase()) {
			case "file": {
				commandSender.sendMessage("Running code from file: " + strings[1]);
				LuaSandbox.runFileFromPath(strings[1]);
				return true;
			}
			case "code": {
				String script = Arrays.stream(strings, 1, strings.length).collect(Collectors.joining(" "));
				//LuaSandbox.executeTask(strings[1],()->{
				LuaSandbox.runScriptSandbox(script);
				//});
				return true;
			}
			case "stop": {
				commandSender.sendMessage("Stopping lua task: " + strings[1]);
				LuaSandbox.stopLuaThread(strings[1]);
				/*boolean r =
				if (!r) {
					Lava.LOGGER.error("Error stopping lua task: {}", strings[1]);
				} else {

					Lava.LOGGER.error("Successful stopping lua task: {}", strings[1]);
				}
				return r;*/
				return true;
			}
			default: {
				commandSender.sendMessage("§cError: Unrecognized command.");
				return false;
			}
		}
	}

	@Override
	public boolean opRequired(String[] strings) {
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
		commandSender.sendMessage("§8< Command Syntax >");
		commandSender.sendMessage("§8> /execute <kind> <filePath.extension>");
	}
}
