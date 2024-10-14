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

import static deus.lava.command.SubCommands.*;

public class EnvironmentCommand extends Command {


	public EnvironmentCommand(String... alts) {
		super("env", alts);


	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
		env.execute(commandSender, args);
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
