package me.itay.bluej;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Text;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.output.TeeOutputStream;

public class BlueJConsoleDialog extends Dialog {

	private String displayText;
	private Button buttonPositive;
	private StringBuffer output = new StringBuffer();

	// @Todo make the console work
	public BlueJConsoleDialog(String message){
		this.displayText = message;
	}

	@Override
	public void init()
	{
		super.init();

		int lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(displayText, getWidth() - 10).size();
		defaultLayout.height += (lines - 1) * 9;

		if(displayText.contains("\n")){
			List<String> split = Arrays.asList(displayText.split("\n"));
			for(String s : split){
				Text message = new Text(s, 5, 5 * split.indexOf(s), this.getWidth());
				this.addComponent(message);
			}
		}
		Text message = new Text(displayText, 5, 5, getWidth() - 10);
		this.addComponent(message);

		buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
		buttonPositive.setSize(36, 16);
		buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> close());
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

}
