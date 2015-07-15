package com.impulsm.fssp.utils.api;

/**
 * Created by vinichenkosa on 15/07/15.
 */
public interface IFSSPSignatureUtil {

    byte[] pkcs7sign(byte[] data) throws Exception;
}
