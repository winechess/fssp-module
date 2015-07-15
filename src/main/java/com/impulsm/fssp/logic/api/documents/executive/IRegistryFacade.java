package com.impulsm.fssp.logic.api.documents.executive;

import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vinichenkosa on 09/07/15.
 */
public interface IRegistryFacade {

    List<BigDecimal> getRegistriesIdsForSending() throws Exception;

    BigDecimal initRegistryUnload(BigDecimal regId);

    void finalizeRegistryUnload(BigDecimal regId, BigDecimal unloadId, SendingStatistics statistics);

    final static String SELECT_REGISTRIES_TO_UNLOAD = "SELECT id FROM gibdd_apr.ep_registry_v";
    final static String INIT_REGISTRY_UNLOAD = "{? = call gibdd_apr.apr_registry_ep.init_registry_unload(?,?,?)}";
}
