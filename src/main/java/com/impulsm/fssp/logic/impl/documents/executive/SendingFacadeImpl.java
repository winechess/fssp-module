package com.impulsm.fssp.logic.impl.documents.executive;

import biz.red_soft.ncore.dx._1.DXReceipt;
import biz.red_soft.ncore.dx._1.DocNote;
import biz.red_soft.ncore.dx._1.MessageEx;
import biz.red_soft.ncore.dx._1.ProcessResult;
import com.impulsm.database.datasource.IDataSource;
import com.impulsm.fssp.logic.api.documents.executive.ISendingFacade;
import com.impulsm.fssp.utils.api.IFsspUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * Created by vinichenkosa on 10/07/15.
 */
public class SendingFacadeImpl implements ISendingFacade {

    @Inject
    IDataSource ds;
    @Inject
    IFsspUtils fsspUtils;

    @Override
    public void setSendingResultForDocument(DXReceipt receipt) {
        String sendingId = fsspUtils.toSendingId(receipt.getReplyTo());
        setSendingResultForDocument(sendingId, receipt.getErrorMessage(), receipt.getStatus());
    }

    @Override
    public void setSendingResultForDocument(String sendingId, DocNote note) {
        setSendingResultForDocument(sendingId, note.getMessage(), note.getStatus());
    }

    public void setSendingResultForDocument(String sendingId, MessageEx error, ProcessResult result){

        try (Connection con = ds.getConnection();
             CallableStatement cs = con.prepareCall(MARK_SENDING_UNLOAD_RESULT)) {

            cs.setString(1, sendingId);
            cs.setString(2, error.getValue());
            cs.setInt(3, fsspUtils.getStatusFromProcessResult(result));
            cs.setString(4, error.getCode());
            cs.execute();

        } catch (Exception ex) {
            logger.error("Не удалось отметить резульат отправки для отправления {}.", sendingId, ex);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
