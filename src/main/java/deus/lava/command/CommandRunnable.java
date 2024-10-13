package deus.lava.command;

import net.minecraft.core.net.command.CommandSender;

@FunctionalInterface
public interface CommandRunnable {

	boolean run(CommandSender commandSender, String[] args);
}
