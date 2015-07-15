package com.impulsm.fssp.logic.api.service;

import biz.red_soft.ncore.dx._1.DXBlock;
import biz.red_soft.ncore.dx._1.DXPack;
import biz.red_soft.ncore.dx._1_1.smev25.ws.Error;
import java.util.Collection;
import java.util.List;

/**
 * Created by vinichenkosa on 07/07/15.
 */
public interface IFsspClient {

    List<DXPack> requestDocumentsAndStatuses();

    List<DXBlock> sendExecutiveDocuments(Collection<? extends DXBlock> blocks) throws Error;

    List<DXBlock>  sendRecallDocuments(Collection<? extends DXBlock> blocks) throws Error;

}
