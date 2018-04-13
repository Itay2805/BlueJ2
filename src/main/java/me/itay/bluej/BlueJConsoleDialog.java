package me.itay.bluej;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.output.TeeOutputStream;

public class BlueJConsoleDialog extends Dialog {

	private StringBuffer output = new StringBuffer();

	@Override
	public void init()
	{
		super.init();

		int lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(output.toString(), getWidth() - 10).size();
		defaultLayout.height += (lines - 1) * 9;

		String[] ls = output.toString().replace("\r", "").split("\n");

		for(int i = 0; i < ls.length; i++){
            Text message = new Text(ls[i], 5, 5 + (i * 10), getWidth() - 10);
            message.setTextColor(Color.DARK_GRAY);
            this.addComponent(message);
        }

		Button buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
		buttonPositive.setSize(36, 16);
		buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			close();
		});
		this.addComponent(buttonPositive);
	}

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

	public void setOutput(String output){
		this.output.append(output);
	}

	public void setOutput(String[] output){
		for(String s : output){
			this.output.append(s);
		}
	}

}
