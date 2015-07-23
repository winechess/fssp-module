package com.impulsm.fssp.utils.impl;

import biz.red_soft.ncore.dx._1.DXBlock;
import biz.red_soft.ncore.dx._1.DXBox;
import biz.red_soft.ncore.dx._1.ProcessResult;
import com.impulsm.fssp.models.documents.extdoc.NPAModel;
import com.impulsm.fssp.utils.api.IFsspUtils;
import ru.gosuslugi.smev.rev120315.AppDataType;
import ru.gosuslugi.smev.rev120315.BaseMessageType;
import ru.gosuslugi.smev.rev120315.MessageDataType;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public class FsspUtilsImpl implements IFsspUtils {

    @Override
    public List<DXBlock> extractDXBlockListFromBaseMessageType(BaseMessageType response) {
        if (response == null) {
            throw new IllegalArgumentException("Ответ от сервиса ФССП равен null");
        }

        MessageDataType data = response.getMessageData();
        if (data == null) {
            throw new IllegalArgumentException("Не найден элемент MessageData в ответе от сервиса ФССП.");
        }

        AppDataType appData = data.getAppData();
        if (appData == null) {
            throw new IllegalArgumentException("Не найден элемент AppData в ответе от сервиса ФССП.");
        }

        if (appData.getAny().isEmpty()) {
            throw new IllegalArgumentException("Не найдено ни одного элемента в AppData.");
        }

        JAXBElement<DXBox> element = (JAXBElement<DXBox>) appData.getAny().get(0);
        return element.getValue().getDXPackOrDXReceiptOrDXFileRequest();
    }

    @Override
    public BigDecimal toCoverId(String packId) {

        if (packId == null) {
            throw new IllegalArgumentException("Параметр не может быть null.");
        }

        if (isOldPackId(packId)) {
            throw new IllegalArgumentException(
                    "В качестве аргумента был передан ид-р пакета в старом формате. " + packId
            );
        }

        String[] array = packId.split("#");

        if (array.length == 2) {
            return new BigDecimal(array[0]);
        } else {
            throw new IllegalArgumentException("Неверный формат идентификатора пакета." + packId);
        }
    }

    @Override
    public String toSendingId(String oldPackIdOrEnvelopeId) {
        if(isOldPackId(oldPackIdOrEnvelopeId)){
            return oldPackIdToSendingId(oldPackIdOrEnvelopeId);
        }else{
            return envelopeIdToSendingId(oldPackIdOrEnvelopeId);
        }
    }

    @Override
    public int getStatusFromProcessResult(ProcessResult result) {
        switch (result) {
            case WAIT:
                return 4;
            case FAIL:
                return 3;
            case SUCCESS:
                return 2;
            default:
                throw new IllegalArgumentException("Статус не может быть " + result.name());
        }
    }

    @Override
    public boolean isOldPackId(String packId) {
        if (packId == null) {
            throw new IllegalArgumentException("Параметр не может быть null.");
        }
        return packId.startsWith("#");
    }

    @Override
    public NPAModel parseStotv(String s) {
        NPAModel npa = new NPAModel();

        Pattern pattern = Pattern.compile("(п[\\s.]+([\\d.]+))?[\\s]*(ч[\\s.]+([\\d.]+))?[\\s]*(пр[\\s.]+([\\d.]+))?[\\s]*(ст[\\s.]+([\\d.]+))?[\\s]*(пр[\\s.]+([\\d.]+))?[\\s]*");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {


            String point = matcher.group(2);
            if (point != null && !point.trim().isEmpty()) {
                npa.setPoint(point.trim());
            }

            String part = matcher.group(4);
            if (part != null && !part.trim().isEmpty()) {
                npa.setPart(part.trim());
            }

            String article = matcher.group(8);
            if (article != null && !article.trim().isEmpty()) {
                npa.setArticle(article.trim());
            }
        }

        return npa;
    }

    private String oldPackIdToSendingId(String oldPackId) {
        return oldPackId.split("#")[1];
    }

    private String envelopeIdToSendingId(String envelopeId){
        return envelopeId.split("#")[0];
    }
}
