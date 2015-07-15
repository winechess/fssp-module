package com.impulsm.fssp.logic.api.documents.executive;

import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;

/**
 * Created by vinichenkosa on 10/07/15.
 */
public interface IPackHandler {

    static final int MAX_DXPACKS_PER_REQUEST = 10;

    void put(ExtendedExtDoc doc);

    void sendPacks();

    SendingStatistics getStatistics();

}
