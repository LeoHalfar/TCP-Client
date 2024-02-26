package tcpclient;

import java.net.*;
import java.io.*;

public class TCPClient {
    public boolean shut = false;
    public Integer lim = 0;
    public Integer bytesreceived = 0;
    public Integer Time = 0;

    ByteArrayOutputStream dynamic = new ByteArrayOutputStream();

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {

        shut = shutdown;
        if (lim != null) {
            lim = limit;
        }
        if (timeout != null) {
            Time = timeout;
        }
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(toServerBytes);
        byte[] fromUserBuffer = new byte[1024];
        byte[] fromServerBuffer = new byte[1024];

        Socket socket = null;

        try {
            socket = new Socket(hostname, port);
            socket.setSoTimeout(Time);

            Integer FromUserLength = inputStream.read(fromUserBuffer);

            while (FromUserLength != -1) {

                socket.getOutputStream().write(fromUserBuffer, 0, FromUserLength);
                FromUserLength = inputStream.read(fromUserBuffer);

            }

            if (shut) {
                socket.shutdownOutput();

            }

            Integer FromServerLength = socket.getInputStream().read(fromServerBuffer);
            // System.out.print("FROM SERVER: ");
            // System.out.write(fromServerBuffer, 0, FromServerLength);
            while (FromServerLength != -1) {

                bytesreceived += FromServerLength;
                if (lim != null&&bytesreceived >= lim ) {
                    dynamic.write(fromServerBuffer, 0, FromServerLength - (bytesreceived - lim));
                    break;

                }

                dynamic.write(fromServerBuffer, 0, FromServerLength);

                FromServerLength = socket.getInputStream().read(fromServerBuffer);

            }

            socket.close();

        }

        catch (SocketTimeoutException e) {
            socket.close();
            return dynamic.toByteArray();

        }

        catch (IOException e) {
           // e.printStackTrace();
        }

        return dynamic.toByteArray();

    }
}
