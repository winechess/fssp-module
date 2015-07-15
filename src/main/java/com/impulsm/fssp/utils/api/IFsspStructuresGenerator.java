package com.impulsm.fssp.utils.api;

import biz.red_soft.ncore.dx._1.DXBlock;
import biz.red_soft.ncore.dx._1.DXPack;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.sun.xml.ws.api.message.Header;
import ru.gosuslugi.smev.rev120315.BaseMessageType;

import javax.xml.ws.Holder;
import java.util.Collection;

/**
 * Created by vinichenkosa on 07/07/15.
 */
public interface IFsspStructuresGenerator {

    Holder<BaseMessageType> generateRequestMessage(int packMaxCount, Collection<? extends DXBlock> dxBlockList);

    Header createSMEVHeader();

    DXPack createDXPack(String packId, String ogaiCode, String fsspCode);

    void addExecutiveDocumentToDXPack(DXPack pack, ExtendedExtDoc doc);
}
