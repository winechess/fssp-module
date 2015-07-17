package com.impulsm.fssp.logic.impl.documents.executive;


import biz.red_soft.schemas.fssp.common._2011._0.Data;
import biz.red_soft.schemas.fssp.common._2011._0.FioType;
import biz.red_soft.schemas.fssp.common._2011._0.PaymentProperties;
import biz.red_soft.schemas.fssp.common._2011._0.TransportDataType;
import com.impulsm.database.datasource.IDataSource;
import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentBaseFacade;
import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentFacade;
import com.impulsm.fssp.models.documents.extdoc.ExtDocCursor;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.impulsm.fssp.utils.api.IFSSPSignatureUtil;
import com.impulsm.fssp.utils.api.IXMLSerializer;
import com.impulsm.utils.datetime.DateTimeUtil;
import oracle.jdbc.OracleTypes;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fssprus.namespace.id._2013._3.IIdType;
import ru.fssprus.namespace.id._2013._3.ObjectFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vinichenkosa on 08/07/15.
 */
@RequestScoped
public class ExecutiveDocumentFacadeImpl implements IExecutiveDocumentFacade {

    @Inject
    IDataSource ds;
    @Inject
    DateTimeUtil dtUtil;
    @Inject
    IExecutiveDocumentBaseFacade extDocBaseFacade;
    @Inject
    IXMLSerializer xmlSerializer;
    @Inject
    IFSSPSignatureUtil signatureUtil;

    @Override
    public ExtendedExtDoc getNextExecutiveDocumentFromCursor(ExtDocCursor cursor) throws Exception {

        if (cursor == null) {
            throw new IllegalArgumentException("Курсор не передан.");
        }

        if (!cursor.next()) {
            return null;
        }
        return generateDocFromCursor(cursor.getCursor());
    }

    @Override
    public ExtDocCursor getCursorWithExecutiveDocumentsForRegistry(BigDecimal registryId) throws Exception {
        ExtDocCursor cursor = new ExtDocCursor();
        try {
            Connection connection = ds.getConnection();
            cursor.setConnection(connection);
            CallableStatement cs = connection.prepareCall(GET_ISP_DOCS);
            cursor.setStatement(cs);
            cs.setBigDecimal(1, registryId);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            cursor.setCursor((ResultSet) cs.getObject(2));
            return cursor;
        }catch(Exception ex){
            logger.error("Ошибка получения курсора с исполнительными документами.");
            cursor.close();
            return null;
        }
    }


    private ExtendedExtDoc generateDocFromCursor(ResultSet cursor) throws Exception {
        ExtendedExtDoc edoc = new ExtendedExtDoc();

        edoc.setFsspCode(cursor.getString("ssp_id"));
        edoc.setOgaiCode(cursor.getBigDecimal("OrganCode").toString());
        edoc.setSendingId(cursor.getString("sending_id"));
        edoc.setEnvelopeId(cursor.getString("envelope_id"));
        edoc.setPackId(cursor.getString("pack_id"));
        //ExternalKey
        edoc.setExternalKey(cursor.getString("ExternalKey"));
        //DocDate
        edoc.setDocDate(dtUtil.toJodaLocalDate(cursor.getDate("DocDate")));
        //BarCode
        edoc.setBarCode(cursor.getString("BarCode"));
        //IDType
        edoc.setIDType(cursor.getBigDecimal("IDType").toString());
        //IDNum
        edoc.setIDNum(cursor.getString("IDNum"));
        //IDDocDate
        edoc.setIDDate(dtUtil.toJodaLocalDate(cursor.getDate("IDDocDate")));
        //DeloNum
        edoc.setDeloNum(cursor.getString("DeloNum"));
        //IDDesDate
        edoc.setDeloDate(dtUtil.toJodaLocalDate(cursor.getDate("IDDesDate")));
        //AktDate
        edoc.setAktDate(dtUtil.toJodaLocalDate(cursor.getDate("AktDate")));
        //DebtorEstate
        edoc.setDebtorEstate(cursor.getString("DebtorEstate"));
        //OrganCode
        edoc.setOrganCode("22");//cursor.getBigDecimal("OrganCode").toString());
        //Organ
        edoc.setOrgan(cursor.getString("Organ"));
        //OrganAdr
        edoc.setOrganAdr(cursor.getString("OrganAdr"));
        //OrganSign
        edoc.setOrganSignPost(cursor.getString("OrganSign"));
        //OrganSignFIO
        edoc.setOrganSignFIO(cursor.getString("OrganSignFIO"));
        //IdSubjCode
        edoc.setIDSubj("37");
        //IdSubjName
        edoc.setIDSubjName(cursor.getString("IDSubjName"));
        //IDSum
        edoc.setIDSum(cursor.getBigDecimal("RekvSum"));
        //ClaimerType
        edoc.setClaimerType("22");//Так сказали в ФССП
        //ClaimerName
        edoc.setClaimerName(cursor.getString("ClaimerName"));
        //ClaimerAdr
        edoc.setClaimerAdr(cursor.getString("ClaimerAdr"));
        //DebtorType
        edoc.setDebtorType(cursor.getString("DebtorType"));
        //DebtorName
        edoc.setDebtorName(cursor.getString("DebtorName").trim());
        //DebtorAdr
        edoc.setDebtorAdr(cursor.getString("DebtorAdr"));

        if (edoc.getDebtorType().equals("2")) {
            //DebtorFIO
            FioType fio = new FioType();
            fio.setFirstName(cursor.getString("FirstName"));
            fio.setSurname(cursor.getString("Surname"));
            fio.setPatronymic(cursor.getString("Patronymic"));
            edoc.setDebtorFIO(fio);
            //DebtorBirthDate
            edoc.setDebtorBirthDate(dtUtil.toJodaLocalDate(cursor.getDate("DebtorBirthDate")));
            //DebtorBirthYear
            edoc.setDebtorBirthYear(dtUtil.toJodaLocalDate(cursor.getDate("DebtorBirthDate")));
            //DebtorBirthPlace
            edoc.setDebtorBirthPlace(cursor.getString("DebtorBirthPlace"));
        } else if (edoc.getDebtorType().equals("1")) {
            //DebtorINN
            edoc.setDebtorINN(cursor.getString("DebtorINN"));
            //DebtorKPP
            edoc.setDebtorKPP(cursor.getString("DebtorKPP"));
            //DebtorRegDate
            Date date = cursor.getDate("DebtorRegDate");
            if (date == null) {
                edoc.setDebtorRegDate(new LocalDate());
            } else {
                edoc.setDebtorRegDate(dtUtil.toJodaLocalDate(date));
            }

            //DebtorOGRN
            edoc.setDebtorOGRN(cursor.getString("DebtorOGRN"));
        } else if (edoc.getDebtorType().equals("3")) {
            //DebtorRegPlace
        }
        //SrokDobrPFR
        edoc.setSrokDobrPFR(new LocalDate());

        HashMap<String, String> hm = parseStotv(cursor.getString("stotv"));
        //KoAPArticle
        edoc.setKoAPArticle(hm.get("article"));
        //KoAPPart
        edoc.setKoAPPart(hm.get("part"));
        //KoAPPoint
        edoc.setKoAPPoint(hm.get("point"));
        //PaymentProperties
        edoc.getPaymentProperties().add(createPaymentProperties(cursor));
        //Создание объекта основания для ИД
        IIdType documentBase = extDocBaseFacade.getExecutiveDocumentBase(cursor);
        //Добавляем документ-основание в ИД
        byte[] idBase = xmlSerializer.serializeToStreamedXML(new ObjectFactory().createIId(documentBase));
        edoc.setIDBase(idBase);
        edoc.setSignatureBase(signatureUtil.pkcs7sign(idBase));
        //Заполняем сведения о ТС
        edoc.getData().add(createTransportData(cursor));
        //Возвращаем ИД
        return edoc;

    }

    private Data createTransportData(ResultSet rs) throws SQLException, DatatypeConfigurationException {
        Data data = new Data();

        TransportDataType transport = new TransportDataType();

        transport.setAutomType(rs.getString("AutomType"));
        transport.setProducer(rs.getString("Producer"));
        transport.setModel(rs.getString("Model"));
        transport.setColor(StringUtils.substring(rs.getString("Color"), 0, 20));
        transport.setRegNo(rs.getString("RegNo"));
        transport.setVIN(rs.getString("Vin"));
        transport.setEngine(rs.getString("Engine"));

        try {
            Integer madeYear = Integer.valueOf(rs.getString("MadeYear"));
            transport.setMadeYear(new LocalDate(madeYear, 1, 1));
        } catch (NumberFormatException ex) {
            logger.warn("Не указана дата регистрации ТС.");
        }

        transport.setActDate(dtUtil.toJodaLocalDate(rs.getDate("AktDate")));
        transport.setDeptCode("1145000");
        transport.setKindData("04");

        data.setTransportData(transport);

        return data;
    }

    private PaymentProperties createPaymentProperties(ResultSet cursor) throws Exception {

        //PaymentProperties
        PaymentProperties pp = new PaymentProperties();
        //RecpName
        pp.setRecpName(cursor.getString("RecpName"));
        //DebtorRegPlace
        String okato = cursor.getString("Okato");
        if (StringUtils.length(okato) != 11) {
            okato = null;
        }
        pp.setOkato(okato);
        //pp.setOkato("45286585000");
        pp.setOktmo(cursor.getString("RecpOktmo"));
        //RecpINN
        pp.setRecpINN(cursor.getString("RecpINN"));
        //RecpKPP
        pp.setRecpKPP(cursor.getString("RecpKPP"));
        //RecpCnt
        pp.setRecpCnt(cursor.getString("RecpCnt"));
        //RecpBank
        pp.setRecpBank(cursor.getString("RecpBank"));
        //RecpBIK
        pp.setRecpBIK(cursor.getString("RecpBIK"));
        //RekvSum
        pp.setRekvSum(cursor.getBigDecimal("RekvSum"));
        //Kbk
        pp.setKbk(cursor.getString("Kbk"));
        //PokPl
        pp.setPokPl(cursor.getString("PokPl"));
        //UNIFOCode
        pp.setUNIFOCode(cursor.getString("UNIFOCode"));
        return pp;

    }

    private HashMap<String, String> parseStotv(String s) {
        //System.out.println("Parse stotv: " + s);
        Pattern pattern = Pattern.compile("(п[\\s.]+([\\d.]+))?[\\s]*(ч[\\s.]+([\\d.]+))?[\\s]*(пр[\\s.]+([\\d.]+))?[\\s]*(ст[\\s.]+([\\d.]+))?[\\s]*(пр[\\s.]+([\\d.]+))?[\\s]*");
        HashMap<String, String> hm = new HashMap<>();
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {

            StringBuilder sb = new StringBuilder("Parsed stotv: ");

            String point = matcher.group(2);
            if (point != null && !point.trim().isEmpty()) {
                sb.append("п.").append(point).append(" ");
                hm.put("point", point.trim());
            }

            String part = matcher.group(4);
            if (part != null && !part.trim().isEmpty()) {
                sb.append("ч.").append(part).append(" ");
                hm.put("part", part.trim());
            }

            String article = matcher.group(8);
            if (article != null && !article.trim().isEmpty()) {
                sb.append("ст.").append(article).append(" ");
                hm.put("article", article.trim());
            }
        }

        return hm;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
