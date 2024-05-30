import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

class Server extends Bridge implements Runnable {
    int port;
    ServerSocket server;
    volatile Thread thread;
    public Client[] clients;
    protected final Object clientsLock = new Object[0];
    int clientCount;
    boolean active = false;
    boolean has_host = false;

    int serverid = 0;
    static int servercount = 0;

    public Server(int port) {
        this.port = port;
        try {
            serverid = servercount++;
            println("Booting Server on " + port);
            server = new ServerSocket(this.port);
            println("Server open via " + server);
            active = true;
            synchronized (clientsLock) {
                clients = new Client[10];
            }
            thread = new Thread(this);
            println("Running");
            thread.start();
        } catch (IOException e) {
            thread = null;
            active = false;
            throw new RuntimeException(e);
        }
    }

    protected void disconnectAll() {
        for (int i = 0; i < clients.length; i++) {
            try {
                clients[i].stop();
            } catch (Exception ignored) {
            }
            clients[i] = null;
        }
    }

    public void stop() {
        active = false;
        thread = null;
        synchronized (clientsLock) {
            if (clients != null) {
                disconnectAll();
                clients = null;
            }
        }
        try {
            if (server != null) {
                server.close();
                server = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clientCount) {
                if (clients[index].active) {
                    clients[index].write(data);
                    index++;
                }
            }
        }
    }

    @Override
    public void run() {
        while (Thread.currentThread() == thread && active) {
            try {
                Socket socket = server.accept();
                Client client = new Client(socket, clientCount, this);
                synchronized (clientsLock) {
                    clients[clientCount++] = client;
                }
                send_bridge_event(this.clientCount-1, new byte[0]);
                println("New client added " + socket + " ID: " + client.clientid + " / " + clientCount);
            } catch (SocketException e) {
                printerrln("SocketException: " + e.getMessage());
                thread = null;
                active = false;
            } catch (IOException e) {
                e.printStackTrace();
                thread = null;
                active = false;
            }
        }
        stop();
        println("Stopping");
    }

    @Override
    void receive_bridge_event(int header, byte[] data) {
//        println("Received Data from " + header + ", " + Arrays.toString(data));
        if (header == 0) {
            synchronized (clientsLock) {
                for (int i = 1; i < clientCount; i++) {
                    try {
                        this.clients[i].write(data);
                    } catch (Exception ignore) {
                    }
                }
            }
        } else if (data.length > 0) {
            synchronized (clientsLock) {
                try {
                    this.clients[0].write(data);
                } catch (Exception ignore) {
                }
            }
        }
    }

    void println(String vargs) {
        System.out.println("[\u001B[34mServer " + this.serverid + "\u001b[37m] " + vargs);
    }

    void printerrln(String vargs) {
        System.err.println("[\u001B[34mServer " + this.serverid + "\u001b[37m] " + vargs);
    }
}