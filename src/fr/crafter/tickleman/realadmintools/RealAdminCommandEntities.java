package fr.crafter.tickleman.realadmintools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

//######################################################################## RealAdminCommandEntities 
public class RealAdminCommandEntities
{

	// ---------------------------------------------------------------------------------------
	// command
	static void command(RealAdminToolsPlugin plugin, CommandSender sender,
			String[] args)
	{
		String subCommand = args.length > 0 ? args[0].toLowerCase() : "";
		try {
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(plugin.getDataFolder().getPath() + File.separator + "entities.txt")
			);
			writer.write("#class,id,itemTypeId,world,x,y,z\n");
			String what = args.length > 1 ? args[1].toLowerCase() : "";
			int removed_entities = 0;
			sender.sendMessage("world : entities / living / players > numerous entities");
			for (World world : plugin.getServer().getWorlds()) {
				Map<Class<? extends Entity>, Integer> entities_counter
					= new HashMap<Class<? extends Entity>, Integer>();
				int numerous_entities = 0;
				Class<? extends Entity> numerous_entities_class = null;
				for (Entity entity : world.getEntities()) {
					Integer cnt = entities_counter.get(entity.getClass());
					if (cnt == null) {
						cnt = 0;
					}
					entities_counter.put(entity.getClass(), cnt + 1);
					if (cnt > numerous_entities) {
						numerous_entities = cnt;
						numerous_entities_class = entity.getClass();
					}
					Item item = (entity instanceof Item ? (Item) entity : null);
					writer.write(entity.getClass().getName() + "," + entity.getEntityId()
							+ "," + (item != null ? item.getItemStack().getTypeId() : "")
							+ "," + world.getName() + ","
							+ Math.round(Math.floor(entity.getLocation().getX())) + ","
							+ Math.round(Math.floor(entity.getLocation().getY())) + ","
							+ Math.round(Math.floor(entity.getLocation().getZ())) + "\n");
					if (subCommand.equals("remove")) {
						if (
							what.equals(entity.getClass().getName().split(".entity.Craft")[1].toLowerCase())
							|| what.equals("all")
						) {
							String id = args.length > 2 ? args[2].toLowerCase() : "";
							String entityTypeId = (item != null ? ""
									+ item.getItemStack().getTypeId() : "");
							if (id.equals(entityTypeId) || id.equals("all")) {
								entity.remove();
								removed_entities++;
							}
						}
					}
				}
				sender.sendMessage(
					world.getName() + " : " + world.getEntities().size()
					+ " / " + world.getLivingEntities().size() + " / "
					+ world.getPlayers().size()
					+ " > " + numerous_entities + " "
					+ numerous_entities_class.getName().split(".entity.Craft")[1].toLowerCase()
				);
			}
			if (subCommand.equals("remove")) {
				sender.sendMessage("removed " + removed_entities + " " + what);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			plugin.getLog().severe(
					"Could not save " + plugin.getDataFolder().getPath()
							+ "/entities.txt file");
		}
	}

}
