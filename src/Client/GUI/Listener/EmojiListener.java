package Client.GUI.Listener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Client.GUI.Listener
 *
 * @Created by Long - StudentID : 18120455
 * @Date 19/07/2020 - 10:39 AM
 * @Description
 **/
public class EmojiListener implements ActionListener {

    private final JTextField textField;
    private final String emoji;

    public EmojiListener(JTextField textField, String emoji) {
        this.textField = textField;
        this.emoji = emoji;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        textField.setText(textField.getText() + emoji);
        System.out.println(emoji);

    }
}
