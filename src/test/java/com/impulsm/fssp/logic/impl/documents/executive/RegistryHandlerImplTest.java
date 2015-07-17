package com.impulsm.fssp.logic.impl.documents.executive;

import com.impulsm.database.datasource.OracleDataSource;
import com.impulsm.fssp.logic.api.documents.executive.IRegistryHandler;
import com.impulsm.fssp.logic.impl.service.FsspClientImpl;
import com.impulsm.fssp.utils.impl.DefaultXMLSerializer;
import com.impulsm.fssp.utils.impl.FSSPSignatureUtilImpl;
import com.impulsm.fssp.utils.impl.FsspStructuresGeneratorImpl;
import com.impulsm.fssp.utils.impl.FsspUtilsImpl;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Created by vinichenkosa on 16.07.15.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({
        FsspClientImpl.class,
        FsspStructuresGeneratorImpl.class,
        FsspUtilsImpl.class,
        RegistryHandlerImpl.class,
        RegistryFacadeImpl.class,
        ExecutiveDocumentsHandlerImpl.class,
        ExecutiveDocumentFacadeImpl.class,
        PackHandlerImpl.class,
        PackFacadeImpl.class,
        SendingFacadeImpl.class,
        OracleDataSource.class,
        FSSPSignatureUtilImpl.class,
        ExecutiveDocumentBaseFacadeImpl.class,
        DefaultXMLSerializer.class

})
public class RegistryHandlerImplTest {

    @Inject
    IRegistryHandler registryHandler;

    @Test
    @InRequestScope
    public void testHandleAllRegistries() throws Exception {
        registryHandler.handleAllRegistries();
    }
}