package Client;

import Client.GUI.ChatBoxGUI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 15/07/2020 - 11:02 AM
 * @Description
 **/

public class SendFileThread extends Thread {

    private final Path path;
    private final JProgressBar bar;
    private final String desName;
    private final ChatBoxGUI box;
    private Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    WindowAdapter windowAdapter;
    Path fileName;

    public SendFileThread(Path path, JProgressBar bar, String desName, ChatBoxGUI box) {
        this.path = path;
        this.bar = bar;
        this.desName = desName;
        this.box = box;
    }

    @Override
    public void run() {

        try {

            int count;
            int connectCode;
            File file = path.toFile();
            long fileLength = file.length();
            long currentRead = 0;
            fileName = path.getFileName();
            byte[] buffer = new byte[512]; // or 4096, or more

            socket = new Socket("localhost", Client.PORT);

            // in/out stream
            FileInputStream fileInputStream = new FileInputStream(file);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            // --- Send establish command

            outputStream.writeUTF(Client.ESTABLISH_SEND_FILE + "/" + Client.currentAccount + "/" + desName + "/" + fileName);
            outputStream.writeLong(fileLength);
            bar.setIndeterminate(true);

            // wait for result of ESTABLISH_SEND_FILE
            do {
                String rec = inputStream.readUTF();
                String command = rec.split("/")[0];
                String result = rec.split("/")[1];


                if (command.equals(Client.ESTABLISH_SEND_FILE) && result.equals(HandleResponseThread.SUCCESSFUL)) {
                    connectCode = Integer.parseInt(rec.split("/")[2]);
                    bar.setIndeterminate(false);
                    break;
                }
                else {
                    JOptionPane.showMessageDialog(null, "Không nhận được phản hồi từ người nhận!");
                    bar.setIndeterminate(false);
                    socket.close();
                    return;
                }

            } while (true);

            // add windows adapter
            windowAdapter = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                    box.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    if (JOptionPane.showConfirmDialog(box,
                            "Tắt sẽ khiến việc gửi file bị gián đoạn?", "Close Window?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                        box.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        box.dispose();
                        try {
                            socket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            };

            box.addWindowListener(windowAdapter);

            // loop to send data
            while (!socket.isClosed() && (count = fileInputStream.read(buffer)) > 0) {
                System.out.println(count);
                // create header
                String header = Client.SEND_FILE_COMMAND + "/" +  connectCode + "/";

                // write to server
                outputStream.writeUTF(header);
                outputStream.writeInt(count);
                outputStream.write(buffer);
                currentRead += count;

                bar.setValue((int)((float)currentRead / fileLength * 100));
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            if (bar.getValue() != 100)
                JOptionPane.showMessageDialog(box, "Có lỗi khi gửi file " + fileName +"!");
            else
                JOptionPane.showMessageDialog(box, "Gửi file " + fileName +" thành công!");

            bar.setValue(0);
            box.removeWindowListener(windowAdapter);
            box.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }
    }
}
