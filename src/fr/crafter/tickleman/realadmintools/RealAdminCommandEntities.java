package fr.crafter.tickleman.realadmintools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;

//######################################################################## RealAdminCommandEntities 
public class RealAdminCommandEntities implements Listener
{

	private static Map<String, Integer> limiter = new HashMap<String, Integer>();

	// ---------------------------------------------------------------------------------------
	// command
	@SuppressWarnings("unchecked")
	static void command(RealAdminToolsPlugin plugin, CommandSender sender,
			String[] args)
	{
		Class<? extends Entity> whatClass = null;
		String subCommand = args.length > 0 ? args[0].toLowerCase() : "";
		if (subCommand.equals("remove") && (args.length > 2)) {
			try {
				whatClass = (Class<? extends Entity>) Class.forName("Craft" + args[2]);
			} catch (ClassNotFoundException e) {
				whatClass = null;
			}
		}
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
							(what.equals("all") || ((whatClass != null) && whatClass.isInstance(entity)))
							&& !(entity instanceof CraftPlayer)
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
					if (subCommand.equals("limit") && (args.length > 3)) {
						limiter.put(args[2], new Integer(args[3]));
						plugin.getServer().getPluginManager().registerEvents(new RealAdminCommandEntities(), plugin);
					}
				}
				sender.sendMessage(
					world.getName() + " : " + world.getEntities().size()
					+ " / " + world.getLivingEntities().size() + " / "
					+ world.getPlayers().size()
					+ " > " + numerous_entities + " "
					+ (numerous_entities == 0 ? "" : numerous_entities_class.getName().split(".entity.Craft")[1].toLowerCase())
				);
			}
			if (subCommand.equals("remove")) {
				sender.sendMessage("removed " + removed_entities + " " + what);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			plugin.getLog().severe(
					"Could not save " + plugin.getDataFolder().getPath()
							+ "/entities.txt file");
		}
	}

}
