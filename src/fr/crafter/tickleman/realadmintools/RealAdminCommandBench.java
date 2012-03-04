package fr.crafter.tickleman.realadmintools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

public class RealAdminCommandBench
{

	private static Map<HandlerList, RegisteredListener[]> backupListeners
		= new HashMap<HandlerList, RegisteredListener[]>();

	//--------------------------------------------------------------------------------------- command
	static void command(RealAdminToolsPlugin plugin, CommandSender sender, String[] args)
	{
		String[] a = new String[4];
		a[0] = args.length > 0 ? args[0] : "";
		a[1] = args.length > 1 ? args[1] : "";
		a[2] = args.length > 2 ? args[2] : "";
		a[3] = args.length > 3 ? args[3] : "";
		if (a[0].equals("start")) {
			if (!isStarted()) {
				start(plugin);
				sender.sendMessage("Bench started !");
			} else {
				sender.sendMessage("Bench is already running.");
			}
		}
		if (a[0].equals("stop")) {
			if (isStarted()) {
				stop(plugin);
				sender.sendMessage("Bench stopped !");
			} else {
				sender.sendMessage("Bench is not running.");
			}
		}
		if (a[0].equals("top10")) {
			Map<Long, String> durations = BenchListenerEvent.getSortedDurations();
			int count = durations.size();
			for (Long duration : durations.keySet()) {
				if (count <= 10) {
					sender.sendMessage(durations.get(duration) + " : " + (duration / 1000000) + "ms");
				}
				count--;
			}
		}
		if (a[0].equals("top20")) {
			Map<Long, String> durations = BenchListenerEvent.getSortedDurations();
			int count = durations.size();
			for (Long duration : durations.keySet()) {
				if (count <= 20) {
					sender.sendMessage(durations.get(duration) + " : " + (duration / 1000000) + "ms");
				}
				count--;
			}
		}
	}

	//------------------------------------------------------------------------------------- isStarted
	private static boolean isStarted()
	{
		return !backupListeners.isEmpty();
	}

	//------------------------------------------------------------- setHandlerListRegisteredListeners
	private static void setHandlerListRegisteredListeners(
		HandlerList handlerList, RegisteredListener[] listeners
	) {
		Field handlerListHandlersField = null;
		try {
			handlerListHandlersField = handlerList.getClass().getDeclaredField("handlers");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		boolean isAccessible = handlerListHandlersField.isAccessible();
		if (!isAccessible) handlerListHandlersField.setAccessible(true);
		try {
			handlerListHandlersField.set(handlerList, listeners);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (!isAccessible) handlerListHandlersField.setAccessible(false);
	}

	//----------------------------------------------------------------------------------------- start
	@SuppressWarnings("unchecked")
	static void start(final JavaPlugin plugin)
	{
		final BenchListener benchListener = new BenchListener();
		// list all bukkit classes from package org.bukkit.event >
		Enumeration<? extends ZipEntry> entries = null;
		try {
			entries = new ZipFile("craftbukkit.jar").entries();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getName().startsWith("org/bukkit/event") && entry.getName().endsWith(".class")) {
				// list active listeners for each bukkit event class
				RegisteredListener[] listeners = null;
				Class<? extends Event> tmpEventClass = null;
				try {
					tmpEventClass = (Class<? extends Event>) Class
						.forName(entry.getName().replace("/", ".").replace(".class", ""));
				} catch (ClassNotFoundException e) {
					System.out.println(entry.getName());
					e.printStackTrace();
				}
				final Class<? extends Event> eventClass = tmpEventClass;
				HandlerList handlerList = null;
				Field handlersField = null;
				try {
					handlersField = eventClass.getDeclaredField("handlers");
				} catch (SecurityException e) {
				} catch (NoSuchFieldException e) {
				}
				if (handlersField != null) {
					boolean isAccessible = handlersField.isAccessible();
					if (!isAccessible) handlersField.setAccessible(true);
					try {
						handlerList = (HandlerList) handlersField.get(null);
					} catch (NullPointerException e) {
						// some classes in the event package (HandlerList) have a non-static handlers property
						// it does not matter. do nothing with them
					} catch (IllegalArgumentException e) {
						System.out.println(eventClass.getName());
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						System.out.println(eventClass.getName());
						e.printStackTrace();
					}
					if (!isAccessible) handlersField.setAccessible(false);
					if (handlerList != null) {
						listeners = handlerList.getRegisteredListeners();
					}
				}
				// debug list of listeners >>>
				/*
				if (listeners != null) {
					for (RegisteredListener listener : listeners) {
						if (listener.getPlugin().isEnabled()) {
							System.out.println(
								"# a listener " + listener.getPlugin().getName() + " : "
								+ eventClass.getSimpleName()
							);
						}
					}
				}
				*/
				// assign new listeners list
				if ((listeners != null) && (listeners.length > 0)) {
					// create new listeners list
					RegisteredListener[] newListeners = new RegisteredListener[listeners.length * 3];
					int di = 0;
					for (final RegisteredListener listener : listeners) {
						//System.out.println("+ register before " + listener.getPlugin().getName() + " > " + eventClass.getSimpleName());
						EventExecutor myExecutor = new EventExecutor() {
							public void execute(Listener eventListener, Event event) {
								if (!eventClass.isAssignableFrom(event.getClass())) {
									return;
								}
								BenchListenerEvent benchListenerEvent = new BenchListenerEvent(
									listener.getPlugin(), listener.getListener(), BenchListenerEvent.Called.BEFORE, event
								);
								benchListener.onEvent(benchListenerEvent);
							}
						};
						newListeners[di++] = new RegisteredListener(
							benchListener, myExecutor, EventPriority.NORMAL, plugin, true
						);
						//System.out.println("+ register origin " + listener.getPlugin().getName() + " > " + eventClass.getSimpleName());
						newListeners[di++] = listener;
						//System.out.println("+ register after " + listener.getPlugin().getName() + " > " + eventClass.getSimpleName());
						myExecutor = new EventExecutor() {
							public void execute(Listener eventListener, Event event) {
								if (!eventClass.isAssignableFrom(event.getClass())) {
									return;
								}
								BenchListenerEvent benchListenerEvent = new BenchListenerEvent(
									listener.getPlugin(), listener.getListener(), BenchListenerEvent.Called.AFTER, event
								);
								benchListener.onEvent(benchListenerEvent);
							}
						};
						newListeners[di++] = new RegisteredListener(
							benchListener, myExecutor, EventPriority.NORMAL, plugin, true
						);
					}
					// set event handler registered listeners list = newListeners
					//System.out.println("  | backup " + handlerList.hashCode() + " : " + listeners.length);
					backupListeners.put(handlerList, listeners);
					setHandlerListRegisteredListeners(handlerList, newListeners);
				}
			}
		}
	}

	//------------------------------------------------------------------------------------------ stop
	static void stop(JavaPlugin plugin)
	{
		for (HandlerList handlerList : backupListeners.keySet()) {
			RegisteredListener[] listeners = backupListeners.get(handlerList);
			//System.out.println("  | restore " + handlerList.hashCode() + " : " + listeners.length);
			setHandlerListRegisteredListeners(handlerList, listeners);
		}
		backupListeners.clear();
	}

}
