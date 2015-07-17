package com.impulsm.fssp.logic.impl.service;

import biz.red_soft.ncore.dx._1.DXBlock;
import biz.red_soft.ncore.dx._1.DXPack;
import biz.red_soft.ncore.dx._1_1.smev25.ws.Error;
import biz.red_soft.ncore.dx._1_1.smev25.ws.NCoreDxSmev25Port;
import biz.red_soft.ncore.dx._1_1.smev25.ws.NCoreDxSmev25Service;
import com.impulsm.fssp.handlers.ExtDocHandler;
import com.impulsm.fssp.handlers.SecurityHandler;
import com.impulsm.fssp.logic.api.service.IFsspClient;
import com.impulsm.fssp.utils.api.IFsspStructuresGenerator;
import com.impulsm.fssp.utils.api.IFsspUtils;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.client.HttpTransportPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gosuslugi.smev.rev120315.BaseMessageType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by vinichenkosa on 07/07/15.
 */
@RequestScoped
public class FsspClientImpl implements IFsspClient {

    @Inject
    IFsspStructuresGenerator structureGenerator;
    @Inject
    IFsspUtils fsspUtils;

    NCoreDxSmev25Port port;

    public FsspClientImpl() {
        logger.info("Constructor was called.");
        HttpTransportPipe.dump = true;
        HttpAdapter.dump_threshold = 8192;
        port = new NCoreDxSmev25Service().getNCoreDxSmev25Port();
        List<Handler> handlerChain = new ArrayList<>();
        handlerChain.add(new ExtDocHandler());
        handlerChain.add(new SecurityHandler());
        ((BindingProvider) port).getBinding().setHandlerChain(handlerChain);
    }


    @Override
    public List<DXPack> requestDocumentsAndStatuses() {

        try {
            BaseMessageType response = send(5, Collections.EMPTY_LIST);

            List<DXBlock> dxBlockList = fsspUtils.extractDXBlockListFromBaseMessageType(response);

            if (dxBlockList == null || dxBlockList.isEmpty()) {
                logger.info("Нет материалов для загрузки из ФССП. Выходим...");
                return Collections.EMPTY_LIST;
            }

            List<DXPack> packs = (List<DXPack>) (List<?>) dxBlockList;
            logger.info("Получено {} пакетов.", packs.size());
            return packs;

        } catch (Error error) {
            logger.error("Ошибка полчения статусов и документов из фссп.");
            return Collections.EMPTY_LIST;
        }

    }

    @Override
    public List<DXBlock> sendExecutiveDocuments(Collection<? extends DXBlock> blocks) throws Error {
        BaseMessageType response = send(0, blocks);
        return fsspUtils.extractDXBlockListFromBaseMessageType(response);
    }

    @Override
    public List<DXBlock> sendRecallDocuments(Collection<? extends DXBlock> blocks) throws Error{
        return this.sendExecutiveDocuments(blocks);
    }

    private BaseMessageType send(int packMaxCount, Collection<? extends DXBlock> dxBlockList) throws Error {

        Holder<BaseMessageType> baseMessageTypeHolder = structureGenerator.generateRequestMessage(packMaxCount, dxBlockList);
        ((WSBindingProvider) port).setOutboundHeaders(structureGenerator.createSMEVHeader());
        port.dx(baseMessageTypeHolder);
        return baseMessageTypeHolder.value;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
