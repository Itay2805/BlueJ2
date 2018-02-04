package me.itay.bluej.project;

import com.mrcrayfish.device.api.io.File;

import me.itay.bluej.resourcelocation.BlueJResolvedResource.BlueJAfter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class SourceFile {
	
	private File file;
	
	public SourceFile(File f) {
		this.file = f;
	}
	
	public void prepare(BlueJAfter after) {
		NBTTagCompound data = file.getData();
		data.setString("content_type", Project.MIME_SRC_FILE);
		file.setData(data, (t, ok) -> after.handle());
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return file.getName();
	}
	
	public String getSource() {
		NBTTagCompound data = file.getData();
		if(data.hasKey("source", NBT.TAG_STRING)) {
			return data.getString("source");
		}
		return "";
	}
	
	public void setSource(String source, BlueJAfter after) {
		NBTTagCompound data = file.getData();
		data.setString("source", source);
		file.setData(data, (t, ok) -> {
			after.handle();
		});
	}
	
}
