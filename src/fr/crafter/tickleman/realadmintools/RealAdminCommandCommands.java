package fr.crafter.tickleman.realadmintools;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

//######################################################################## RealAdminCommandCommands
public class RealAdminCommandCommands
{

	//--------------------------------------------------------------------------------------- command
	public static void command(RealAdminToolsPlugin plugin, CommandSender sender, String[] args)
	{
		Map<String, Set<String>> commandsMap = new HashMap<String, Set<String>>();
		File plugins = new File("plugins");
		for (File file : plugins.listFiles()) {
			if (
				(file.getName().length() > 4)
				&& file.getName().substring(file.getName().length() - 4).equalsIgnoreCase(".jar")
			) {
				try {
					JarFile jarFile = new JarFile(file.getPath());
					JarEntry pluginYml = jarFile.getJarEntry("plugin.yml");
					InputStream stream = jarFile.getInputStream(pluginYml);
					Yaml yaml = new Yaml(new SafeConstructor());
					@SuppressWarnings("unchecked")
					Map<String, Object> pluginsYml = (Map<String, Object>)yaml.load(stream);
					if (pluginsYml.containsKey("commands")) {
						Object commands = null;
						try {
							commands = pluginsYml.get("commands");
						} catch (Exception e) {
							sender.sendMessage(file.getName() + " commands are of wrong type");
						}
						if (commands != null) {
							@SuppressWarnings("unchecked")
							Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) commands;
							for (String command : map.keySet()) {
								Set<String> commandPlugins = commandsMap.get(command);
								if (commandPlugins == null) {
									commandPlugins = new HashSet<String>();
									commandsMap.put(command, commandPlugins);
								}
								commandPlugins.add(file.getName().substring(0, file.getName().length() - 4));
								if (map.get(command).containsKey("aliases")) {
									Object aliases = null;
									aliases = map.get(command).get("aliases");
									@SuppressWarnings("unchecked")
									List<String> aliasList = (List<String>)aliases;
									for (String alias : aliasList) {
										commandPlugins = commandsMap.get(alias.toString());
										if (commandPlugins == null) {
											commandPlugins = new HashSet<String>();
											commandsMap.put(alias.toString(), commandPlugins);
										}
										commandPlugins.add(file.getName().substring(0, file.getName().length() - 4));
									}
								}
							}
						}
					}
					jarFile.close();
				} catch (Exception e) {
					sender.sendMessage("could not open jar file " + file.getPath());
				}
			}
		}
		for (String command : commandsMap.keySet()) {
			if (
				((args.length >= 1) && args[0].equalsIgnoreCase(command))
				|| ((args.length == 0) && commandsMap.get(command).size() > 1)
			) {
				for (String pluginName : commandsMap.get(command)) {
					sender.sendMessage("command /" + command + " by " + pluginName);
				}
			}
		}
		for (String arg : args) {
			sender.sendMessage("arg " + arg);
		}
	}

}
