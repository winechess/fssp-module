package com.impulsm.fssp.logic.impl.documents.executive;

import biz.red_soft.ncore.dx._1.*;
import com.impulsm.database.datasource.IDataSource;
import com.impulsm.fssp.logic.api.documents.executive.IPackFacade;
import com.impulsm.fssp.logic.api.documents.executive.ISendingFacade;
import com.impulsm.fssp.utils.api.IFsspUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Collection;

/**
 * Created by vinichenkosa on 10/07/15.
 */
@RequestScoped
public class PackFacadeImpl implements IPackFacade {

    @Inject
    IDataSource ds;
    @Inject
    IFsspUtils fsspUtils;
    @Inject
    ISendingFacade sendingFacade;

    @Override
    public void setSendingResultForPack(DXReceipt receipt) {
        setSendingResultForPack(receipt.getReplyTo(), receipt.getErrorMessage(), receipt.getStatus());
    }

    @Override
    public void setSendingResultForPacks(Collection<? extends DXPack> packs, MessageEx error, ProcessResult result) {
        for(DXPack pack: packs){
            setSendingResultForPack(pack.getId(), error, result);
        }
    }


    private void setSendingResultForPack(String packId, MessageEx error, ProcessResult result){

        if(fsspUtils.isOldPackId(packId)){
            sendingFacade.setSendingResultForDocument(fsspUtils.toSendingId(packId), error, ProcessResult.FAIL);
            return;
        }

        try (Connection con = ds.getConnection();
             CallableStatement cs = con.prepareCall(MARK_COVER_UNLOAD_RESULT)) {

            cs.setBigDecimal(1, fsspUtils.toCoverId(packId));
            cs.setInt(2, fsspUtils.getStatusFromProcessResult(result));
            cs.setString(3, error.getValue());
            cs.setString(4, error.getCode());
            cs.execute();
        } catch (Exception ex) {
            logger.error("Не удалось отметить резульат отправки для пакета {}.", packId, ex);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
