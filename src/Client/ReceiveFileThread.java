package Client;

import Client.GUI.ChatBoxGUI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 15/07/2020 - 8:17 PM
 * @Description
 **/
public class ReceiveFileThread extends Thread {

    int connectCode;
    String sendUserName;
    String fileName;
    long fileLength;
    FileOutputStream writeFileStream;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    WindowAdapter windowAdapter;

    ChatBoxGUI box;
    Socket socket;
    JProgressBar bar;

    public ReceiveFileThread(int connectCode, String sendUserName, String fileName, long fileLength) {
        this.connectCode = connectCode;
        this.sendUserName = sendUserName;
        this.fileName = fileName;
        this.fileLength = fileLength;
    }

    @Override
    public void run() {

        try {
            int count;
            int currentRead = 0;
            byte[] buffer = new byte[8192]; // or 4096, or more
            File file = new File(fileName);

            HashMap<String, ChatBoxGUI> map = Client.getChatBoxMap();

            if (!map.containsKey(sendUserName))
                Client.invokeGUI(Client.ViewLevel.CHAT_BOX, sendUserName);

            // wait for map update chat box
            do {
                map = Client.getChatBoxMap();
            } while (!map.containsKey(sendUserName));

            box = map.get(sendUserName);
            bar = box.getBar();

            // -- Socket
            socket = new Socket("localhost", Client.PORT);

            // in/out stream
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            if (file.exists()) file.delete();
            writeFileStream = new FileOutputStream(fileName, true);

            // Check if user wants to receive file
            bar.setIndeterminate(true);
            if (JOptionPane.showConfirmDialog(box,
                    sendUserName + " muốn gửi cho bạn một file tên " + fileName + ". Bạn có muốn nhận?", "Nhận file?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                // --- Receive establish command
                outputStream.writeUTF(Client.ESTABLISH_RECEIVE_FILE + "/" + HandleResponseThread.SUCCESSFUL + "/" + connectCode);

            } else {
                socket.close();
                bar.setIndeterminate(false);
                freeStream();
                return;
            }

            // wait for result of ESTABLISH_RECEIVE_FILE
            do {
                String rec = inputStream.readUTF();
                String command = rec.split("/")[0];
                String result = rec.split("/")[1];

                if (command.equals(Client.ESTABLISH_RECEIVE_FILE) && result.equals(HandleResponseThread.SUCCESSFUL))
                    break;
                else {
                    JOptionPane.showMessageDialog(null, "Đã quá thời gian kết nối hoặc đã có người dùng nơi khác nhận!");
                    bar.setIndeterminate(false);
                    freeStream();
                    return;
                }

            } while (true);
            bar.setIndeterminate(false);

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
                            freeStream();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            };

            box.addWindowListener(windowAdapter);

            // loop to send data
            while (!socket.isClosed()) {
                count = inputStream.readInt();
                inputStream.readFully(buffer, 0, count);
                // read from server
                writeFileStream.write(Arrays.copyOfRange(buffer, 0, count));
                currentRead += count;

                bar.setValue((int)((float)currentRead / fileLength * 100));

                if (currentRead >= fileLength)
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bar.getValue() != 100)
                JOptionPane.showMessageDialog(box, "Có lỗi khi nhận file " + fileName +"!");
            else
                JOptionPane.showMessageDialog(box, "Nhận file " + fileName +" thành công!");

            bar.setValue(0);
            box.removeWindowListener(windowAdapter);
            box.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            try {
                freeStream();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void freeStream() throws IOException {
        socket.close();
        writeFileStream.close();
    }
}
