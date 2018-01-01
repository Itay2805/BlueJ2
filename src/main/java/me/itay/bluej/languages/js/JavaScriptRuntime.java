package me.itay.bluej.languages.js;

import java.util.HashMap;

import com.mrcrayfish.device.api.app.interfaces.IHighlight;

import me.itay.bluej.languages.BlueJLanguage;
import me.itay.bluej.languages.BlueJRunResponse;
import me.itay.bluej.project.Project;
import net.minecraft.util.text.TextFormatting;

public class JavaScriptRuntime implements BlueJLanguage {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public BlueJRunResponse run(Project project) {
		return null;
	}
	
	@Override
	public TextFormatting[] getKeywordFormatting(String text) {
		/// Super basic, but will work for now :D
		
		TextFormatting[] formatting = new TextFormatting[1];
		switch(text) {
			case "static":
			case "private":
			case "protected":
			case "public":
			case "constructor":
			case "extends":
			case "implements":
			case "enum":
			case "interface":
			case "class":
			case "arguments":
			case "const":
			case "debugger":
			case "delete":
			case "false":
			case "function":
			case "in":
			case "instanceof":
			case "let":
			case "new":
			case "null":
			case "super":
			case "this":
			case "true":
			case "typeof":
			case "var":
			case "void":
				formatting[0] = TextFormatting.BLUE;
				break;
			case "for":
			case "await":
			case "break":
			case "case":
			case "catch":
			case "continue":
			case "default":
			case "do":
			case "else":
			case "export":
			case "finally":
			case "goto":
			case "if":
			case "import":
			case "return":
			case "switch":
			case "throw":
			case "try":
			case "while":
			case "with":
			case "yeild":
				formatting[0] = TextFormatting.LIGHT_PURPLE;
				break;
		}
		
		if(text.length() > 3 && formatting[0] == null) {
			formatting[0] = TextFormatting.AQUA;			
		}else if(formatting[0] == null) {
			formatting[0] = TextFormatting.WHITE;
		}
		
		return formatting;
	}
	
}
