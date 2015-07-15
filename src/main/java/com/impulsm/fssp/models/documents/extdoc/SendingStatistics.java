package com.impulsm.fssp.models.documents.extdoc;

/**
 * Created by vinichenkosa on 10/07/15.
 */
public final class SendingStatistics {

    private final int totaltoSend;
    private final int wasSent;

    public SendingStatistics(int totaltoSend, int wasSent) {
        this.totaltoSend = totaltoSend;
        this.wasSent = wasSent;
    }

    public int getTotaltoSend() {
        return totaltoSend;
    }

    public int getWasSent() {
        return wasSent;
    }

    public final boolean isAllDocsSended(){
        return totaltoSend == wasSent;
    }
}
