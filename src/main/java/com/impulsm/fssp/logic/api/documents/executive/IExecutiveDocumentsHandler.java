package com.impulsm.fssp.logic.api.documents.executive;

import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;

import java.math.BigDecimal;

/**
 * Created by vinichenkosa on 09/07/15.
 */
public interface IExecutiveDocumentsHandler {

    SendingStatistics sendAllExecutiveDocumentsForRegistry(BigDecimal registryId);

}
