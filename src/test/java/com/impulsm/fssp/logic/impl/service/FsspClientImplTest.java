package com.impulsm.fssp.logic.impl.service;

import biz.red_soft.ncore.dx._1.DXBlock;
import biz.red_soft.ncore.dx._1.DXPack;
import biz.red_soft.ncore.dx._1_1.smev25.ws.Error;
import com.impulsm.fssp.logic.api.service.IFsspClient;
import com.impulsm.fssp.models.documents.extdoc.ExtendedExtDoc;
import com.impulsm.fssp.utils.api.IFsspStructuresGenerator;
import com.impulsm.fssp.utils.impl.FsspStructuresGeneratorImpl;
import com.impulsm.fssp.utils.impl.FsspUtilsImpl;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * Created by vinichenkosa on 07/07/15.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({FsspClientImpl.class, FsspStructuresGeneratorImpl.class, FsspUtilsImpl.class, FsspStructuresGeneratorImpl.class})
public class FsspClientImplTest {

    @Inject
    IFsspClient fsspClient;
    @Inject
    IFsspStructuresGenerator generator;

    @Test
    @InRequestScope
    public void testRequestDocumentsAndStatuses() {
        fsspClient.requestDocumentsAndStatuses();
        assertThat("Method not throws any exception!", true);
    }

    @Test
    @InRequestScope
    public void testSendExecutiveDocuments() throws biz.red_soft.ncore.dx._1_1.smev25.ws.Error {

        ExtendedExtDoc doc = new ExtendedExtDoc();
        doc.setClaimerAdr("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        doc.setExternalKey("123456");
        DXPack dxPack = generator.createDXPack("1", "46608", "33021");


        //Добавяем документ в пакет
        generator.addExecutiveDocumentToDXPack(dxPack, doc);
        ArrayList<DXBlock> dxBlocks = new ArrayList<>();
        dxBlocks.add(dxPack);
        fsspClient.sendExecutiveDocuments(dxBlocks);
        assertThat("Method not throws any exception!", true);
    }

    @Test
    @InRequestScope
    public void testSendRecallDocuments() throws Error {
        fsspClient.sendRecallDocuments(Collections.EMPTY_LIST);
        assertThat("Method not throws any exception!", true);
    }
}