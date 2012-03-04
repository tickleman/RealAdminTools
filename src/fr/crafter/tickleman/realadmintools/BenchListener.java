package fr.crafter.tickleman.realadmintools;

import org.bukkit.event.Listener;

public class BenchListener implements Listener
{

	//--------------------------------------------------------------------------------------- onEvent
	public void onEvent(BenchListenerEvent event)
	{
		/*
		System.out.println(
			"call BenchListenerEvent "
			+ event.getStep() + " "
			+ event.getCallerPlugin().getName() + "'s "
			+ event.getListener().getClass().getSimpleName()
			+ " for event " + event.getCalledEvent().getClass().getSimpleName()
			+ " duration = " + event.getDuration()
			+ " cumulated = " + event.getTotalDuration()
		);
		*/
	}

}
