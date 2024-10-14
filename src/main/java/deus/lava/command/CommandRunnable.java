package deus.lava.command;

import net.minecraft.core.net.command.CommandSender;

@FunctionalInterface
public interface CommandRunnable {

	Object run(CommandSender commandSender, String[] args);
}
