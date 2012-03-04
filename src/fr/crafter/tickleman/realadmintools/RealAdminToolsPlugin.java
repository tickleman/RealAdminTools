package fr.crafter.tickleman.realadmintools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import fr.crafter.tickleman.realplugin.RealPlugin;

//############################################################################ RealAdminToolsPlugin
public class RealAdminToolsPlugin extends RealPlugin implements Listener
{

	//-------------------------------------------------------------------------- RealTeleporterPlugin
	public RealAdminToolsPlugin()
	{
		super();
	}

	//------------------------------------------------------------------------------- onPlayerCommand
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		return RealAdminToolsCommands.onCommand(this, sender, cmd, commandLabel, args);
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		super.onDisable();
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		super.onEnable();
		getServer().getPluginManager().registerEvents(this, this);
	}

	//--------------------------------------------------------------------------------- onPlayerLogin
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		event.getPlayer().sendMessage("Welcome to you");
	}

}
