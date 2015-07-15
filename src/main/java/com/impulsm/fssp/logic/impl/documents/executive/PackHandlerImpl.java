package com.impulsm.fssp.logic.impl.documents.executive;

import biz.red_soft.ncore.dx._1.*;
import biz.red_soft.ncore.dx._1_1.smev25.ws.Error;
import com.impulsm.fssp.config.ApplicationConfig;
import com.impulsm.fssp.logic.api.documents.executive.IPackFacade;
import com.impulsm.fssp.logic.api.documents.executive.IPackHandler;
import com.impulsm.fssp.logic.api.documents.executive.ISendingFacade;
import com.impulsm.fssp.logic.api.service.IFsspClient;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;
import com.impulsm.fssp.utils.api.IFsspStructuresGenerator;
import com.impulsm.fssp.utils.api.IFsspUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vinichenkosa on 10/07/15.
 */
public class PackHandlerImpl implements IPackHandler {

    @Inject
    IFsspStructuresGenerator generator;
    @Inject
    IFsspClient client;
    @Inject
    IPackFacade packFacade;
    @Inject
    ISendingFacade sendingFacade;
    @Inject
    IFsspUtils fsspUtils;

    private Map<String, DXPack> packs = new HashMap<>();
    private int totalDocsToSend = 0;
    private int wasSent = 0;

    @Override
    public void put(ExtendedExtDoc doc) {

        DXPack dxPack = packs.get(doc.getPackId());

        if (dxPack == null) {
            dxPack = addNewPackToMap(doc);
        }
        //Добавяем документ в пакет
        generator.addExecutiveDocumentToDXPack(dxPack, doc);
        totalDocsToSend++;
    }

    @Override
    public void sendPacks() {
        if (!packs.values().isEmpty()) {
            try {
                List<DXBlock> dxBlocks = client.sendExecutiveDocuments(packs.values());
                parseResponse(dxBlocks);
            } catch (Error error) {
                packFacade.setSendingResultForPacks(packs.values(), error.getFaultInfo(), ProcessResult.FAIL);
                logger.error("Не удалось отправить пакеты {}.", packs.values().toString(), error);
            }
        }
    }

    @Override
    public SendingStatistics getStatistics() {
        return new SendingStatistics(totalDocsToSend, wasSent);
    }


    private DXPack addNewPackToMap(ExtendedExtDoc doc) {

        if (packs.size() == MAX_DXPACKS_PER_REQUEST) {
            sendPacks();
            packs.clear();
        }
        DXPack dxPack;
        switch(ApplicationConfig.PROJECT_STAGE){
            case PRODUCTION:
                dxPack = generator.createDXPack(doc.getPackId(), doc.getOgaiCode(), doc.getFsspCode());
                break;
            default:
                dxPack = generator.createDXPack(doc.getPackId(), doc.getOgaiCode(), "33021");
                break;
        }
        packs.put(doc.getPackId(), dxPack);
        return dxPack;
    }

    private void parseResponse(List<? extends DXBlock> blocks) {

        if (blocks == null || blocks.isEmpty()) {
            throw new IllegalArgumentException("Список квитанци в ответе не может быть пустым.");
        }

        for (DXBlock block : blocks) {
            parseBlock(block);
        }
    }

    private void parseBlock(DXBlock block) {

        if (block.getClass() == DXReceipt.class) {
            parseDXReceipt((DXReceipt) block);
        }
    }

    private void parseDXReceipt(DXReceipt receipt) {

        String packId = receipt.getReplyTo();
        //Получаем отметки об отправке для каждого документа в пачке
        DocNoteCollection notes = receipt.getNotes();

        if (notes == null || notes.getNote().isEmpty()) {
            markDXPackNotSent(receipt);
        } else {
            parseNotes(packId, notes.getNote());
        }
    }

    private void markDXPackNotSent(DXReceipt receipt) {
        packFacade.setSendingResultForPack(receipt);
    }

    private void parseNotes(String packId, List<DocNote> notes) {
        for (DocNote note : notes) {
            parseNote(packId, note);
        }
    }

    private void parseNote(String packId, DocNote note) {

        String sendingId = fsspUtils.toSendingId(note.getDocRef() != null ? note.getDocRef().getEnvId() : packId);
        sendingFacade.setSendingResultForDocument(sendingId, note);
        if (note.getStatus() == ProcessResult.SUCCESS) {
            wasSent++;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}


