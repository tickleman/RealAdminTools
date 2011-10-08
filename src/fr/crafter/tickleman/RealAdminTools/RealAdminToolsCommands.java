package fr.crafter.tickleman.RealAdminTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

//########################################################################## RealAdminToolsCommands 
public class RealAdminToolsCommands
{

	//------------------------------------------------------------------------------------- onCommand
	static public boolean onCommand(
		RealAdminToolsPlugin plugin,
		CommandSender sender, Command cmd, String commandLabel, String[] args
	) {
		if ((sender instanceof Player) || (sender instanceof ConsoleCommandSender)) {
			if (sender instanceof ConsoleCommandSender || ((Player)sender).isOp()) {
				String command = cmd.getName().toLowerCase();
				if (command.equals("config")) {
					RealAdminCommandConfig.command(plugin, sender, args);
					return true;
				}
				if (command.equals("entities")) {
					RealAdminCommandEntities.command(plugin, sender, args);
					return true;
				}
				if (command.equals("ops")) {
					RealAdminCommandOps.command(plugin, sender, args);
					return true;
				}
				if (command.equals("tasks")) {
					RealAdminCommandTasks.command(plugin, sender, args);
				}
			}
		}
		return false;
	}

}
