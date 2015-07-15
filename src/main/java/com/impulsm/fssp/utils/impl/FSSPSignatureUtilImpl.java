package com.impulsm.fssp.utils.impl;

import com.impulsm.fssp.utils.api.IFSSPSignatureUtil;
import com.impulsm.signatureutils.container.PKCS7Container;
import com.impulsm.signatureutils.keystore.Keystore;
import com.impulsm.signatureutils.keystore.KeystoreTypes;
import com.impulsm.signatureutils.signature.GOSTSignature;
import com.objsys.asn1j.runtime.Asn1Exception;
import com.objsys.asn1j.runtime.Asn1GeneralizedTime;
import com.objsys.asn1j.runtime.Asn1ObjectIdentifier;
import com.objsys.asn1j.runtime.Asn1Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Attribute;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Attribute_values;
import ru.CryptoPro.JCP.params.OID;

import javax.enterprise.context.ApplicationScoped;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.GregorianCalendar;

/**
 * Created by vinichenkosa on 15/07/15.
 */
@ApplicationScoped
public class FSSPSignatureUtilImpl implements IFSSPSignatureUtil {

    private PKCS7Container pkcs7Cont;
    private Keystore ks;
    private PrivateKey pk;
    private Certificate cert;

    public FSSPSignatureUtilImpl() {
        try {
            //XmlDSignTools.init();
            pkcs7Cont = new PKCS7Container(new GOSTSignature());
            ks = new Keystore();
            ks.load(KeystoreTypes.RutokenStore);
            pk = ks.loadKey("Efremov E.L.", "170808");
            cert = ks.loadCertificate("Efremov E.L.");
        } catch (Exception ex) {
            logger.error("Не удалось инициализировать класс для подписи документа основания", ex);
        }

    }

    @Override
    public byte[] pkcs7sign(byte[] data) throws Exception {
        try {
            return pkcs7Cont.generatePKCS7Signature(
                    pk, cert, data, getDateAttributeForPKCS7Sign(), true
            );
        } catch (Exception ex) {
            throw new Exception("Ошибка подпси документа основания.", ex);
        }
    }

    /**
     * Получаем аттрибут с датой неоплаченного штрафа
     *
     * @return
     */
    private Attribute[] getDateAttributeForPKCS7Sign() throws Asn1Exception {

        Attribute_values attr = new Attribute_values();
        attr.elements = new Asn1Type[1];

        Asn1GeneralizedTime date = new Asn1GeneralizedTime();
        date.setTime(new GregorianCalendar());
        attr.elements[0] = date;

        return new Attribute[]{new Attribute(
                new Asn1ObjectIdentifier(
                        new OID(STR_CMS_OID_DATE).value),
                attr)};
    }

    private final String STR_CMS_OID_DATE = "1.2.643.5.1.5.2.3.1";

    private Logger logger = LoggerFactory.getLogger(getClass());
}
