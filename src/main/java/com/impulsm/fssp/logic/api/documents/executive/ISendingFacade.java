package com.impulsm.fssp.logic.api.documents.executive;

import biz.red_soft.ncore.dx._1.DXReceipt;
import biz.red_soft.ncore.dx._1.DocNote;
import biz.red_soft.ncore.dx._1.MessageEx;
import biz.red_soft.ncore.dx._1.ProcessResult;

/**
 * Created by vinichenkosa on 10/07/15.
 */
public interface ISendingFacade {

    final static String MARK_SENDING_UNLOAD_RESULT = "{call gibdd_apr.apr_registry_ep.mark_sending_unload_result(?,?,?,?)}";

    void setSendingResultForDocument(DXReceipt receipt);

    void setSendingResultForDocument(String sendingId, DocNote note);

    void setSendingResultForDocument(String sendingId, MessageEx error, ProcessResult result);

}
