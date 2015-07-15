package com.impulsm.fssp.utils.impl;

import com.impulsm.fssp.utils.api.IXMLSerializer;

import javax.enterprise.context.RequestScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;

/**
 * Created by vinichenkosa on 08/07/15.
 */
@RequestScoped
public class DefaultXMLSerializer implements IXMLSerializer {

    @Override
    public <T> byte[] serializeToStreamedXML(JAXBElement<T> obj) throws Exception {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();) {

            JAXBContext jaxbCtx = JAXBContext.newInstance(obj.getDeclaredType());
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(obj, bos);
            return bos.toByteArray();
        }
    }
}
