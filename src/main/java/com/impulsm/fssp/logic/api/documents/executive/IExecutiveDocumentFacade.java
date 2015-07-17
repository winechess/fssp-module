package com.impulsm.fssp.logic.api.documents.executive;

import com.impulsm.fssp.models.documents.extdoc.ExtDocCursor;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;

import java.math.BigDecimal;
import java.sql.ResultSet;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public interface IExecutiveDocumentFacade {

    static final String GET_ISP_DOCS = "{call gibdd_apr.apr_registry_ssp.get_isp_docs(?,?)}";

    ExtendedExtDoc getNextExecutiveDocumentFromCursor(ExtDocCursor cursor) throws Exception;

    ExtDocCursor getCursorWithExecutiveDocumentsForRegistry(BigDecimal registryId) throws Exception;

}
