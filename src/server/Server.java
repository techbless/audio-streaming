package server;

import javax.sound.sampled.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ArrayList<DataOutputStream> listeners;
    ServerSocket serverSocket;
    Socket listener;
    DataOutputStream dos;
    Server() {
        listeners = new ArrayList<>();
    }

    private void start() {
        try {
            serverSocket = new ServerSocket(10001);
            new broadCast().start();

            while (true) {
                listener = serverSocket.accept();
                dos = new DataOutputStream(listener.getOutputStream());
                listeners.add(dos);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//start()

    public static void main(String[] args) {
        new Server().start();
    }//main()

    class broadCast extends Thread{
        AudioFormat format = new AudioFormat(192000.0f, 16, 2, true, false);
        TargetDataLine microphone;
        DataOutputStream lstn;

        @Override
        public void run() {
                int dsize;
                byte[] data = new byte[1024];

                try {
                    microphone = AudioSystem.getTargetDataLine(format);
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    microphone = (TargetDataLine) AudioSystem.getLine(info);
                    microphone.open(format);
                    data = new byte[1024];
                    microphone.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                while (true) {
                    try {
                        dsize = microphone.read(data, 0, 1024);

                        int size = listeners.size();

                        for (int i = 0; i < size; i++) {
                            lstn = listeners.get(i);
                            lstn.write(data, 0, dsize);
                        }
                    } catch(IOException e) {
                        try {
                            lstn.close();
                            listeners.remove(lstn);
                        } catch(IOException f) {
                            f.printStackTrace();
                        }
                    }
                }


        }
    }

}// server.Server class