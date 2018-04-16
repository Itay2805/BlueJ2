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
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return file.getName();
	}

	public NBTTagCompound getData(){
	    return this.file.getData();
    }
	
	public String getSource() {
		NBTTagCompound data = file.getData();
		if(data.hasKey("source", NBT.TAG_STRING)) {
			return data.getString("source");
		}
		return "";
	}
	
	public void setSource(String source) {
		NBTTagCompound data = file.getData();
		data.setString("source", source);
		file.setData(data);
	}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SourceFile && this.getFile().getPath().equals(((SourceFile) obj).file.getPath());
    }
}
