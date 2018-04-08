package me.itay.bluej;

import java.io.IOException;
import java.io.OutputStream;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import org.apache.commons.io.output.TeeOutputStream;

import com.mrcrayfish.device.api.app.Application;

import net.minecraft.nbt.NBTTagCompound;

public class BlueJConsoleDialog extends Dialog {

	private Text displayText;

	private StringBuffer output = new StringBuffer();

	private final OutputStream STDOUT = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			output.append(b);
		}
	};

	public OutputStream getStdout() {
		return new TeeOutputStream(STDOUT, System.out);
	}

	public String[] getOutput() {
		return output.toString().split("\n");
	}

	// @Todo make the console work

	@Override
	public void init() {
		displayText = new Text(this.output.toString(), 5, 5, 20);
		this.addComponent(displayText);
	}

}
