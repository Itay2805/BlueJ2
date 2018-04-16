package me.itay.bluej.dialogs;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.system.component.FileBrowser;

import me.itay.bluej.resourcelocation.BlueJResourceLocation;

/**
 * 
 * Inspired by SaveFile
 * 
 */
public class SelectFolder extends Dialog {
	
	private final Application app;

	private String positiveText = "Select";
	private String negativeText = "Cancel";
	
	private Layout main;
	private FileBrowser browser;
	private Button buttonPositive;
	private Button buttonNegative;
	
	public ResponseHandler<Folder> responseHandler;
	
	private String path = FileSystem.DIR_HOME;
	
	public SelectFolder(Application app) {
		this.app = app;
		this.setTitle("Select Folder");
	}
	
	@Override
	public void init() {
		super.init();
		main = new Layout(211, 145);
		
		browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
//		browser.setFilter(File::isFolder);
		browser.openFolder(path);
		main.addComponent(browser);
		
		buttonPositive = new Button(169, 125, positiveText);
		buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
			if(mouseButton == 0) {
				if(browser.getSelectedFile() == null || !browser.getSelectedFile().isFolder()) {
					// @Hack this is to get the currentFolder, if we have no selected file
					try {
						Field f = browser.getClass().getDeclaredField("currentFolder");
						f.setAccessible(true);
						Folder folder = (Folder) f.get(browser);
						responseHandler.onResponse(true, folder);						
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						responseHandler.onResponse(false, null);						
					}
				}else {
					responseHandler.onResponse(true, (Folder) browser.getSelectedFile());
				}
				System.out.println(browser.getSelectedFile().getName());
				close();
			}
		});
		main.addComponent(buttonPositive);
		
		buttonNegative = new Button(123, 125, negativeText);
		buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
		main.addComponent(buttonNegative);
		
		this.setLayout(main);
	}
	
	public void setPositiveText(@Nonnull String positiveText) {
		this.positiveText = positiveText;
	}
	
	public void setNegativeText(@Nonnull String negativeText) {
		this.negativeText = negativeText;
	}
	
	public void setResponseHandler(ResponseHandler<Folder> responseHandler) {
		this.responseHandler = responseHandler;
	}
	
	public void setFolder(String path) {
		this.path = path;
	}
	
}
