package fr.crafter.tickleman.RealAdminTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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
	    			String what = args.length > 1 ? args[1].toLowerCase() : "";
						int removed_entities = 0;
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
				    					removed_entities ++;
				    				}
				    			}
				    		}
				    	}
				    }
				    if (subCommand.equals("remove")) {
				    	sender.sendMessage("removed " + removed_entities + " " + what);
				    }
						writer.flush();
						writer.close();
					} catch (Exception e) {
						plugin.log.severe("Could not save plugins/" + plugin.name + "/entities.txt file");
					}
					return true;
				}
				if (command.equals("config")) {
					String option = args.length > 0 ? args[0].toLowerCase() : "";
					String value  = args.length > 1 ? args[1].toLowerCase() : ""; 
					if (!option.equals("") && !value.equals("")) {
						boolean did_it = false;
						String bufferOut = "";
						try {
							BufferedReader reader = new BufferedReader(
								new FileReader("server.properties")
							);
							String buffer;
							while ((buffer = reader.readLine()) != null) {
								if (!buffer.equals("") && buffer.charAt(0) != '#') {
									if (
										option.equals(buffer.substring(0, Math.min(buffer.length(), option.length())))
									) {
										sender.sendMessage(option + "=" + value);
										buffer = option + "=" + value;
										did_it = true;
									}
								}
								if (!bufferOut.equals("")) {
									bufferOut = bufferOut + "\n";
								}
								bufferOut = bufferOut + buffer;
							}
							reader.close();
						} catch (Exception e) {
							plugin.log.severe("Could not read server.properties file");
							sender.sendMessage("Could not read server.properties file");
							bufferOut = "";
						}
						if (!bufferOut.equals("")) {
							if (did_it) {
								try {
									BufferedWriter writer = new BufferedWriter(
										new FileWriter("server.properties")
									);
									writer.write(bufferOut);
									writer.close();
									sender.sendMessage("server.properties changed : use /reload command to load the new configuration");
								} catch (Exception e) {
									plugin.log.severe("Could not write server.properties file");
									sender.sendMessage("Could not write server.properties file");
								}
							} else {
								sender.sendMessage("Unknown option " + option);
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

}
