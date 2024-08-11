package bgu.spl.net.impl.tftp;

import bgu.spl.net.impl.echo.EchoProtocol;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.srv.Server;

public class TftpServer {

    /**
     * @param port                  The port for the server socket
     * @param protocolFactory       A factory that creats new MessagingProtocols
     * @param encoderDecoderFactory A factory that creats new MessageEncoderDecoder
     * @param <T>                   The Message Object for the protocol
     * @return A new Thread per client server
     */
    public static void main(String[] args) {

        // you can use any server...
        Server.threadPerClient(
                7777, // port
                () -> new TftpProtocol(), // protocol factory
                TftpEncoderDecoder::new // message encoder decoder factory
        ).serve();

    }
}
