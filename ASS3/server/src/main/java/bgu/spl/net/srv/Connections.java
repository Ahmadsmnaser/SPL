package bgu.spl.net.srv;

import java.io.IOException;

public interface Connections<T> {

    public void connect(int connectionId, BlockingConnectionHandler<T> handler);

    boolean send(int connectionId, T msg);

    void disconnect(int connectionId);

    
}
