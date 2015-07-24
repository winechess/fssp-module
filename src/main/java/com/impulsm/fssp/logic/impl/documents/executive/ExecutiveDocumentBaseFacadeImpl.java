package com.impulsm.fssp.logic.impl.documents.executive;

import biz.red_soft.schemas.fssp.common._2011._0.FioType;
import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentBaseFacade;
import com.impulsm.fssp.models.documents.extdoc.NPAModel;
import com.impulsm.fssp.utils.api.IFsspUtils;
import com.impulsm.fssp.utils.api.IXMLSerializer;
import com.impulsm.utils.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fssprus.namespace.id._2013._3.*;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public class ExecutiveDocumentBaseFacadeImpl implements IExecutiveDocumentBaseFacade {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private DateTimeUtil dtUtil;
    @Inject
    private IXMLSerializer ixmlSerializer;
    @Inject
    private IFsspUtils fsspUtils;

    @Override
    public IIdType getExecutiveDocumentBase(ResultSet cursor) throws Exception {
        try {
            IIdType extDocBase = new IIdType();

            //ExternalKey
            String extKey = cursor.getString("ExternalKey");
            extDocBase.setExternalKey(extKey);
            extDocBase.setBarCode(cursor.getString("BarCode"));
            extDocBase.setIDType(cursor.getBigDecimal("IDType"));
            extDocBase.setIDNum(cursor.getString("IDNum"));
            extDocBase.setIDDocDate(dtUtil.toJodaLocalDate(cursor.getDate("IDDocDate")));
            extDocBase.setDeloNum(cursor.getString("DeloNum"));
            extDocBase.setDeloPlace(cursor.getString("DeloPlace"));
            extDocBase.setIDDesDate(dtUtil.toJodaLocalDate(cursor.getDate("IDDesDate")));
            extDocBase.setAktDate(dtUtil.toJodaLocalDate(cursor.getDate("AktDate")));
            int srokPrIsp = cursor.getInt("SrokPrIsp");
            if (!cursor.wasNull()) {
                extDocBase.setSrokPrIsp(srokPrIsp);
            }
            extDocBase.setSrokPrIspType(cursor.getString("SrokPrIspType"));
            extDocBase.setSrokDobr(new LocalDate());
            extDocBase.setIsimmediate(true);
            extDocBase.setSrokAppeal(10);
            extDocBase.setOrderAppeal("КоАП_СТ30.2");
            extDocBase.setAppealPeriodType(cursor.getString("AppealPeriodType"));

            extDocBase.setDebtorEstate(cursor.getString("DebtorEstate"));
            extDocBase.setOrganOKOGU(cursor.getString("OrganOKOGU"));
            extDocBase.setOrganCode(cursor.getString("ogai_kod_canonical"));
            extDocBase.setOrgan(cursor.getString("Organ"));
            extDocBase.setOrganAdr(cursor.getString("OrganAdr"));
            extDocBase.setOrganSignCodePost(cursor.getBigDecimal("OrganSignCodePost"));
            extDocBase.setOrganSign(cursor.getString("OrganSign"));
            extDocBase.setOrganSignFIO(cursor.getString("OrganSignFIO"));
            extDocBase.setIDSubjCode(cursor.getString("IDSubjCode"));
            extDocBase.setIDSubjName(cursor.getString("IDSubjName"));
            extDocBase.setContext(cursor.getString("Context"));
            extDocBase.setIDSum(cursor.getBigDecimal("RekvSum"));
            extDocBase.setIDSumCurrency("643");
            extDocBase.setClaimerName(cursor.getString("ClaimerName"));
            extDocBase.setClaimerINN(cursor.getString("ClaimerINN"));
            extDocBase.setClaimerKPP(cursor.getString("ClaimerKPP"));
            extDocBase.setClaimerOGRN("1046102000570");
            extDocBase.setClaimerAdr(cursor.getString("ClaimerAdr"));
            extDocBase.setDebtorName(cursor.getString("DebtorName").trim());
            extDocBase.setDebtorAdr(cursor.getString("DebtorAdr"));

            String debtorType = cursor.getString("DebtorType");
            switch (debtorType) {
                case "2":
                    //DebtorFIO
                    FioType fio = new FioType();
                    fio.setFirstName(cursor.getString("FirstName"));
                    fio.setSurname(cursor.getString("Surname"));
                    fio.setPatronymic(cursor.getString("Patronymic"));
                    //DebtorBirthDate
                    extDocBase.setDebtorBirthDate(dtUtil.toJodaLocalDate(cursor.getDate("DebtorBirthDate")));
                    //DebtorBirthYear
                    //DebtorBirthPlace
                    extDocBase.setDebtorBirthPlace(cursor.getString("DebtorBirthPlace"));
                    //DebtroSnils
                    //extDocBase.setDebtorSNILS("150-223-667 19");
                    //DebtorGender
                    String gender = cursor.getString("DebtorGender");
                    if (gender.equalsIgnoreCase("ж")) {
                        extDocBase.setDebtorGender("ЖЕНСКИЙ");
                    } else {
                        extDocBase.setDebtorGender("МУЖСКОЙ");
                    }
                    break;
                case "1":
                    //IF URLICO
                    //DebtorINN
                    extDocBase.setDebtorINN(cursor.getString("DebtorINN"));
                    //DebtorKPP
                    String kpp = cursor.getString("DebtorKPP");
                    if (kpp == null) {
                        kpp = "771501001";
                    }
                    extDocBase.setDebtorKPP(kpp);

                    //DebtorRegDate
                    Date drg = cursor.getDate("DebtorRegDate");
                    if (drg == null) {
                        extDocBase.setDebtorRegDate(new LocalDate());
                    } else {
                        extDocBase.setDebtorRegDate(dtUtil.toJodaLocalDate(drg));
                    }

                    //DebtorOGRN
                    extDocBase.setDebtorOGRN(cursor.getString("DebtorOGRN"));
                    //DebtorRegPlace
                    break;
                case "3":
                    break;
            }

            NPAModel stotv = fsspUtils.parseStotv(cursor.getString("stotv"));

            IIdType.NpaActs npaActs = new IIdType.NpaActs();
            NpaActType npaAct = new NpaActType();
            npaAct.setNpaCaption("КоАП РФ");
            npaAct.setNPAType("1");

            if (StringUtils.isBlank(stotv.getArticle())) {
                NpaActType.NpaArticleParts npaArticles = new NpaActType.NpaArticleParts();
                NpaArticleType articleType = new NpaArticleType();
                articleType.setNpaArticle(stotv.getArticle());

                if (StringUtils.isBlank(stotv.getPart())) {
                    NpaArticleType.NpaParts npaParts = new NpaArticleType.NpaParts();
                    NpaPartType npaPart = new NpaPartType();
                    npaPart.setNpaPart(stotv.getPart());

                    if (StringUtils.isBlank(stotv.getPoint())) {
                        NpaPartType.NpaPoints npaPoints = new NpaPartType.NpaPoints();
                        NpaPointType npaPoint = new NpaPointType();
                        npaPoint.setNpaPoint(stotv.getPoint());
                        npaPoints.setNpaPoint(npaPoint);
                        npaPart.getNpaPoints().add(npaPoints);
                    }


                    npaParts.setNpaPart(npaPart);
                    articleType.getNpaParts().add(npaParts);
                }

                npaArticles.setNpaArticle(articleType);
                npaAct.getNpaArticleParts().add(npaArticles);
            }
            npaActs.setNpaAct(npaAct);
            extDocBase.getNpaActs().add(npaActs);
            extDocBase.getDetIdPaymentPropertiesId().add(createPaymentProperties(cursor));
            extDocBase.getDetMvvDatumDocument().add(createMvvDatumTransport(cursor));
            return extDocBase;

        } catch (Exception ex) {
            throw new Exception("Ошибка при попытке сформировать основание для ИД.", ex);
        }
    }

    private IIdType.DetIdPaymentPropertiesId createPaymentProperties(ResultSet cursor) throws SQLException {

        IIdType.DetIdPaymentPropertiesId dipp = new IIdType.DetIdPaymentPropertiesId();

        IdPaymentPropertiesType ipp = new IdPaymentPropertiesType();

        ipp.setRecpName(cursor.getString("RecpName"));
        String okato = cursor.getString("Okato");
        if (StringUtils.length(okato) != 11) {
            okato = null;
        }
        ipp.setOkato(okato);
        //ipp.setOkato("45286585000");
        ipp.setOktmo(cursor.getString("RecpOktmo"));
        ipp.setRecpINN(cursor.getString("RecpINN"));
        ipp.setRecpKPP(cursor.getString("RecpKPP"));
        ipp.setRecpCnt(cursor.getString("RecpCnt"));
        ipp.setRecpBank(cursor.getString("RecpBank"));
        ipp.setRecpBIK(cursor.getString("RecpBIK"));
        ipp.setRekvSum(cursor.getBigDecimal("RekvSum"));
        ipp.setKbk(cursor.getString("Kbk"));
        ipp.setPokPl(cursor.getString("PokPl"));
        ipp.setUNIFOCode(cursor.getString("UNIFOCode"));
        dipp.setIdPaymentProperties(ipp);
        return dipp;
    }

    private IIdType.DetMvvDatumDocument createMvvDatumTransport(ResultSet rs) throws SQLException, DatatypeConfigurationException {

        IIdType.DetMvvDatumDocument document = new IIdType.DetMvvDatumDocument();

        MvvDatumTransportType transport = new MvvDatumTransportType();

        transport.setAutomType(rs.getString("AutomType"));
        transport.setProducer(rs.getString("Producer"));
        transport.setModel(rs.getString("Model"));
        transport.setColor(rs.getString("Color"));
        transport.setRegNo(rs.getString("RegNo"));
        transport.setVin(rs.getString("Vin"));
        transport.setEngine(rs.getString("Engine"));
        try {
            Integer madeYear = Integer.valueOf(rs.getString("MadeYear"));
            transport.setMadeYear(madeYear);
        } catch (NumberFormatException ex) {
            logger.warn("Не указана дата регистрации ТС.");
        }
        transport.setActDate(dtUtil.toJodaLocalDate(rs.getDate("AktDate")));
        transport.setDeptCode("1145000");
        document.setMvvDatumTransport(transport);
        return document;

    }
}
