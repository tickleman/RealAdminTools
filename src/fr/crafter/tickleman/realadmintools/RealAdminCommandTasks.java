package fr.crafter.tickleman.realadmintools;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

//########################################################################### RealAdminCommandTasks
public class RealAdminCommandTasks
{

	// ---------------------------------------------------------------------------------------
	// command
	static void command(RealAdminToolsPlugin plugin, CommandSender sender, String[] args)
	{
		List<BukkitTask> listtask = plugin.getServer().getScheduler().getPendingTasks();
		HashMap<String, Integer> MapTask = new HashMap<String, Integer>();
		sender.sendMessage("Nbr de Shedule : " + listtask.size());
		// remplissage de MapTask avec les tasks du serveur + un conteur de double
		for (BukkitTask task : listtask) {
			String strt = "" + task.getOwner();
			if (MapTask.containsKey(strt)) {
				int count = MapTask.get(strt);
				count++;
				MapTask.remove(strt);
				MapTask.put(strt, count);
			} else {
				MapTask.put(strt, 1);
			}
		}
		// affichage MapTask
		for (String key : MapTask.keySet()) {
			Integer value = (Integer) MapTask.get(key);
			sender.sendMessage("Task : " + key + " : x" + value);
		}
	}

}
