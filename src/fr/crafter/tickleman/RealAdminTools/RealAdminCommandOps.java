package fr.crafter.tickleman.RealAdminTools;

import java.io.BufferedReader;
import java.io.FileReader;

import org.bukkit.command.CommandSender;

//############################################################################# RealAdminCommandOps 
public class RealAdminCommandOps
{

	//--------------------------------------------------------------------------------------- command
	static void command(RealAdminToolsPlugin plugin, CommandSender sender, String[] args)
	{
		sender.sendMessage("operators list :");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("ops.txt"));
			String buffer;
			String bufferOut = "";
			while ((buffer = reader.readLine()) != null) {
				if (!buffer.equals("") && buffer.charAt(0) != '#') {
					if (!bufferOut.equals("")) {
						bufferOut += ", ";
					}
					bufferOut += buffer;
				}
			}
			sender.sendMessage("§a" + bufferOut);
			reader.close();
		} catch (Exception e) {
			plugin.log.severe("Could not read server.properties file");
			sender.sendMessage("§cCould not read server.properties file");
		}
	}

}
