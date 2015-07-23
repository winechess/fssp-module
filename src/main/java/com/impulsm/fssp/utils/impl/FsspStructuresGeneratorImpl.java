package com.impulsm.fssp.utils.impl;

import biz.red_soft.ncore.dx._1.*;
import biz.red_soft.ncore.dx._1.ObjectFactory;
import biz.red_soft.schemas.fssp.common._2011._0.ExtDoc;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.impulsm.fssp.utils.api.IFsspStructuresGenerator;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import org.joda.time.DateTime;
import ru.gosuslugi.smev.rev120315.*;

import javax.enterprise.context.RequestScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.ws.Holder;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by vinichenkosa on 07/07/15.
 */
@RequestScoped
public class FsspStructuresGeneratorImpl implements IFsspStructuresGenerator {

    @Override
    public Holder<BaseMessageType> generateRequestMessage(int packMaxCount, Collection<? extends DXBlock> dxBlockList) {

        BaseMessageType baseMsg = new BaseMessageType();
        //Message
        baseMsg.setMessage(createMessageType());
        //MessageData
        baseMsg.setMessageData(createMessageDataType(packMaxCount, dxBlockList));
        return new Holder<>(baseMsg);
    }

    private MessageType createMessageType() {

        //Message
        MessageType msg = new MessageType();
        //Sender
        msg.setSender(createSender());
        //Recipient
        msg.setRecipient(createRecipient());
        //TypeCode
        msg.setTypeCode(TypeCodeType.GFNC);
        //Status
        msg.setStatus(StatusType.REQUEST);
        //Date
        msg.setDate(new DateTime());
        //ExchangeType
        msg.setExchangeType("2");

        return msg;
    }

    private OrgExternalType createOrgExternalType(String code, String name){
        OrgExternalType orgExternalType = new OrgExternalType();
        orgExternalType.setCode(code);
        orgExternalType.setName(name);
        return orgExternalType;
    }

    private OrgExternalType createSender(){
        return createOrgExternalType("835801771", "ГИБДД МОСКВЫ");
    }

    private OrgExternalType createRecipient(){
        return createOrgExternalType("FSSP01001", "ФССП России");
    }

    private MessageDataType createMessageDataType(int packMaxCount, Collection<? extends DXBlock> dxBlockList){
        MessageDataType msgData = new MessageDataType();
        //AppData
        AppDataType appData = new AppDataType();
        appData.getAny().add(createDXBox(packMaxCount, dxBlockList));
        msgData.setAppData(appData);
        return msgData;
    }

    private JAXBElement<DXBox> createDXBox(int packMaxCount, Collection<? extends DXBlock> dxBlockList){
        DXBox dxBox = new DXBox();
        dxBox.setDXControl(createDXControl(packMaxCount));
        dxBox.getDXPackOrDXReceiptOrDXFileRequest().addAll(dxBlockList);
        return new ObjectFactory().createDXBox(dxBox);
    }

    private DXControl createDXControl(int packMaxCount){
        //DXControl
        DXControl dxControl = new DXControl();
        dxControl.setId(UUID.randomUUID().toString());
        dxControl.setTimeSent(new DateTime());
        dxControl.setDirection(new Direction());
        dxControl.getDirection().setProtocol("общее_0.5");
        dxControl.getDirection().setRecipient(createRedAddress("ФССП", "МВВ"));
        dxControl.getDirection().setSender(createRedAddress("УГИБДДМСК", null));//убран код подразделения согласно письму от 20.07.2015
        //Pack
        dxControl.setPack(createDXControlPack(packMaxCount));
        return dxControl;
    }

    private DXControl.Pack createDXControlPack(int packMaxCount){
        DXControl.Pack pack = new DXControl.Pack();
        pack.setMaxCount(packMaxCount);
        return pack;
    }

    @Override
    public Header createSMEVHeader(){
        HeaderType header = new HeaderType();
        header.setMessageId(UUID.randomUUID().toString());
        header.setTimeStamp(new DateTime());
        header.setMessageClass(MessageClassType.REQUEST);
        header.setNodeId(UUID.randomUUID().toString());

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HeaderType.class);
            return Headers.create(jaxbContext, new ru.gosuslugi.smev.rev120315.ObjectFactory().createHeader(header));
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DXPack createDXPack(String packId, String ogaiCode, String fsspCode) {
        DXPack pack = new DXPack();
        pack.setId(packId);
        pack.setTimeSent(new DateTime());
        pack.setDirection(new Direction());
        pack.getDirection().setSender(createRedAddress("УГИБДДМСК", ogaiCode));
        pack.getDirection().setRecipient(createRedAddress("ФССП", fsspCode));
        pack.getDirection().setProtocol("общее_0.5");
        return pack;
    }

    @Override
    public void addExecutiveDocumentToDXPack(DXPack pack, ExtendedExtDoc doc) {
        Envelope env = new Envelope();
        env.setId(doc.getEnvelopeId());
        env.setDocument(createDocument(doc));
        env.setDocId(doc.getExternalKey());
        pack.setEnvelopes(new DXPack.Envelopes());
        pack.getEnvelopes().getEnvelope().add(env);
    }

    private DocumentContainer createDocument(ExtDoc document) {
        DocumentContainer doc = new DocumentContainer();
        JAXBElement<ExtDoc> extDoc = new biz.red_soft.schemas.fssp.common._2011._0.ObjectFactory().createExtDoc(document);
        doc.setAny(extDoc);
        return doc;
    }

    private RedAddress createRedAddress(String organization, String department){
        RedAddress redAddress = new RedAddress();
        redAddress.setOrg(organization);
        redAddress.setDept(department);
        return redAddress;
    }

}
