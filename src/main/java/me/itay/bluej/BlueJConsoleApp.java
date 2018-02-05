package me.itay.bluej;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.TeeOutputStream;

import com.mrcrayfish.device.api.app.Application;

import net.minecraft.nbt.NBTTagCompound;

public class BlueJConsoleApp extends Application {

	private static StringBuffer output = new StringBuffer();

	public static class BlueJConsoleOutput extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			output.append(b);
		}
	}

	private static final BlueJConsoleOutput STDOUT = new BlueJConsoleOutput();

	public static OutputStream getStdout() {
		return new TeeOutputStream(STDOUT, System.out);
	}

	public static String[] getOutput() {
		return output.toString().split("\n");
	}

	// @Todo make the console work

	@Override
	public void init() {

	}

	@Override
	public void load(NBTTagCompound tagCompound) {
	}

	@Override
	public void save(NBTTagCompound tagCompound) {
	}

}
