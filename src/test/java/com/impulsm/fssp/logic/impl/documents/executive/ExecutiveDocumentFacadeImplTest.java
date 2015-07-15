package com.impulsm.fssp.logic.impl.documents.executive;

import com.impulsm.database.datasource.OracleDataSource;
import com.impulsm.fssp.logic.api.documents.executive.IExecutiveDocumentFacade;
import com.impulsm.fssp.utils.impl.DefaultXMLSerializer;
import com.impulsm.fssp.utils.impl.FSSPSignatureUtilImpl;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by vinichenkosa on 09/07/15.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({
        OracleDataSource.class,
        ExecutiveDocumentBaseFacadeImpl.class,
        DefaultXMLSerializer.class,
        ExecutiveDocumentFacadeImpl.class,
        FSSPSignatureUtilImpl.class
})
public class ExecutiveDocumentFacadeImplTest {

    @Inject
    IExecutiveDocumentFacade facade;

    @Test
    @InRequestScope
    public void testGetCursorWithExecutiveDocumentsForRegistry() throws Exception {

        ResultSet resultSet = facade.getCursorWithExecutiveDocumentsForRegistry(new BigDecimal("214807"));
        assertThat("result set not null", resultSet, notNullValue());

    }
}