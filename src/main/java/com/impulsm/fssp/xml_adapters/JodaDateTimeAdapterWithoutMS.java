package com.impulsm.fssp.xml_adapters;

/**
 * Created by vinichenkosa on 05.08.15.
 */

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class JodaDateTimeAdapterWithoutMS extends XmlAdapter<String, DateTime> {

    @Override
    public DateTime unmarshal(String value) throws DatatypeConfigurationException {
        return new DateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(value).toGregorianCalendar());
    }

    @Override
    public String marshal(DateTime datetime) throws DatatypeConfigurationException {

        if (datetime == null) {
            return null;
        }
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss");
        return dtfOut.print(datetime);
    }
}