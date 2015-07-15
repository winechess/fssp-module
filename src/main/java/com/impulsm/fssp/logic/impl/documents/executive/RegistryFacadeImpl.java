package com.impulsm.fssp.logic.impl.documents.executive;

import com.impulsm.database.datasource.IDataSource;
import com.impulsm.exceptions.registry.RegistryUnloadException;
import com.impulsm.fssp.logic.api.documents.executive.IRegistryFacade;
import com.impulsm.fssp.models.documents.extdoc.SendingStatistics;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinichenkosa on 09/07/15.
 */
@RequestScoped
public class RegistryFacadeImpl implements IRegistryFacade {

    @Inject
    IDataSource ds;

    @Override
    public List<BigDecimal> getRegistriesIdsForSending() throws Exception {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_REGISTRIES_TO_UNLOAD);
             ResultSet rs = ps.executeQuery();) {

            List<BigDecimal> regIds = new ArrayList<>();

            while (rs.next()) {
                regIds.add(rs.getBigDecimal(1));
            }

            return regIds;
        }
    }

    public BigDecimal initRegistryUnload(BigDecimal regId) {

        //Если ид-р реестра равен null
        if (regId == null) {
            throw new IllegalArgumentException("Ид-р реестра равен Null.");
        }

        BigDecimal unloadId = null;

        //Получаем соединение
        try (Connection con = ds.getConnection();) {
            con.setAutoCommit(false);
            //Выполняем запрос
            try (CallableStatement cs = con.prepareCall(INIT_REGISTRY_UNLOAD)) {

                cs.registerOutParameter(1, Types.BIGINT);
                cs.setBigDecimal(2, new BigDecimal(2));
                cs.setBigDecimal(3, regId);
                cs.registerOutParameter(4, OracleTypes.CURSOR);
                cs.execute();

                //Если результат 1 или 2 возвращаем null
                int result = cs.getInt(1);

                if (result == 1 || result == 2) {
                    throw new RegistryUnloadException("Процедура init_registry_unload вернула " + result);
                } else {
                    //Получаем курсор из процедуры
                    try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                        if (rs.next()) {
                            unloadId = rs.getBigDecimal("UNLOAD_ID");
                            con.commit();
                        } else {
                            throw new RegistryUnloadException("Процедура init_registry_unload вернула " + result +
                                    ", но курсор оказался пустым.");
                        }
                    }
                }
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            logger.error("Ошибка инициализации реестра {} для выгрузки в ФССП.", regId, ex);
        } finally {
            return unloadId;
        }
    }

    @Override
    public void finalizeRegistryUnload(BigDecimal regId, BigDecimal unloadId, SendingStatistics statistics) {

        String error = null;
        if (!statistics.isAllDocsSended()) {
            error = "Ошибка: Не все постановления были отправлены.";
        }

        try (Connection con = ds.getConnection();) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(
                    "{call gibdd_apr.apr_registry_ep.finalize_registry_unload(?,?,?,?)}")
            ) {

                cs.setBigDecimal(1, regId);
                cs.setBigDecimal(2, unloadId);
                cs.setString(3, error);
                cs.setInt(4, statistics.getWasSent());

                cs.execute();
                con.commit();

                logger.debug("finalize_registry_unload успешно вызвана с параметрами: {}, {}, {}, {}.",
                        regId, unloadId, error, statistics.getWasSent());

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            logger.error("Не удалось вызвать процедуру finalize_registry_unload.", ex);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
