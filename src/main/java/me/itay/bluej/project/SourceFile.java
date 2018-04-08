package me.itay.bluej.project;

import com.mrcrayfish.device.api.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.Objects;

public class SourceFile {
	
	private File file;
	
	public SourceFile(File f) {
		this.file = f;
	}
	
	public void prepare(Runnable runnable) {
		NBTTagCompound data = file.getData();
		Objects.requireNonNull(data).setString("content_type", Project.MIME_SRC_FILE);
		file.setData(data, (resp, ok) -> {
			if(ok) {
				runnable.run();	
			}else {
				// @Todo proper error handling
				System.err.println("[ERROR] error preparing source file: " + Objects.requireNonNull(resp).getMessage());
			}
		});
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return file.getName();
	}
	
	public String getSource() {
		NBTTagCompound data = file.getData();
		if(Objects.requireNonNull(data).hasKey("source", NBT.TAG_STRING)) {
			return data.getString("source");
		}
		return "";
	}
	
	public void setSource(String source, Runnable runnable) {
		NBTTagCompound data = file.getData();
		Objects.requireNonNull(data).setString("source", source);
		file.setData(data, (resp, ok) -> {
			if(ok) {
				runnable.run();	
			}else {
				// @Todo proper error handling
				System.err.println("[ERROR] error setting source in source file: " + Objects.requireNonNull(resp).getMessage());
			}
		});
	}
	
}
