package com.impulsm.fssp.handlers;

import com.sun.org.apache.xpath.internal.XPathAPI;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.xml.security.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI;
import ru.CryptoPro.JCPxml.xmldsig.JCPXMLDSigInit;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by vinichenkosa on 28/05/15.
 */
public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {


    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        logger.debug("handleMessage() was called");

        SOAPMessage message = context.getMessage();
        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isRequest) {

            try {
                org.apache.xml.security.Init.init();
                // Инициализация сервис-провайдера.
                if (!JCPXMLDSigInit.isInitialized()) {
                    JCPXMLDSigInit.init();
                }


                // Инициализация ключевого контейнера и получение сертификата и закрытого ключа.
                KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
                keyStore.load(null, "".toCharArray());
                PrivateKey privateKey = (PrivateKey) keyStore.getKey("gibdd", "1234567890".toCharArray());
                X509Certificate cert = (X509Certificate) keyStore.getCertificate("gibdd");

                // Prepare secured header
                //message.getSOAPPart().getEnvelope().addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                message.getSOAPPart().getEnvelope().addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                message.getSOAPPart().getEnvelope().addNamespaceDeclaration("ds", "http://www.w3.org/2000/09/xmldsig#");
                message.getSOAPBody().setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id", "body");

                //Добавляем заголовки для помещения информации о подписи:
                WSSecHeader header = new WSSecHeader("http://smev.gosuslugi.ru/actors/smev", false);

                Element sec = header.insertSecurityHeader(message.getSOAPPart().getEnvelope().getOwnerDocument());

                Document doc = message.getSOAPPart().getEnvelope().getOwnerDocument();

                Element token = (Element) sec.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:BinarySecurityToken"));
                token.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
                token.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
                token.setAttribute("wsu:Id", "SenderCertificate");
                //token.setAttribute("xmlns:wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                //token.setAttribute("xmlns:wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                header.getSecurityHeader().appendChild(token);

                //Далее необходимо создать экземпляр сервис-провайдера для подписи документа:
                Provider xmlDSigProvider = new XMLDSigRI();

                //Добавляем описание преобразований над документом и узлом SignedInfo согласно методическим рекомендациям СМЭВ.
                // К каноническому виду приводим с помощью алгоритма http://www.w3.org/2001/10/xml-exc-c14n#:
                final Transforms transforms = new Transforms(doc);
                transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
                XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

                //Преобразования над узлом ds:SignedInfo:
                List<Transform> transformList = new ArrayList<Transform>();
                Transform transformC14N = fac.newTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, (XMLStructure) null);
                transformList.add(transformC14N);

                //Добавляем ссылку на подписываемый узел с идентификатором "body":
                // Ссылка на подписываемые данные с алгоритмом хеширования ГОСТ 34.11.
                Reference ref = fac.newReference("#body", fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null),
                        transformList, null, null);

                //Задаём алгоритм подписи:
                SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
                        (C14NMethodParameterSpec) null), fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", null), Collections.singletonList(ref));
                //В качестве алгоритма хэширования был задан алгоритм ГОСТ Р 34.11-94, а алгоритма подписи — ГОСТ Р 34.10-2001.

                //Создаём узел ds:KeyInfo с информацией о сертификате:
                KeyInfoFactory kif = fac.getKeyInfoFactory();
                X509Data x509d = kif.newX509Data(Collections.singletonList((X509Certificate) cert));
                KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));


                //Подписываем данные в элементе token:
                javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(si, ki);
                DOMSignContext signContext = new DOMSignContext((Key) privateKey, token);
                sig.sign(signContext);

                //Следующий этап — поместить узел ds:Signature и сертификат (X509Certificate) в узел wsse:Security,
                // причём сертификат нужно удалить из ds:KeyInfo и оставить там ссылку на wsse:BinarySecurityToken с сертификатом:

                // Узел подписи Signature.
                Element sigE = (Element) XPathAPI.selectSingleNode(signContext.getParent(), "//ds:Signature");

                // Блок данных KeyInfo.
                Node keyE = XPathAPI.selectSingleNode(sigE, "//ds:KeyInfo", sigE);
                // Insert signature node in document
                token.appendChild(doc.createTextNode(XPathAPI.selectSingleNode(keyE, "//ds:X509Certificate", keyE).getFirstChild().getNodeValue()));


                // Удаляем содержимое KeyInfo
                keyE.removeChild(XPathAPI.selectSingleNode(keyE, "//ds:X509Data", keyE));
                NodeList chl = keyE.getChildNodes();
                for (int i = 0; i < chl.getLength(); i++) {
                    keyE.removeChild(chl.item(i));
                }

                // Узел KeyInfo содержит указание на проверку подписи с помощью сертификата SenderCertificate.
                Node str = keyE.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:SecurityTokenReference"));
                Element strRef = (Element) str.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Reference"));
                strRef.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
                strRef.setAttribute("URI", "#SenderCertificate");
                header.getSecurityHeader().appendChild(sigE);
            } catch (Exception ex) {
                logger.error("Ошибка формирования заголовков безопасности.", ex);
                return false;
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

}
