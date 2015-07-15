package com.impulsm.fssp.utils.api;

import javax.xml.bind.JAXBElement;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public interface IXMLSerializer {

    <T> byte[] serializeToStreamedXML(JAXBElement<T> obj) throws Exception;
}
