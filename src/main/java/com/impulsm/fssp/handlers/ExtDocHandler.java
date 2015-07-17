package com.impulsm.fssp.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * Created by vinichenkosa on 28/05/15.
 */
public class ExtDocHandler implements SOAPHandler<SOAPMessageContext> {


    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        System.out.println("handleMessage() was called");

        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isRequest) {
            try {
                SOAPMessage msg = context.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope env = sp.getEnvelope();
                SOAPBody body = env.getBody();
                NodeList list = body.getElementsByTagNameNS("http://www.red-soft.biz/schemas/fssp/common/2011/0.5", "ExtDoc");

                for (int i = 0; i < list.getLength(); i++) {
                    //((SOAPElement) item).setElementQName(new QName("http://www.red-soft.biz/schemas/fssp/common/2011/0.5", "ExtDoc", item.getPrefix()));
                    //item.setPrefix("");
                    ((SOAPElement) list.item(i)).addNamespaceDeclaration(list.item(i).getPrefix(), "http://www.red-soft.biz/schemas/fssp/common/2011/0.5");
                }

                dumpSOAPMessage(msg);

            } catch (SOAPException e) {
                e.printStackTrace();
            }
        }
        return true;

    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {

    }


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private void dumpSOAPMessage(SOAPMessage msg) {
        if (msg == null) {
            System.out.println("SOAP Message is null");
            return;
        }
        System.out.println("");
        System.out.println("--------------------");
        System.out.println("DUMP OF SOAP MESSAGE");
        System.out.println("--------------------");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            msg.writeTo(baos);
            System.out.println(baos.toString(getMessageEncoding(msg)));
// show included values
            String values = msg.getSOAPBody().getTextContent();
            System.out.println("Included values:" + values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMessageEncoding(SOAPMessage msg) throws SOAPException {
        String encoding = "utf-8";
        if (msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING) != null) {
            encoding = msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING).toString();
        }
        return encoding;
    }

}
