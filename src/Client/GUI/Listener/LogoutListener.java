package Client.GUI.Listener;

import Client.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Client.GUI
 *
 * @Created by Long - StudentID : 18120455
 * @Date 12/07/2020 - 8:58 PM
 * @Description
 **/
public class LogoutListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Client.sendLogOutCommandToServer();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Client.currentAccount = null;

        Client.disposeAllChatBox();
        Client.invokeGUI(Client.ViewLevel.LOGIN, null);
    }
}
