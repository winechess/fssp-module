package com.impulsm.fssp.logic.impl.documents.executive;

import com.impulsm.database.datasource.OracleDataSource;
import com.impulsm.fssp.logic.api.documents.executive.IRegistryFacade;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by vinichenkosa on 09/07/15.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({
        OracleDataSource.class,
        RegistryFacadeImpl.class
})
public class RegistryFacadeImplTest {

    @Inject
    IRegistryFacade facade;

    @Test
    @InRequestScope
    public void testGetRegistriesIdsForSending() throws Exception {
        List<BigDecimal> registriesIdsForSending = facade.getRegistriesIdsForSending();
        assertThat("Method not throw exceptions", true);
    }
}