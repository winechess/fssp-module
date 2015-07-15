package com.impulsm.fssp.models.documents.extdoc;

import biz.red_soft.schemas.fssp.common._2011._0.ExtDoc;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "ExtDoc", namespace = "http://www.red-soft.biz/schemas/fssp/common/2011/0.5")
public class ExtendedExtDoc extends ExtDoc {

    @XmlTransient
    private String fsspCode;
    @XmlTransient
    private String sendingId;
    @XmlTransient
    private String envelopeId;
    @XmlTransient
    private String packId;
    @XmlTransient
    private String ogaiCode;

    public String getSendingId() {
        return sendingId;
    }

    public void setSendingId(String sendingId) {
        this.sendingId = sendingId;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String getFsspCode() {
        return fsspCode;
    }

    public void setFsspCode(String fsspCode) {
        this.fsspCode = fsspCode;
    }

    public String getOgaiCode() {
        return ogaiCode;
    }

    public void setOgaiCode(String ogaiCode) {
        this.ogaiCode = ogaiCode;
    }
}
