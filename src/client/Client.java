package client;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.Socket;

public class Client {
    Socket socket;
    DataInputStream dis;
    AudioFormat format;
    DataLine.Info info;
    SourceDataLine speakers;

    public void start() {
        try {
            format = new AudioFormat(192000.0f, 16, 2, true, false);
            info = new DataLine.Info(SourceDataLine.class, format);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            socket = new Socket("192.168.0.5", 10001);
            dis = new DataInputStream(socket.getInputStream());

            int dsize = 0;
            byte[] data = new byte[1024];

            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();

            while(true) {
                dsize = dis.read(data);
                speakers.write(data, 0, dsize);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            speakers.drain();
            speakers.close();
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }

}
