package me.itay.bluej.project;

import com.mrcrayfish.device.api.io.File;

public class SourceFile {
	
	private File file;
	
	public SourceFile(File f) {
		this.file = f;
	}
	
	public File getFile() {
		return file;
	}
	
}
