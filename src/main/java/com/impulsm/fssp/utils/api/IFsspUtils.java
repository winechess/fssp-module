package com.impulsm.fssp.utils.api;

import biz.red_soft.ncore.dx._1.DXBlock;
import biz.red_soft.ncore.dx._1.ProcessResult;
import ru.gosuslugi.smev.rev120315.BaseMessageType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public interface IFsspUtils {

    List<DXBlock> extractDXBlockListFromBaseMessageType(BaseMessageType response);

    boolean isOldPackId(String packId);

    BigDecimal toCoverId(String packId);

    String toSendingId(String oldPackIdOrEnvelopeId);

    int getStatusFromProcessResult(ProcessResult result);


}
