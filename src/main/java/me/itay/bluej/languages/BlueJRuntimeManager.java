package me.itay.bluej.languages;

import java.util.HashMap;

import me.itay.bluej.languages.js.JavaScriptRuntime;

public class BlueJRuntimeManager {
	
	private static HashMap<String, BlueJLanguage> runtimes = new HashMap<>();
	
	static {
		registerLanguage(new JavaScriptRuntime());
	}
	
	public static void registerLanguage(BlueJLanguage language) {
		runtimes.put(language.getName(), language);
	}
	
	public static BlueJLanguage getLanguageByExtension(String extension) {
		for(BlueJLanguage runtime : runtimes.values()) {
			for(String str : runtime.getExtensions()) {
				if(str.equalsIgnoreCase(extension)) {
					return runtime;
				}
			}
		}
		return null;
	}
	
	public static BlueJLanguage getLanguage(String name) {
		return runtimes.get(name);
	}
	
	public static HashMap<String, BlueJLanguage> getRuntimes() {
		return runtimes;
	}
	
	
	
}
