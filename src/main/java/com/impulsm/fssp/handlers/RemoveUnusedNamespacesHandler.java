package com.impulsm.fssp.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by vinichenkosa on 28/05/15.
 */
public class RemoveUnusedNamespacesHandler implements SOAPHandler<SOAPMessageContext> {


    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        System.out.println("RemoveUnusedNamespacesHandler.handleMessage() was called");

        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isRequest) {
            try {
                SOAPMessage msg = context.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope env = sp.getEnvelope();
                SOAPBody body = env.getBody();

                NodeList nodeList = body.getElementsByTagNameNS("http://www.red-soft.biz/ncore/dx/1.1/smev25/ws", "DXSmev");

                SOAPElement dxSmev = (SOAPElement) nodeList.item(0);
                Iterator namespacePrefixes = dxSmev.getNamespacePrefixes();
                Set<String> unusedPrefixes = new HashSet<>();
                while (namespacePrefixes.hasNext()) {

                    String prefix = (String) namespacePrefixes.next();

                    if (prefix.equals(dxSmev.getPrefix())) continue;

                    if (!prefixIsUsed(dxSmev, prefix)) {
                        System.out.println("prefix " + prefix + " not used.");
                        unusedPrefixes.add(prefix);
                    }
                }

                for (String unusedPrefix : unusedPrefixes) {
                    dxSmev.removeNamespaceDeclaration(unusedPrefix);
                }


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

    /**
     * Get all prefixes defined, up to the root, for a namespace URI.
     *
     * @param element
     */
    public boolean namespaceIsUsed(SOAPElement element, String namespaceURI) {
        System.out.println("Searching using of " + namespaceURI + " uri");
        return element.getElementsByTagNameNS(namespaceURI, "*").getLength() != 0;

    }

    public boolean prefixIsUsed(SOAPElement element, String prefix) {
        System.out.println("Searching using of " + prefix + " prefix");
        return namespaceIsUsed(element, element.getNamespaceURI(prefix));
    }

}
