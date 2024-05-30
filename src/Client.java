import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class Client extends Bridge implements Runnable {
    Socket socket;
    public InputStream input;
    public OutputStream output;
    volatile Thread thread;
    boolean serverconnection = false;
    boolean active = false;

    int clientid = 0;
    static int clientcount = 0;

    public Client(String address, int port) {
        try {
            clientid = clientcount++;
            println("Connecting to " + address + ":" + port);
            socket = new Socket(address, port);
            active = true;
            input = socket.getInputStream();
            output = socket.getOutputStream();
            println("Connected");
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            active = false;
            stop();
        }
    }

    public Client(Socket socket_, int id, Server host) {
        try {
            this.clientid = id;
            this.serverconnection = true;
            set_bridge(host);
            println("Connecting to " + socket_.getLocalAddress());
            socket = socket_;
            active = true;
            input = socket.getInputStream();
            output = socket.getOutputStream();
            println("Connected");
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        active = false;
        thread = null;
        try {
            if (input != null) {
                input.close();
                input = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (output != null) {
                output.close();
                output = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] readBuffer;
        {
            int readBufferSize = 1 << 22;
            try {
                readBufferSize = socket.getReceiveBufferSize();
            } catch (SocketException ignore) {
            }
            readBuffer = new byte[readBufferSize];
        }
        while (Thread.currentThread() == thread) {
            try {
                while (input != null) {
                    try { // try to read a byte using a blocking read. 
                        int readCount = input.read(readBuffer, 0, readBuffer.length);
                        if (readCount > 0) {
                            byte[] readdata = new byte[readCount];
                            System.arraycopy(readBuffer, 0, readdata, 0, readCount);
                            println("Read Data " + readCount + " " + Arrays.toString(readdata));
                            this.send_bridge_event(this.clientid, readdata);
                        } else if (readCount == -1) { // read returns -1 if end-of-stream occurs (for example if the host disappears)
                            printerrln("Client got end-of-stream.");
                            stop();
                            return;
                        }
                    } catch (SocketException e) {
                        printerrln("Client SocketException: " + e.getMessage());
                        stop();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    public void write(byte[] data) {
        try {
            output.write(data);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    @Override
    void receive_bridge_event(int header, byte[] data) {
        println(Arrays.toString(data));
        this.write(data);
    }

    void println(String vargs) {
        System.out.println((this.serverconnection ? "[\u001B[33mServerConnection " + this.clientid + "\u001b[37m] " : "[\u001B[32mClient " + this.clientid + "\u001b[37m] ") + vargs);
    }
    
    void printerrln(String vargs) {
        System.err.println((this.serverconnection ? "[\u001B[33mServerConnection " + this.clientid + "\u001b[37m] " : "[\u001B[32mClient " + this.clientid + "\u001b[37m] ") + vargs);
    }
    
    public static class DelayedBridgeClient extends Bridge {
        Bridge parent;
        String address;
        int port;
        
        public void attach_to(Bridge parent, String address, int port) {
            this.parent = parent;
            this.address = address;
            this.port = port;
            this.parent.set_bridge(this);
        }
        
        @Override
        void receive_bridge_event(int header, byte[] data) {
            Client client = new Client(this.address, this.port);
            client.println("Created via delayed bridge");
            parent.set_bridge(client);
            client.set_bridge(parent);
            client.receive_bridge_event(header, data);
            this.parent = client;
        }
    }
}