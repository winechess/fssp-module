package com.impulsm.fssp.logic.impl.service;

import biz.red_soft.ncore.dx._1_1.smev25.ws.Error;
import com.impulsm.fssp.logic.api.service.IFsspClient;
import com.impulsm.fssp.utils.impl.FsspStructuresGeneratorImpl;
import com.impulsm.fssp.utils.impl.FsspUtilsImpl;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * Created by vinichenkosa on 07/07/15.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({FsspClientImpl.class, FsspStructuresGeneratorImpl.class, FsspUtilsImpl.class})
public class FsspClientImplTest {

    @Inject
    IFsspClient fsspClient;

    @Test
    @InRequestScope
    public void testRequestDocumentsAndStatuses() {
        fsspClient.requestDocumentsAndStatuses();
        assertThat("Method not throws any exception!", true);
    }

    @Test
    @InRequestScope
    public void testSendExecutiveDocuments() throws biz.red_soft.ncore.dx._1_1.smev25.ws.Error {
        fsspClient.sendExecutiveDocuments(Collections.EMPTY_LIST);
        assertThat("Method not throws any exception!", true);
    }

    @Test
    @InRequestScope
    public void testSendRecallDocuments() throws Error {
        fsspClient.sendRecallDocuments(Collections.EMPTY_LIST);
        assertThat("Method not throws any exception!", true);
    }
}