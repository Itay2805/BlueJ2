package me.itay.bluej.languages;

import com.mrcrayfish.device.api.app.interfaces.IHighlight;

import me.itay.bluej.project.Project;

public interface BlueJLanguage extends IHighlight {
	
	public String getName();
	public String[] getExtensions();
	public BlueJRunResponse run(Project project);
	
}
