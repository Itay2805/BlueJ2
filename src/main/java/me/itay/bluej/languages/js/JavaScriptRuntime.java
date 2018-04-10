package me.itay.bluej.languages.js;

import javax.script.*;

import me.itay.bluej.languages.BlueJLanguage;
import me.itay.bluej.languages.BlueJRunResponse;
import me.itay.bluej.languages.BlueJRuntimeManager;
import me.itay.bluej.project.Project;
import me.itay.bluej.resourcelocation.BlueJResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.io.StringWriter;

public class JavaScriptRuntime implements BlueJLanguage {

	@Override
	public String[] getExtensions() {
		return new String[] { "js" };
	}

	@Override
	public String getName() {
		return "javascript";
	}

	@Override
	public BlueJRunResponse run(Project project) {
		BlueJRunResponse resp = new BlueJRunResponse();
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("javascript");
        try {
//				engine.eval("function require(name) {"
//						+ "native.loadModuleFromProject(name);"
//						+ "}");
            Compilable compilable = (Compilable)engine;
            String source = project.getStartupFile().getSource();
            System.out.println(source);
            CompiledScript compiledScript = compilable.compile(source);
            StringWriter sw = new StringWriter();
            ScriptContext context = engine.getContext();
            context.setWriter(sw);
            String ret = (String)compiledScript.eval();
            resp.setOutput(sw.toString().replace("\n", ""));
            System.out.println("Ret: " + ret);
            System.out.println("Sw: " + sw.toString());
        } catch (ScriptException | NullPointerException e) {
            e.printStackTrace();
        }
        return resp;
	}

	@Override
	public TextFormatting[] getKeywordFormatting(String text) {
		switch(text) {
			case "break":
			case "case":
			case "catch":
			case "class":
			case "const":
			case "continue":
			case "debugger":
			case "default":
			case "delete":
			case "do":
			case "else":
			case "export":
			case "extends":
			case "finally":
			case "for":
			case "function":
			case "if":
			case "import":
			case "in":
			case "instanceof":
			case "new":
			case "return":
			case "super":
			case "switch":
			case "this":
			case "throw":
			case "try":
			case "typeof":
			case "var":
			case "void":
			case "while":
			case "with":
			case "enum":
			case "implements":
			case "interface":
			case "let":
			case "package":
			case "private":
			case "protected":
			case "public":
			case "static":
			case "abstract":
			case "boolean":
			case "byte":
			case "char":
			case "double":
			case "final":
			case "float":
			case "goto":
			case "int":
			case "long":
			case "native":
			case "short":
			case "synchronized":
			case "throws":
			case "transient":
			case "volatile":
			case "null":
			case "true":
			case "false":
				return new TextFormatting[] {
					TextFormatting.BOLD,
					TextFormatting.BLUE
				};
			case "undefined":
			case "Nan":
			case "Number":
			case "Infinity":
			case "String":
			case "Date":
			case "eval":
			case "isFinite":
			case "isNaN":
				return new TextFormatting[] {
						TextFormatting.RESET,
						TextFormatting.DARK_PURPLE
					};
			case "decodeURI":
			case "decodeURIComponent":
			case "encodeURI":
			case "encodeURIComponent":
			case "escape":
			case "unescape":
			case "parseFloat":
			case "parseInt":
				return new TextFormatting[] {
						TextFormatting.BOLD,
						TextFormatting.DARK_RED
					};
		}

//		That looked horrible x-x
//		if(text.matches("\\(|\\)|\\{|\\}|\\:|\\,")) {
//			return new TextFormatting[] {
//					TextFormatting.BOLD,
//					TextFormatting.DARK_BLUE
//				};
//		}

		if(text.startsWith("\"")) {
			return new TextFormatting[] {
					TextFormatting.RESET,
					TextFormatting.GRAY
				};
		}

		if(text.matches("(0x|0b|0B|0X)?[0-9]+")) {
			return new TextFormatting[] {
					TextFormatting.RESET,
					TextFormatting.GOLD
				};
		}

		return new TextFormatting[] {
				TextFormatting.RESET,
				TextFormatting.WHITE
			};
	}

}
