package fr.crafter.tickleman.RealAdminTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.bukkit.command.CommandSender;

//########################################################################## RealAdminCommandConfig
public class RealAdminCommandConfig
{

	//----------------------------------------------------------------------------- changeOptionValue
	static void changeOptionValue(
		RealAdminToolsPlugin plugin, CommandSender sender, String option, String value
	) {
		// read server.properties and change value if found
		boolean did_it = false;
		String bufferOut = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("server.properties"));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				if (!buffer.equals("") && buffer.charAt(0) != '#') {
					if (
						option.equals(buffer.substring(0, Math.min(buffer.length(), option.length())))
					) {
						sender.sendMessage("§a" + option + "=" + value);
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
			sender.sendMessage("§cCould not read server.properties file");
			bufferOut = "";
		}
		if (!bufferOut.equals("")) {
			if (did_it) {
				// save server.properties
				try {
					BufferedWriter writer = new BufferedWriter(
						new FileWriter("server.properties")
					);
					writer.write(bufferOut);
					writer.close();
					sender.sendMessage(
						"§3server.properties changed : use /reload command to load the new configuration"
					);
				} catch (Exception e) {
					plugin.log.severe("Could not write server.properties file");
					sender.sendMessage("§cCould not write server.properties file");
				}
			} else {
				sender.sendMessage("§cUnknown option " + option);
			}
		}
	}

	//--------------------------------------------------------------------------------------- command
	static void command(RealAdminToolsPlugin plugin, CommandSender sender, String[] args)
	{
		String option = args.length > 0 ? args[0].toLowerCase() : "";
		String value  = args.length > 1 ? args[1].toLowerCase() : ""; 
		if (!option.equals("") && !value.equals("")) {
			changeOptionValue(plugin, sender, option, value);
		} else if (!option.equals("") && value.equals("")) {
			displayOptionValue(plugin, sender, option);
		} else if (option.equals("") && value.equals("")) {
			displayOptionsValues(plugin, sender);
		}
	}

	//---------------------------------------------------------------------------- displayOptionValue
	static void displayOptionValue(RealAdminToolsPlugin plugin, CommandSender sender, String option)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader("server.properties"));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				if (!buffer.equals("") && buffer.charAt(0) != '#') {
					if (
						option.equals(buffer.substring(0, Math.min(buffer.length(), option.length())))
					) {
						sender.sendMessage("§a" + buffer);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			plugin.log.severe("Could not read server.properties file");
			sender.sendMessage("§cCould not read server.properties file");
		}
	}

	//-------------------------------------------------------------------------- displayOptionsValues
	static void displayOptionsValues(RealAdminToolsPlugin plugin, CommandSender sender)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader("server.properties"));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				if (!buffer.equals("") && buffer.charAt(0) != '#') {
					sender.sendMessage("§a" + buffer);
				}
			}
			reader.close();
		} catch (Exception e) {
			plugin.log.severe("Could not read server.properties file");
			sender.sendMessage("§cCould not read server.properties file");
		}
	}

}
