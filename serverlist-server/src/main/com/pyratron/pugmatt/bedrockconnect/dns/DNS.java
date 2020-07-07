package main.com.pyratron.pugmatt.bedrockconnect.dns;

import org.xbill.DNS.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.List;

// Referenced code from: https://medium.com/swlh/mock-a-dns-server-in-java-a810b9338872

public class DNS {
            private Thread thread = null;
            private volatile boolean running = false;
            private static final int UDP_SIZE = 512;
            private final int port;
            private int requestCount = 0;

            private String localIP;
            private String publicIP;

            private String localIPV6;
            private String publicIPV6;

            public DNS(String localIP, String publicIP, String localIPV6, String publicIPV6, int port) {
                this.port = port;
                this.localIP = localIP;
                this.publicIP = publicIP;

                this.localIPV6 = localIPV6;
                this.publicIPV6 = publicIPV6;
            }

            public void start() {
                running = true;
                thread = new Thread(() -> {
                    try {
                        serve();
                    } catch (IOException ex) {
                        stop();
                        throw new RuntimeException(ex);
                    }
                });
                thread.start();
                System.out.println("BedrockConnect DNS server now running ( Local IPv4: " + localIP + " ; Public IPv4: " + publicIP + " )");
            }

            public void stop() {
                running = false;
                thread.interrupt();
                thread = null;
            }

            public int getRequestCount() {
                return requestCount;
            }

            private void serve() throws IOException {
                DatagramSocket socket = new DatagramSocket(port);
                while (running) {
                    process(socket);
                }
            }

            private void process(DatagramSocket socket) throws IOException {
                byte[] in = new byte[UDP_SIZE];

                DatagramPacket indp = new DatagramPacket(in, UDP_SIZE);
                socket.receive(indp);
                ++requestCount;

                Message request = new Message(in);
                Message response = new Message(request.getHeader().getID());

                response.addRecord(request.getQuestion(), Section.QUESTION);
                System.out.println(request.getQuestion().getName().toString());
                List<String> featuredServers = Arrays.asList("receive-lp1.dg.srv.nintendo.net", "www.facebook.com.", "mobile.twitter.com.", "geo.hivebedrock.network.", "hivebedrock.network.", "mco.mineplex.com.", "play.inpvp.net.", "mco.lbsg.net.", "mco.cubecraft.net.");
                if(featuredServers.contains(request.getQuestion().getName().toString())) {
                    if (indp.getAddress().getHostAddress().startsWith("192.168")) {
                        System.out.println(localIP);
                        response.addRecord(Record.fromString(Name.root, Type.A, DClass.ANY, 86400, "192.168.0.140", Name.root), Section.ANSWER);
                        //if(localIPV6 != null) {
                        //    response.addRecord(Record.fromString(Name.root, Type.AAAA, DClass.IN, 86400, localIPV6, Name.root), Section.ANSWER);
                        //}
                    }
                    else {
                        response.addRecord(Record.fromString(Name.root, Type.A, DClass.IN, 86400, publicIP, Name.root), Section.ANSWER);
                        if(publicIPV6 != null) {
                            response.addRecord(Record.fromString(Name.root, Type.AAAA, DClass.IN, 86400, publicIPV6, Name.root), Section.ANSWER);
                        }
                    }

                    byte[] resp = response.toWire();
                    DatagramPacket outdp = new DatagramPacket(resp, resp.length, indp.getAddress(), indp.getPort());
                    socket.send(outdp);
                }
            }
}
