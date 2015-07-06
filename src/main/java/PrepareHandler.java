import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Created by vinichenkosa on 01/06/15.
 */
public class PrepareHandler implements LogicalHandler<LogicalMessageContext> {
    @Override
    public boolean handleMessage(LogicalMessageContext context) {
        LogicalMessage msg = context.getMessage();
        return true;
    }

    @Override
    public boolean handleFault(LogicalMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }
}
