package com.impulsm.fssp.logic.api.documents.executive;

import biz.red_soft.ncore.dx._1.DXPack;
import biz.red_soft.ncore.dx._1.DXReceipt;
import biz.red_soft.ncore.dx._1.MessageEx;
import biz.red_soft.ncore.dx._1.ProcessResult;

import java.util.Collection;

/**
 * Created by vinichenkosa on 10/07/15.
 */
public interface IPackFacade {

    static final String MARK_COVER_UNLOAD_RESULT = "{call gibdd_apr.apr_registry_ep.mark_cover_unload_result (?,?,?,?)}";

    void setSendingResultForPack(DXReceipt receipt);

    void setSendingResultForPacks(Collection<? extends DXPack> packs, MessageEx error, ProcessResult result);

}
