package Client.GUI.Listener;

import Client.GUI.ChatBoxGUI;
import Client.SendFileThread;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

/**
 * Client.GUI.Listener
 *
 * @Created by Long - StudentID : 18120455
 * @Date 15/07/2020 - 10:25 AM
 * @Description
 **/
public class SendFileListener implements ActionListener {

    private final JProgressBar bar;
    private final String desName;
    private final ChatBoxGUI box;

    public SendFileListener(JProgressBar bar, String desName, ChatBoxGUI box) {
        this.bar = bar;
        this.desName = desName;
        this.box = box;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // create an object of JFileChooser class
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        j.setAcceptAllFileFilterUsed(true);
        j.setDialogTitle("Select a file");

        // invoke the showsOpenDialog function to show the save dialog
        int res = j.showOpenDialog(null);

        // if the user selects a file
        if (res == JFileChooser.APPROVE_OPTION) {

            String path = j.getSelectedFile().getAbsolutePath();
            new SendFileThread(Paths.get(path), bar, desName, box).start();
        }
    }
}
