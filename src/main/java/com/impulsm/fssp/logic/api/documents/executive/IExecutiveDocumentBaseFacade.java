package com.impulsm.fssp.logic.api.documents.executive;

import ru.fssprus.namespace.id._2013._3.IIdType;

import java.sql.ResultSet;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public interface IExecutiveDocumentBaseFacade {

    IIdType getExecutiveDocumentBase(ResultSet cursor) throws Exception;

}
