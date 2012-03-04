package fr.crafter.tickleman.realadmintools;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BenchListenerEvent extends Event
{

	public enum Called {BEFORE, AFTER};

	private Event calledEvent;

	private Plugin callerPlugin;

	private Long duration;

	private static Map<String, Long> durations = new HashMap<String, Long>();

	private static final HandlerList handlers = new HandlerList();

	private Listener listener;

	private Called step;

	private static Map<String, Long> startTimes = new HashMap<String, Long>();

	//---------------------------------------------------------------------------- BenchListenerEvent
	public BenchListenerEvent(
		Plugin callerPlugin, Listener listener,
		Called step, Event calledEvent
	) {
		this.calledEvent = calledEvent;
		this.callerPlugin = callerPlugin;
		this.listener = listener;
		this.step = step;
		if (step.equals(Called.BEFORE)) {
			startTimer();
		} else {
			stopTimer();
		}
	}

	//----------------------------------------------------------------------------------- getBenchKey
	private String getBenchKey()
	{
		return callerPlugin.getName() + ":" + calledEvent.getEventName();
	}

	//-------------------------------------------------------------------------------- getCalledEvent
	public Event getCalledEvent()
	{
		return calledEvent;
	}

	//------------------------------------------------------------------------------- getCallerPlugin
	public Plugin getCallerPlugin()
	{
		return callerPlugin;
	}

	//----------------------------------------------------------------------------------- getDuration
	public long getDuration()
	{
		return duration;
	}

	//-------------------------------------------------------------------------------- getHandlerList
  public static HandlerList getHandlerList()
  {
      return handlers;
  }
 
	//----------------------------------------------------------------------------------- getHandlers
	@Override
  public HandlerList getHandlers()
	{
      return handlers;
  }

	//----------------------------------------------------------------------------------- getListener
	public Listener getListener()
	{
		return listener;
	}

	//---------------------------------------------------------------------------- getSortedDurations
	public static Map<Long, String> getSortedDurations()
	{
		Map<Long, String> sortedDurations = new TreeMap<Long, String>();
		for (String key : durations.keySet()) {
			Long duration = durations.get(key);
			while (sortedDurations.get(duration) != null) duration++;
			sortedDurations.put(duration, key);
		}
		return sortedDurations;
	}

	//--------------------------------------------------------------------------------------- getStep
	public Called getStep()
	{
		return step;
	}

	//------------------------------------------------------------------------------ getTotalDuration
	public long getTotalDuration()
	{
		Long totalDuration = durations.get(getBenchKey());
		return (totalDuration == null) ? 0L : totalDuration;
	}

	//------------------------------------------------------------------------------------ startTimer
	private void startTimer()
	{
		startTimes.put(getBenchKey(), System.nanoTime());
		duration = 0L;
	}

	//------------------------------------------------------------------------------------- stopTimer
	private void stopTimer()
	{
		String key = getBenchKey();
		Long totalDuration = durations.get(key);
		if (totalDuration == null) totalDuration = 0L;
		Long startTime = startTimes.get(key);
		if (startTime == null) duration = 0L;
		else duration = System.nanoTime() - startTime;
		durations.put(key, totalDuration + duration);
	}

}
