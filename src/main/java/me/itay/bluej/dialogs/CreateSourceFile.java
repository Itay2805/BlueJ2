package me.itay.bluej.dialogs;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class CreateSourceFile extends Dialog
{
    private static final int DIVIDE_WIDTH = 15;

    private String messageText = null;
    private String inputText = "";
    private String positiveText = "Okay";
    private String negativeText = "Cancel";

    private ResponseHandler<String> responseListener;

    private TextField textFieldInput;
    private Button buttonPositive;
    private Button buttonNegative;
    private CheckBox isStartupFile;

    public CreateSourceFile() {}

    public CreateSourceFile(String messageText)
    {
        this.messageText = messageText;
    }

    @Override
    public void init()
    {
        super.init();

        int offset = 0;

        if(messageText != null)
        {
            int lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(messageText, getWidth() - 10).size();
            defaultLayout.height += lines * 9 + 10;
            offset += lines * 9 + 5;
        }

        super.init();

        defaultLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
        });

        if(messageText != null)
        {
            Text message = new Text(messageText, 5, 5, getWidth() - 10);
            this.addComponent(message);
        }

        textFieldInput = new TextField(5, 5 + offset, getWidth() - 10);
        textFieldInput.setText(inputText);
        textFieldInput.setFocused(true);
        this.addComponent(textFieldInput);

        isStartupFile = new CheckBox("Startup File", 75, 5);
        this.addComponent(isStartupFile);

        int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
        buttonPositive = new Button(getWidth() - positiveWidth - DIVIDE_WIDTH, getHeight() - 20, positiveText);
        buttonPositive.setSize(positiveWidth + 10, 16);
        buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(!textFieldInput.getText().isEmpty())
            {
                boolean close = true;
                if(responseListener != null)
                {
                    System.out.println(textFieldInput.getText().trim());
                    close = responseListener.onResponse(true, textFieldInput.getText().trim());
                }
                if(close) close();
            }
        });
        this.addComponent(buttonPositive);

        int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
        buttonNegative = new Button(getWidth() - DIVIDE_WIDTH - positiveWidth - DIVIDE_WIDTH - negativeWidth + 1, getHeight() - 20, negativeText);
        buttonNegative.setSize(negativeWidth + 10, 16);
        buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
        this.addComponent(buttonNegative);
    }

    /**
     * Sets the initial text for the input text field
     * @param inputText
     */
    public void setInputText(@Nonnull String inputText)
    {
        this.inputText = inputText;
    }

    public CheckBox getIsStartupFile() {
        return isStartupFile;
    }

    /**
     * Gets the input text field. This will be null if has not been
     * @return
     */
    @Nullable
    public TextField getTextFieldInput()
    {
        return textFieldInput;
    }

    /**
     * Sets the positive button text
     * @param positiveText
     */
    public void setPositiveText(@Nonnull String positiveText)
    {
        this.positiveText = positiveText;
    }

    /**
     * Sets the negative button text
     *
     * @param negativeText
     */
    public void setNegativeText(@Nonnull String negativeText)
    {
        this.negativeText = negativeText;
    }

    /**
     * Sets the response handler. The handler is called when the positive
     * button is pressed and returns the value in the input text field. Returning
     * true in the handler indicates that the dialog should close.
     *
     * @param responseListener
     */
    public void setResponseHandler(ResponseHandler<String> responseListener)
    {
        this.responseListener = responseListener;
    }
}
