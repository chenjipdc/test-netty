package top.chenjipdc.testnetty.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author chenjipdc@gmail.com
 * @date 2020-06-09 10:11
 * <p>
 * 一个连接一个线程处理，当高并发请求时，线程太多，会拖垮服务器
 */
public class BioUtils {

    private final static int PORT = 29901;

    public static void startServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);

            //noinspection InfiniteLoopStatement
            while (true) {
                final Socket accept = serverSocket.accept();
                new Thread(() -> {
                    BufferedReader reader = null;
                    PrintWriter writer = null;

                    try {
                        reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                        writer = new PrintWriter(accept.getOutputStream(),
                                true);

                        String recv = reader.readLine();
                        writer.println("server => write: " + recv);
                        System.out.println("server recv: " + recv);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                        if (writer != null) {
                            writer.close();
                        }

                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (accept != null) {
                            try {
                                accept.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clientSend(int id) {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            socket = new Socket("127.0.0.1",
                    PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(),
                    true);

            writer.println("client => write by: " + id);
            String recv = reader.readLine();
            System.out.println(id + " recv: " + recv);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (writer != null) {
                writer.close();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
