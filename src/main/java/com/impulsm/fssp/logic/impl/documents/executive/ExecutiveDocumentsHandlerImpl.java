package com.impulsm.fssp.logic.impl.documents.executive;

import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentFacade;
import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentsHandler;
import com.impulsm.fssp.logic.api.documents.executive.IPackHandler;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.ResultSet;

/**
 * Created by vinichenkosa on 09/07/15.
 */
@RequestScoped
public class ExecutiveDocumentsHandlerImpl implements IExecutiveDocumentsHandler {

    @Inject
    IExecutiveDocumentFacade executiveDocumentFacade;
    @Inject
    IPackHandler packHandler;

    @Override
    public SendingStatistics sendAllExecutiveDocumentsForRegistry(BigDecimal registryId){

        try (ResultSet cursor = executiveDocumentFacade.getCursorWithExecutiveDocumentsForRegistry(registryId)) {

            ExtendedExtDoc doc;

            while ((doc = executiveDocumentFacade.getNextExecutiveDocumentFromCursor(cursor)) != null) {
                packHandler.put(doc);
            }
            packHandler.sendPacks();

        }catch(Exception ex) {
            logger.error("Ошибка при отправке документов реестра {}", registryId, ex);
        }
        return packHandler.getStatistics();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
