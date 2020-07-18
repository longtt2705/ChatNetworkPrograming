package Client.GUI.Listener;

import Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Client.GUI
 *
 * @Created by Long - StudentID : 18120455
 * @Date 13/07/2020 - 4:47 PM
 * @Description
 **/
public class DoubleClickAdapter implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent evt) {
        JList list = (JList)evt.getSource();

        Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());

        if (r != null && r.contains(evt.getPoint()) && evt.getClickCount() == 2) {
            if (!Client.currentAccount.equals(list.getSelectedValue())) {

                Client.invokeGUI(Client.ViewLevel.CHAT_BOX, (String)list.getSelectedValue());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
