package fr.crafter.tickleman.RealAdminTools;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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
				if (command.equals("entities")) {
					String subCommand = args.length > 0 ? args[0].toLowerCase() : "";
					try {
						BufferedWriter writer = new BufferedWriter(
							new FileWriter("plugins/" + plugin.name + "/entities.txt")
						);
						writer.write("#class,id,itemTypeId,world,x,y,z\n");
				    for (World world : plugin.getServer().getWorlds()) {
				    	sender.sendMessage(world.getName() + " :");
				    	sender.sendMessage("- " + world.getEntities().size() + " entities");
				    	sender.sendMessage("- " + world.getLivingEntities().size() + " living entities");
				    	sender.sendMessage("- " + world.getPlayers().size() + " players");
				    	for (Entity entity : world.getEntities()) {
				    		Item item = (entity instanceof Item ? (Item)entity : null);
				    		writer.write(
				    			entity.getClass().getName()
				    			+ "," + entity.getEntityId()
				    			+ "," + (item != null ? item.getItemStack().getTypeId() : "")
				    			+ "," + world.getName()
				    			+ "," + Math.round(Math.floor(entity.getLocation().getX()))
				    			+ "," + Math.round(Math.floor(entity.getLocation().getY()))
				    			+ "," + Math.round(Math.floor(entity.getLocation().getZ()))
				    			+ "\n"
				    		);
				    		if (subCommand.equals("remove")) {
				    			String what = args.length > 1 ? args[1].toLowerCase() : "";
				    			if (
				    				what.equals(entity.getClass().getName().split(".entity.Craft")[1].toLowerCase())
				    				|| what.equals("all")
				    			) {
				    				String id = args.length > 2 ? args[2].toLowerCase() : "";
				    				String entityTypeId = (item != null ? "" + item.getItemStack().getTypeId() : "");
				    				if (
				    					id.equals(entityTypeId)
				    					|| id.equals("all")
				    				) {
				    					entity.remove();
				    				}
				    			}
				    		}
				    	}
				    }
						writer.flush();
						writer.close();
					} catch (Exception e) {
						plugin.log.severe("Could not save plugins/" + plugin.name + "/entities.txt file");
					}
				}
			}
		}
		return false;
	}

}
