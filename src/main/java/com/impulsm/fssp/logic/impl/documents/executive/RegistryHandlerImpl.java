package com.impulsm.fssp.logic.impl.documents.executive;

import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentsHandler;
import com.impulsm.fssp.logic.api.documents.executive.IRegistryFacade;
import com.impulsm.fssp.logic.api.documents.executive.IRegistryHandler;
import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vinichenkosa on 09/07/15.
 */
@RequestScoped
public class RegistryHandlerImpl implements IRegistryHandler {

    @Inject
    IRegistryFacade registryFacade;
    @Inject
    IExecutiveDocumentsHandler executiveDocumentsHandler;

    @Override
    public void handleAllRegistries() throws Exception {
        List<BigDecimal> registriesIdsForSending = registryFacade.getRegistriesIdsForSending();
        for (BigDecimal registryId : registriesIdsForSending) {
            handleRegistry(registryId);
        }
    }

    private void handleRegistry(BigDecimal registryId) {

        SendingStatistics statistics = new SendingStatistics(0, 0);
        BigDecimal unloadId = registryFacade.initRegistryUnload(registryId);
        if (unloadId != null) {
            try {
                statistics = executiveDocumentsHandler.sendAllExecutiveDocumentsForRegistry(registryId);
            } catch (Exception ex) {
                logger.error("Непредвиденная ошибка при попытке отправить реест {} в ФССП.", registryId, ex);
            } finally {
                registryFacade.finalizeRegistryUnload(registryId, unloadId, statistics);
            }
        }
        logger.debug("Выгрузка реестра {} звершена. Отправлено: {} из {} постановлений.",
                registryId, statistics.getWasSent(), statistics.getTotaltoSend());
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
