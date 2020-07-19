package Server;

import java.io.*;
import java.net.Socket;

/**
 * Server
 *
 * @Created by Long - StudentID : 18120455
 * @Date 16/07/2020 - 11:37 AM
 * @Description
 **/
public class TransferFileConnection {

    private Socket sendUser;
    private final String sendUserName;
    private final String receiveUserName;
    private Socket receiveUser;
    public final HandleRequestThread thread;
    private DataOutputStream receiverWriter;
    private DataOutputStream senderWriter;

    public static int TIME_LIFE = 5;

    public TransferFileConnection(Socket sendUser, String sendUserName, String receiveUserName, HandleRequestThread thread) {
        this.sendUser = sendUser;
        this.sendUserName = sendUserName;
        this.receiveUserName = receiveUserName;
        this.thread = thread;

        new CountDownTime(TIME_LIFE, this).start();
    }

    public Socket getSendUser() {
        return sendUser;
    }

    public void setSendUser(Socket sendUser) {
        this.sendUser = sendUser;
    }

    public Socket getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(Socket receiveUser) throws IOException {
        this.receiveUser = receiveUser;

        receiverWriter = new DataOutputStream(receiveUser.getOutputStream());
        senderWriter = new DataOutputStream(sendUser.getOutputStream());
        new SocketStatusTracker(sendUser, receiveUser, receiverWriter, senderWriter, this).start();
    }

    @Override
    public int hashCode() {
        return (sendUser.getRemoteSocketAddress().toString() + sendUserName + receiveUserName).hashCode();
    }

    public void endTimeToLive() throws IOException {

        if (receiveUser == null) {
            thread.removeTransferFileConnection(this);
        }
    }

    public void sendData(byte[] data) throws IOException {

        receiverWriter.writeInt(data.length);
        receiverWriter.write(data);
    }

    public void freeStream() throws IOException {
        receiverWriter.close();
    }

    // Count down time class
    static class CountDownTime extends Thread {

        private final int timeToLive;
        private final TransferFileConnection connection;

        public CountDownTime(int timeToLive, TransferFileConnection connection) {
            this.timeToLive = timeToLive;
            this.connection = connection;
        }

        @Override
        public void run() {
            try {

                Thread.sleep(timeToLive * 1000); // secs
                connection.endTimeToLive();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Socket Status Tracker class
    static class SocketStatusTracker extends Thread {

        Socket sendUser;
        Socket receiveUser;
        DataOutputStream receiverWriter;
        DataOutputStream senderWriter;
        TransferFileConnection connection;

        public SocketStatusTracker(Socket sendUser, Socket receiveUser, DataOutputStream receiverWriter, DataOutputStream senderWriter, TransferFileConnection connection) {
            this.sendUser = sendUser;
            this.receiveUser = receiveUser;
            this.receiverWriter = receiverWriter;
            this.senderWriter = senderWriter;
            this.connection = connection;
        }

        @Override
        public void run() {
            do {
                if (sendUser.isClosed() || receiveUser.isClosed()) {
                    try {

                        if (sendUser.isClosed())
                            receiveUser.close();

                        if (receiveUser.isClosed())
                            sendUser.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } while (true);

        }
    }
}
