package deus.lava.command;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class LavaCommand extends Command {

	private final SubCommand rootCommand;


	public LavaCommand(String... alts) {
		super("lava", alts);
		rootCommand = new SubCommand("lava", "Root command for lava mod",
			(commandSender, args) -> {
				commandSender.sendMessage("Usage: /lava <subcommand> [options]");
				return false;
			});

		rootCommand.addSubCommand(SubCommands.env);
		rootCommand.addSubCommand(SubCommands.getHelpSubCommand(rootCommand));

	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		rootCommand.execute(commandSender, strings);
		return true;
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
