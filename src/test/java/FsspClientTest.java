import biz.red_soft.ncore.dx._1_1.smev25.ws.NCoreDxSmev25Port;
import biz.red_soft.ncore.dx._1_1.smev25.ws.NCoreDxSmev25Service;
import com.sun.xml.ws.transport.http.client.HttpTransportPipe;
import org.junit.Test;
import ru.gosuslugi.smev.rev120315.BaseMessageType;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinichenkosa on 20/05/15.
 */
public class FsspClientTest {

    @Test
    public void test() throws Exception {
        HttpTransportPipe.dump = true;



        NCoreDxSmev25Port port = new NCoreDxSmev25Service().getNCoreDxSmev25Port();

        List<Handler> handlerChain = new ArrayList<>();
        handlerChain.add(new SecurityHandler());
        ((BindingProvider) port).getBinding().setHandlerChain(handlerChain);
        port.dx(new Holder<>(new BaseMessageType()));
    }
}