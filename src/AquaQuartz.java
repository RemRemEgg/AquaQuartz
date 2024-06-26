import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AquaQuartz {
    static final Object delay = new Object[0];
    static int PORT = 0;

    public static void main(String[] args) throws InterruptedException {
        synchronized (delay) {
            String vport = System.getenv("AQAQTZ_PORT");
            PORT = Integer.parseInt(vport != null ? vport : "0");
            println("Starting [" + PORT + "]");
            if (PORT == 0) PORT = 8080;
            println("t2 using 8080");

//            server_side();
//            client_side();
            packet_stealer();
//            passthrough();

//            varint_to_int();

            println("Done");
        }
    }

    public static void server_side() throws InterruptedException {
        Server host = new Server(15973);
        while (!host.has_host) {
            host.println("No host connected yet...");
            for (int i = 0; i < 10; i++) {
                delay.wait(200);
            }
        }
        host.println("A connection has been established!");
        while (host.clients[0].active) {
            delay.wait(10000);
        }
        host.printerrln("Host has disconnected, Stopping server");
        host.stop();
        delay.wait(1000);
    }

    public static void client_side() throws InterruptedException {
        Client outbound = new Client("2a09:8280:1::1c:f1a1", 15973);
        Client.DelayedBridgeClient dbc = new Client.DelayedBridgeClient();
        dbc.attach_to(outbound, "localhost", 15973);
        while (dbc.parent == outbound) {
            delay.wait(10000);
            if (dbc.parent == outbound) outbound.println("DelayedBridgeClient waiting");
        }
        Client inbound = (Client) dbc.parent;
        while (outbound.active && inbound.active) {
            delay.wait(2000);
        }
        outbound.printerrln("Server Disconnected (" + outbound.active + "/" + inbound.active + ")");
        outbound.stop();
        inbound.stop();
        delay.wait(1000);
    }

    public static void packet_stealer() throws InterruptedException {
        Server host = new Server(PORT);
        while (host.clientCount == 0) {
            host.println("No host connected yet...");
            for (int i = 0; i < 10; i++) {
                delay.wait(200);
            }
        }
        host.println("A connection has been established!");
        while (host.clients[0].active) {
            delay.wait(10000);
        }
        host.printerrln("Host has disconnected, Stopping server");
        host.stop();
        delay.wait(1000);
    }

    public static String non = "{\"version\":{\"protocol\":-1,\"name\":\"Â§4â\u009A  Error\"},\"players\":{\"online\":0,\"max\":0,\"sample\":[]},\"description\":{\"color\":\"red\",\"extra\":[\"\\n\",{\"color\":\"gray\",\"extra\":[{\"bold\":true,\"text\":\"AD\"},\":\",\" \",{\"color\":\"white\",\"text\":\"Minecraft servers on demand:\"},\" \",{\"color\":\"green\",\"text\":\"craft.link/eb\"}],\"text\":\"\"}],\"text\":\"Server not found.\"},\"favicon\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAADeElEQVR4Xu2bsU7cQBCGeQF6CpCuzzNQ8BQ8BA0Vb3CKhMQTpEmRKhJKQZkiiSKlI0hHF4rLKTogwCXT0HKZ/2wry7+ztmP7fHb2RvqUiF3P/Puf77zetTc2lhwiMlD2laFyqpwrU+VReUrB//E3tKEP+uKYAefrRajwPeVEGSnzmiAHcu1xnU6FCtxSjpQLYxBNgdyoscX1VxYqZluSU/bOELwsUAs1t1lPq6ECDpWxIbAtxsoh61p6aNFd5cwQtCqgZZd1LiW00IFybYhYNdB0wHobC02+qRwbhbsGNG6y/lqhCXeUV0axrgKtOzyOSoFEymujSNeB5nomSHLa9+mTZ6C9+tdB6Dv/4uVlLyATjnlcpUKSX/tnybhQV2HdGAuPLzckuc57lzou1FVYdzqW8vMECUxyuFBXYd0pZzxOMySZ3vLB/4MBIH/aLMmNzdg4MGjAl9H1/Nv3+5Xw8evU01NgAMYWvoGS5A6LD8o1AEK4X1tcXv309BQYAIY87kVIcj+fe0vLhVwDHma/vU8oxM3dLy/3rf6N+4XIjqloAMborydIstDAnZ/BhVwD8C+3hcCpy7k/BU5ni+yYigaAIx4/DChcyeFCoKcGXPDgsYbHnTy4EOipAeDvGqMki47cwYMLgR4bcOIaUGr1lguBHhswygY/MBpNuBAoMoBz/AtFOWsaAAYwABsQ3GDChUDPDdgvnPy4cCFQ1oDp7WxxmpcBc4IyORswYAgDsBXFDSZcCJQ1ICTWom5O1p3DKQzAfhw3mHChJsRa1M3JunM4hwHYlOQGEy7kig1NhbNjQ2It6uZk3TlMYQB2ZrnBhAu5YosIibWom5P75fAIA7A9zQ0mXKgJsRZ1c3K/HJ7WBsj6K9DMj+CPm9n8zfuxR3ZsSKxF3ZysO4fFj2D0l8HoJ0KtTIXvH+xrugWu/2VyNmDAYioc/c3QUm+Hryb+J1x2URRL7pzPHWADBgzWCyKpAdEvicW9KJqaEO+yeGpA9BsjtbbGVkFFA+ytMYQUTIq4UE8NsDdHEVJhe/zd54m3qNkWbz9MPD0FBmBs4e1xhMT8gEQWEvMjMgiJ/SEphMT8mFwWEvODkgiJ/VFZhMT8sHQWSCT9OhOae1w+C4n5hQk3JNZXZtyQmF+ackNifW3ODYn5xUk3JNZXZ62QGF+eDoV0/PX5P4yU8vMeKmqdAAAAAElFTkSuQmCC\"}";

    public static void passthrough() throws InterruptedException {
        Server internal = new Server(25565) {
            @Override
            void receive_bridge_event(int header, byte[] data) {
                Client external = new Client("not_dl.exaroton.me", 25565);
                Bridge packet_mod = new Bridge() {
                    @Override
                    void receive_bridge_event(int header, byte[] data) {
                        if (data.length > 1000) {
//                            data = new byte[]{-30, 12, 0, -33, 12, 123, 34, 118, 101, 114, 115, 105, 111, 110, 34, 58, 123, 34, 112, 114, 111, 116, 111, 99, 111, 108, 34, 58, 45, 49, 44, 34, 110, 97, 109, 101, 34, 58, 34, -62, -89, 52, -30, -102, -96, 32, 69, 114, 114, 114, 114, 34, 125, 44, 34, 112, 108, 97, 121, 101, 114, 115, 34, 58, 123, 34, 111, 110, 108, 105, 110, 101, 34, 58, 48, 44, 34, 109, 97, 120, 34, 58, 48, 44, 34, 115, 97, 109, 112, 108, 101, 34, 58, 91, 93, 125, 44, 34, 100, 101, 115, 99, 114, 105, 112, 116, 105, 111, 110, 34, 58, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 114, 101, 100, 34, 44, 34, 101, 120, 116, 114, 97, 34, 58, 91, 34, 92, 110, 34, 44, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 103, 114, 97, 121, 34, 44, 34, 101, 120, 116, 114, 97, 34, 58, 91, 123, 34, 98, 111, 108, 100, 34, 58, 116, 114, 117, 101, 44, 34, 116, 101, 120, 116, 34, 58, 34, 65, 68, 34, 125, 44, 34, 58, 34, 44, 34, 32, 34, 44, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 119, 104, 105, 116, 101, 34, 44, 34, 116, 101, 120, 116, 34, 58, 34, 77, 105, 110, 101, 99, 114, 97, 102, 116, 32, 115, 101, 114, 118, 101, 114, 115, 32, 111, 110, 32, 100, 101, 109, 97, 110, 100, 58, 34, 125, 44, 34, 32, 34, 44, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 103, 114, 101, 101, 110, 34, 44, 34, 116, 101, 120, 116, 34, 58, 34, 99, 114, 97, 102, 116, 46, 108, 105, 110, 107, 47, 101, 98, 34, 125, 93, 44, 34, 116, 101, 120, 116, 34, 58, 34, 34, 125, 93, 44, 34, 116, 101, 120, 116, 34, 58, 34, 83, 101, 114, 118, 101, 114, 32, 110, 111, 116, 32, 102, 111, 117, 110, 100, 46, 34, 125, 44, 34, 102, 97, 118, 105, 99, 111, 110, 34, 58, 34, 100, 97, 116, 97, 58, 105, 109, 97, 103, 101, 47, 112, 110, 103, 59, 98, 97, 115, 101, 54, 52, 44, 105, 86, 66, 79, 82, 119, 48, 75, 71, 103, 111, 65, 65, 65, 65, 78, 83, 85, 104, 69, 85, 103, 65, 65, 65, 69, 65, 65, 65, 65, 66, 65, 67, 65, 89, 65, 65, 65, 67, 113, 97, 88, 72, 101, 65, 65, 65, 68, 101, 69, 108, 69, 81, 86, 82, 52, 88, 117, 50, 98, 115, 85, 55, 99, 81, 66, 67, 71, 101, 81, 70, 54, 67, 112, 67, 117, 122, 122, 78, 81, 56, 66, 81, 56, 66, 65, 48, 86, 98, 51, 67, 75, 104, 77, 81, 84, 112, 69, 109, 82, 75, 104, 74, 75, 81, 90, 107, 105, 105, 83, 75, 108, 73, 48, 104, 72, 70, 52, 114, 76, 75, 84, 111, 103, 119, 67, 88, 84, 48, 72, 75, 90, 47, 50, 119, 114, 121, 55, 43, 122, 116, 109, 80, 55, 102, 72, 98, 50, 82, 118, 113, 85, 105, 70, 51, 80, 47, 80, 117, 102, 55, 55, 122, 101, 116, 84, 99, 50, 108, 104, 119, 105, 77, 108, 68, 50, 108, 97, 70, 121, 113, 112, 119, 114, 85, 43, 86, 82, 101, 85, 114, 66, 47, 47, 69, 51, 116, 75, 69, 80, 43, 117, 75, 89, 65, 101, 102, 114, 82, 97, 106, 119, 80, 101, 86, 69, 71, 83, 110, 122, 109, 105, 65, 72, 99, 117, 49, 120, 110, 85, 54, 70, 67, 116, 120, 83, 106, 112, 81, 76, 89, 120, 66, 78, 103, 100, 121, 111, 115, 99, 88, 49, 86, 120, 89, 113, 90, 108, 117, 83, 85, 47, 98, 79, 69, 76, 119, 115, 85, 65, 115, 49, 116, 49, 108, 80, 113, 54, 69, 67, 68, 112, 87, 120, 73, 98, 65, 116, 120, 115, 111, 104, 54, 49, 112, 54, 97, 78, 70, 100, 53, 99, 119, 81, 116, 67, 113, 103, 90, 90, 100, 49, 76, 105, 87, 48, 48, 73, 70, 121, 98, 89, 104, 89, 78, 100, 66, 48, 119, 72, 111, 98, 67, 48, 50, 43, 113, 82, 119, 98, 104, 98, 115, 71, 78, 71, 54, 121, 47, 108, 113, 104, 67, 88, 101, 85, 86, 48, 97, 120, 114, 103, 75, 116, 79, 122, 121, 79, 83, 111, 70, 69, 121, 109, 117, 106, 83, 78, 101, 66, 53, 110, 111, 109, 83, 72, 76, 97, 57, 43, 109, 84, 90, 54, 67, 57, 43, 116, 100, 66, 54, 68, 118, 47, 52, 117, 86, 108, 76, 121, 65, 84, 106, 110, 108, 99, 112, 85, 75, 83, 88, 47, 116, 110, 121, 98, 104, 81, 86, 50, 72, 100, 71, 65, 117, 80, 76, 122, 99, 107, 117, 99, 53, 55, 108, 122, 111, 117, 49, 70, 86, 89, 100, 122, 113, 87, 56, 118, 77, 69, 67, 85, 120, 121, 117, 70, 66, 88, 89, 100, 48, 112, 90, 122, 120, 79, 77, 121, 83, 90, 51, 118, 76, 66, 47, 52, 77, 66, 73, 72, 47, 97, 76, 77, 109, 78, 122, 100, 103, 52, 77, 71, 106, 65, 108, 57, 72, 49, 47, 78, 118, 51, 43, 53, 88, 119, 56, 101, 118, 85, 48, 49, 78, 103, 65, 77, 89, 87, 118, 111, 71, 83, 53, 65, 54, 76, 68, 56, 111, 49, 65, 69, 75, 52, 88, 49, 116, 99, 88, 118, 51, 48, 57, 66, 81, 89, 65, 73, 89, 56, 55, 107, 86, 73, 99, 106, 43, 102, 101, 48, 118, 76, 104, 86, 119, 68, 72, 109, 97, 47, 118, 85, 56, 111, 120, 77, 51, 100, 76, 121, 47, 51, 114, 102, 54, 78, 43, 52, 88, 73, 106, 113, 108, 111, 65, 77, 98, 111, 114, 121, 100, 73, 115, 116, 68, 65, 110, 90, 47, 66, 104, 86, 119, 68, 56, 67, 43, 51, 104, 99, 67, 112, 121, 55, 107, 47, 66, 85, 53, 110, 105, 43, 121, 89, 105, 103, 97, 65, 73, 120, 52, 47, 68, 67, 104, 99, 121, 101, 70, 67, 111, 75, 99, 71, 88, 80, 68, 103, 115, 89, 98, 72, 110, 84, 121, 52, 69, 79, 105, 112, 65, 101, 68, 118, 71, 113, 77, 107, 105, 52, 55, 99, 119, 89, 77, 76, 103, 82, 52, 98, 99, 79, 73, 97, 85, 71, 114, 49, 108, 103, 117, 66, 72, 104, 115, 119, 121, 103, 89, 47, 77, 66, 112, 78, 117, 66, 65, 111, 77, 111, 66, 122, 47, 65, 116, 70, 79, 87, 115, 97, 65, 65, 89, 119, 65, 66, 115, 81, 51, 71, 68, 67, 104, 85, 68, 80, 68, 100, 103, 118, 110, 80, 121, 52, 99, 67, 70, 81, 49, 111, 68, 112, 55, 87, 120, 120, 109, 112, 99, 66, 99, 52, 73, 121, 79, 82, 115, 119, 89, 65, 103, 68, 115, 66, 88, 70, 68, 83, 90, 99, 67, 74, 81, 49, 73, 67, 84, 87, 111, 109, 53, 79, 49, 112, 51, 68, 75, 81, 122, 65, 102, 104, 119, 51, 109, 72, 67, 104, 74, 115, 82, 97, 49, 77, 51, 74, 117, 110, 77, 52, 104, 119, 72, 89, 108, 79, 81, 71, 69, 121, 55, 107, 105, 103, 49, 78, 104, 98, 78, 106, 81, 50, 73, 116, 54, 117, 90, 107, 51, 84, 108, 77, 89, 81, 66, 50, 90, 114, 110, 66, 104, 65, 117, 53, 89, 111, 115, 73, 105, 98, 87, 111, 109, 53, 80, 55, 53, 102, 65, 73, 65, 55, 65, 57, 122, 81, 48, 109, 88, 75, 103, 74, 115, 82, 90, 49, 99, 51, 75, 47, 72, 74, 55, 87, 66, 115, 106, 54, 75, 57, 68, 77, 106, 43, 67, 80, 109, 57, 110, 56, 122, 102, 117, 120, 82, 51, 90, 115, 83, 75, 120, 70, 51, 90, 121, 115, 79, 52, 102, 70, 106, 50, 68, 48, 108, 56, 72, 111, 74, 48, 75, 116, 84, 73, 88, 118, 72, 43, 120, 114, 117, 103, 87, 117, 47, 50, 86, 121, 78, 109, 68, 65, 89, 105, 111, 99, 47, 99, 51, 81, 85, 109, 43, 72, 114, 121, 98, 43, 74, 49, 120, 50, 85, 82, 82, 76, 55, 112, 122, 80, 72, 87, 65, 68, 66, 103, 122, 87, 67, 121, 75, 112, 65, 100, 69, 118, 105, 99, 87, 57, 75, 74, 113, 97, 69, 79, 43, 121, 101, 71, 112, 65, 57, 66, 115, 106, 116, 98, 98, 71, 86, 107, 70, 70, 65, 43, 121, 116, 77, 89, 81, 85, 84, 73, 113, 52, 85, 69, 56, 78, 115, 68, 100, 72, 69, 86, 74, 104, 101, 47, 122, 100, 53, 52, 109, 51, 113, 78, 107, 87, 98, 122, 57, 77, 80, 68, 48, 70, 66, 109, 66, 115, 52, 101, 49, 120, 104, 77, 84, 56, 103, 69, 81, 87, 69, 118, 77, 106, 77, 103, 105, 74, 47, 83, 69, 112, 104, 77, 84, 56, 109, 70, 119, 87, 69, 118, 79, 68, 107, 103, 105, 74, 47, 86, 70, 90, 104, 77, 84, 56, 115, 72, 81, 87, 83, 67, 84, 57, 79, 104, 79, 97, 101, 49, 119, 43, 67, 52, 110, 53, 104, 81, 107, 51, 74, 78, 90, 88, 90, 116, 121, 81, 109, 70, 43, 97, 99, 107, 78, 105, 102, 87, 51, 79, 68, 89, 110, 53, 120, 85, 107, 51, 74, 78, 90, 88, 90, 54, 50, 81, 71, 70, 43, 101, 68, 111, 86, 48, 47, 80, 88, 53, 80, 52, 121, 85, 56, 118, 77, 101, 75, 109, 113, 100, 65, 65, 65, 65, 65, 69, 108, 70, 84, 107, 83, 117, 81, 109, 67, 67, 34, 125};
                            ArrayList<Byte> new_data = new ArrayList<>(List.of(new Byte[]{-30, 12, 0, -33, 12}));
                            new_data.addAll(List.of(to_bad_bytes(non.getBytes())));
                            println("Normal: " + data.length + " New:" + new_data.size());
                            data = to_bytes(new_data.toArray(new Byte[0]));
                        }
                        this.send_bridge_event(header, data);
                    }
                };
                this.clients[0].set_bridge(external);
                external.set_bridge(packet_mod);
                packet_mod.set_bridge(this.clients[0]);
                this.clients[0].send_bridge_event(0, data);

//                external.send_bridge_event(0, new byte[]{-30, 12, 0, -33, 12, 123, 34, 118, 101, 114, 115, 105, 111, 110, 34, 58, 123, 34, 112, 114, 111, 116, 111, 99, 111, 108, 34, 58, 45, 49, 44, 34, 110, 97, 109, 101, 34, 58, 34, -62, -89, 52, -30, -102, -96, 32, 69, 114, 111, 111, 114, 34, 125, 44, 34, 112, 108, 97, 121, 101, 114, 115, 34, 58, 123, 34, 111, 110, 108, 105, 110, 101, 34, 58, 48, 44, 34, 109, 97, 120, 34, 58, 48, 44, 34, 115, 97, 109, 112, 108, 101, 34, 58, 91, 93, 125, 44, 34, 100, 101, 115, 99, 114, 105, 112, 116, 105, 111, 110, 34, 58, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 114, 101, 100, 34, 44, 34, 101, 120, 116, 114, 97, 34, 58, 91, 34, 92, 110, 34, 44, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 103, 114, 97, 121, 34, 44, 34, 101, 120, 116, 114, 97, 34, 58, 91, 123, 34, 98, 111, 108, 100, 34, 58, 116, 114, 117, 101, 44, 34, 116, 101, 120, 116, 34, 58, 34, 65, 68, 34, 125, 44, 34, 58, 34, 44, 34, 32, 34, 44, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 119, 104, 105, 116, 101, 34, 44, 34, 116, 101, 120, 116, 34, 58, 34, 77, 105, 110, 101, 99, 114, 97, 102, 116, 32, 115, 101, 114, 118, 101, 114, 115, 32, 111, 110, 32, 100, 101, 109, 97, 110, 100, 58, 34, 125, 44, 34, 32, 34, 44, 123, 34, 99, 111, 108, 111, 114, 34, 58, 34, 103, 114, 101, 101, 110, 34, 44, 34, 116, 101, 120, 116, 34, 58, 34, 99, 114, 97, 102, 116, 46, 108, 105, 110, 107, 47, 101, 98, 34, 125, 93, 44, 34, 116, 101, 120, 116, 34, 58, 34, 34, 125, 93, 44, 34, 116, 101, 120, 116, 34, 58, 34, 83, 101, 114, 118, 101, 114, 32, 110, 111, 116, 32, 102, 111, 117, 110, 100, 46, 34, 125, 44, 34, 102, 97, 118, 105, 99, 111, 110, 34, 58, 34, 100, 97, 116, 97, 58, 105, 109, 97, 103, 101, 47, 112, 110, 103, 59, 98, 97, 115, 101, 54, 52, 44, 105, 86, 66, 79, 82, 119, 48, 75, 71, 103, 111, 65, 65, 65, 65, 78, 83, 85, 104, 69, 85, 103, 65, 65, 65, 69, 65, 65, 65, 65, 66, 65, 67, 65, 89, 65, 65, 65, 67, 113, 97, 88, 72, 101, 65, 65, 65, 68, 101, 69, 108, 69, 81, 86, 82, 52, 88, 117, 50, 98, 115, 85, 55, 99, 81, 66, 67, 71, 101, 81, 70, 54, 67, 112, 67, 117, 122, 122, 78, 81, 56, 66, 81, 56, 66, 65, 48, 86, 98, 51, 67, 75, 104, 77, 81, 84, 112, 69, 109, 82, 75, 104, 74, 75, 81, 90, 107, 105, 105, 83, 75, 108, 73, 48, 104, 72, 70, 52, 114, 76, 75, 84, 111, 103, 119, 67, 88, 84, 48, 72, 75, 90, 47, 50, 119, 114, 121, 55, 43, 122, 116, 109, 80, 55, 102, 72, 98, 50, 82, 118, 113, 85, 105, 70, 51, 80, 47, 80, 117, 102, 55, 55, 122, 101, 116, 84, 99, 50, 108, 104, 119, 105, 77, 108, 68, 50, 108, 97, 70, 121, 113, 112, 119, 114, 85, 43, 86, 82, 101, 85, 114, 66, 47, 47, 69, 51, 116, 75, 69, 80, 43, 117, 75, 89, 65, 101, 102, 114, 82, 97, 106, 119, 80, 101, 86, 69, 71, 83, 110, 122, 109, 105, 65, 72, 99, 117, 49, 120, 110, 85, 54, 70, 67, 116, 120, 83, 106, 112, 81, 76, 89, 120, 66, 78, 103, 100, 121, 111, 115, 99, 88, 49, 86, 120, 89, 113, 90, 108, 117, 83, 85, 47, 98, 79, 69, 76, 119, 115, 85, 65, 115, 49, 116, 49, 108, 80, 113, 54, 69, 67, 68, 112, 87, 120, 73, 98, 65, 116, 120, 115, 111, 104, 54, 49, 112, 54, 97, 78, 70, 100, 53, 99, 119, 81, 116, 67, 113, 103, 90, 90, 100, 49, 76, 105, 87, 48, 48, 73, 70, 121, 98, 89, 104, 89, 78, 100, 66, 48, 119, 72, 111, 98, 67, 48, 50, 43, 113, 82, 119, 98, 104, 98, 115, 71, 78, 71, 54, 121, 47, 108, 113, 104, 67, 88, 101, 85, 86, 48, 97, 120, 114, 103, 75, 116, 79, 122, 121, 79, 83, 111, 70, 69, 121, 109, 117, 106, 83, 78, 101, 66, 53, 110, 111, 109, 83, 72, 76, 97, 57, 43, 109, 84, 90, 54, 67, 57, 43, 116, 100, 66, 54, 68, 118, 47, 52, 117, 86, 108, 76, 121, 65, 84, 106, 110, 108, 99, 112, 85, 75, 83, 88, 47, 116, 110, 121, 98, 104, 81, 86, 50, 72, 100, 71, 65, 117, 80, 76, 122, 99, 107, 117, 99, 53, 55, 108, 122, 111, 117, 49, 70, 86, 89, 100, 122, 113, 87, 56, 118, 77, 69, 67, 85, 120, 121, 117, 70, 66, 88, 89, 100, 48, 112, 90, 122, 120, 79, 77, 121, 83, 90, 51, 118, 76, 66, 47, 52, 77, 66, 73, 72, 47, 97, 76, 77, 109, 78, 122, 100, 103, 52, 77, 71, 106, 65, 108, 57, 72, 49, 47, 78, 118, 51, 43, 53, 88, 119, 56, 101, 118, 85, 48, 49, 78, 103, 65, 77, 89, 87, 118, 111, 71, 83, 53, 65, 54, 76, 68, 56, 111, 49, 65, 69, 75, 52, 88, 49, 116, 99, 88, 118, 51, 48, 57, 66, 81, 89, 65, 73, 89, 56, 55, 107, 86, 73, 99, 106, 43, 102, 101, 48, 118, 76, 104, 86, 119, 68, 72, 109, 97, 47, 118, 85, 56, 111, 120, 77, 51, 100, 76, 121, 47, 51, 114, 102, 54, 78, 43, 52, 88, 73, 106, 113, 108, 111, 65, 77, 98, 111, 114, 121, 100, 73, 115, 116, 68, 65, 110, 90, 47, 66, 104, 86, 119, 68, 56, 67, 43, 51, 104, 99, 67, 112, 121, 55, 107, 47, 66, 85, 53, 110, 105, 43, 121, 89, 105, 103, 97, 65, 73, 120, 52, 47, 68, 67, 104, 99, 121, 101, 70, 67, 111, 75, 99, 71, 88, 80, 68, 103, 115, 89, 98, 72, 110, 84, 121, 52, 69, 79, 105, 112, 65, 101, 68, 118, 71, 113, 77, 107, 105, 52, 55, 99, 119, 89, 77, 76, 103, 82, 52, 98, 99, 79, 73, 97, 85, 71, 114, 49, 108, 103, 117, 66, 72, 104, 115, 119, 121, 103, 89, 47, 77, 66, 112, 78, 117, 66, 65, 111, 77, 111, 66, 122, 47, 65, 116, 70, 79, 87, 115, 97, 65, 65, 89, 119, 65, 66, 115, 81, 51, 71, 68, 67, 104, 85, 68, 80, 68, 100, 103, 118, 110, 80, 121, 52, 99, 67, 70, 81, 49, 111, 68, 112, 55, 87, 120, 120, 109, 112, 99, 66, 99, 52, 73, 121, 79, 82, 115, 119, 89, 65, 103, 68, 115, 66, 88, 70, 68, 83, 90, 99, 67, 74, 81, 49, 73, 67, 84, 87, 111, 109, 53, 79, 49, 112, 51, 68, 75, 81, 122, 65, 102, 104, 119, 51, 109, 72, 67, 104, 74, 115, 82, 97, 49, 77, 51, 74, 117, 110, 77, 52, 104, 119, 72, 89, 108, 79, 81, 71, 69, 121, 55, 107, 105, 103, 49, 78, 104, 98, 78, 106, 81, 50, 73, 116, 54, 117, 90, 107, 51, 84, 108, 77, 89, 81, 66, 50, 90, 114, 110, 66, 104, 65, 117, 53, 89, 111, 115, 73, 105, 98, 87, 111, 109, 53, 80, 55, 53, 102, 65, 73, 65, 55, 65, 57, 122, 81, 48, 109, 88, 75, 103, 74, 115, 82, 90, 49, 99, 51, 75, 47, 72, 74, 55, 87, 66, 115, 106, 54, 75, 57, 68, 77, 106, 43, 67, 80, 109, 57, 110, 56, 122, 102, 117, 120, 82, 51, 90, 115, 83, 75, 120, 70, 51, 90, 121, 115, 79, 52, 102, 70, 106, 50, 68, 48, 108, 56, 72, 111, 74, 48, 75, 116, 84, 73, 88, 118, 72, 43, 120, 114, 117, 103, 87, 117, 47, 50, 86, 121, 78, 109, 68, 65, 89, 105, 111, 99, 47, 99, 51, 81, 85, 109, 43, 72, 114, 121, 98, 43, 74, 49, 120, 50, 85, 82, 82, 76, 55, 112, 122, 80, 72, 87, 65, 68, 66, 103, 122, 87, 67, 121, 75, 112, 65, 100, 69, 118, 105, 99, 87, 57, 75, 74, 113, 97, 69, 79, 43, 121, 101, 71, 112, 65, 57, 66, 115, 106, 116, 98, 98, 71, 86, 107, 70, 70, 65, 43, 121, 116, 77, 89, 81, 85, 84, 73, 113, 52, 85, 69, 56, 78, 115, 68, 100, 72, 69, 86, 74, 104, 101, 47, 122, 100, 53, 52, 109, 51, 113, 78, 107, 87, 98, 122, 57, 77, 80, 68, 48, 70, 66, 109, 66, 115, 52, 101, 49, 120, 104, 77, 84, 56, 103, 69, 81, 87, 69, 118, 77, 106, 77, 103, 105, 74, 47, 83, 69, 112, 104, 77, 84, 56, 109, 70, 119, 87, 69, 118, 79, 68, 107, 103, 105, 74, 47, 86, 70, 90, 104, 77, 84, 56, 115, 72, 81, 87, 83, 67, 84, 57, 79, 104, 79, 97, 101, 49, 119, 43, 67, 52, 110, 53, 104, 81, 107, 51, 74, 78, 90, 88, 90, 116, 121, 81, 109, 70, 43, 97, 99, 107, 78, 105, 102, 87, 51, 79, 68, 89, 110, 53, 120, 85, 107, 51, 74, 78, 90, 88, 90, 54, 50, 81, 71, 70, 43, 101, 68, 111, 86, 48, 47, 80, 88, 53, 80, 52, 121, 85, 56, 118, 77, 101, 75, 109, 113, 100, 65, 65, 65, 65, 65, 69, 108, 70, 84, 107, 83, 117, 81, 109, 67, 67, 34, 125});
            }
        };
        while (internal.active) {
            delay.wait(2000);
        }
        internal.printerrln("Server Disconnected");
        internal.stop();
        delay.wait(1000);
    }

    // c->h login o [20, 0, -3, 5, 13, 49, 51, 55, 46, 54, 54, 46, 49, 48, 46, 49, 49, 50, 62, 101, 2, 30, 0, 12, 79, 114, 105, 103, 105, 110, 85, 110, 116, 111, 108, 100, 26, -59, -93, -31, -26, 87, 79, -108, -107, -62, 90, -97, 103, -95, 88, 116]
    // "          o [20, 0, -3, 5, 13, 49, 51, 55, 46, 54, 54, 46, 49, 48, 46, 49, 49, 50, 62, 101, 2, 30, 0, 12, 79, 114, 105, 103, 105, 110, 85, 110, 116, 111, 108, 100, 26, -59, -93, -31, -26, 87, 79, -108, -107, -62, 90, -97, 103, -95, 88, 116]
    // "          o [16, 0, -3, 5, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116, 62, 101, 2]
    // "                                                                                            + [30, 0, 12, 79, 114, 105, 103, 105, 110, 85, 110, 116, 111, 108, 100, 26, -59, -93, -31, -26, 87, 79, -108, -107, -62, 90, -97, 103, -95, 88, 116]
    // "          r [20, 0, -3, 5, 13, 49, 51, 55, 46, 54, 54, 46, 49, 48, 46, 49, 49, 50, 62, 101, 2, 27, 0, 9, 82, 101, 109, 82, 101, 109, 69, 103, 103, -125, -48, 13, -64, 124, 8, 75, -96, -89, -32, 57, -69, 47, -126, 110, 83]
    // "          r [16, 0, -3, 5, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116,           62, 101, 2, 27, 0, 9, 82, 101, 109, 82, 101, 109, 69, 103, 103, -125, -48, 13, -64, 124, 8, 75, -96, -89, -32, 57, -69, 47, -126, 110, 83]
    // "          r [33, 0, -3, 5, 26, 107, 117, 98, 101, 114, 110, 101, 116, 101, 115, 46, 100, 111, 99, 107, 101, 114, 46, 105, 110, 116, 101, 114, 110, 97, 108, 62, 101, 2, 27, 0, 9, 82, 101, 109, 82, 101, 109, 69, 103, 103, -125, -48, 13, -64, 124, 8, 75, -96, -89, -32, 57, -69, 47, -126, 110, 83]

    // c->h ping r [16, 0, -3, 5, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116, 62, 101, 1, 1, 0]
    // "         r [16, 0, -3, 5, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116, 62, 101, 1] 
    //                                                                                   + [1, 0]
    // "         r [16, 0, -3, 5, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116, 62, 101, 1, 1, 0]
    // "         o [16, 0, -3, 5, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116, 62, 101, 1]
    // "                                                                                 + [1, 0]
    static void println(String varg) {
        System.out.println("[\u001b[36mAquaQuartz Proxy\u001b[37m] " + varg);
    }

    static final int SEGMENT_BITS = 0x7F;
    static final int CONTINUE_BIT = 0x80;

    public static void varint_to_int() {
        int value = 0;
        int position = 0;
        byte currentByte;
        byte[] bytes = {-30, 12, 0, -33, 12};

        while (true) {
            currentByte = bytes[position / 7];
            value |= (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        println(value + " : " + position + "/" + position / 7);
    }

    public static Byte[] int_to_varint(int value) {
        ArrayList<Byte> bytes = new ArrayList<>();
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                bytes.add((byte) value);
                break;
            }
            bytes.add((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
        return bytes.toArray(new Byte[0]);
    }

    public static byte[] to_bytes(Byte[] bytesPrim) {
        byte[] bytes = new byte[bytesPrim.length];
        int i = 0;
        for (Byte b : bytesPrim) bytes[i++] = b;
        return bytes;
    }

    public static Byte[] to_bad_bytes(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (Byte b : bytesPrim) bytes[i++] = b;
        return bytes;
    }
}