package com.icent.isaver.core.util;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionUtil {
    public TransactionUtil() {
    }

    public static TransactionStatus getMybatisTransactionStatus(DataSourceTransactionManager transactionManager) {
        return transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    public static TransactionStatus getMybatisTransactionStatus(DataSourceTransactionManager transactionManager, int transactionDefinition, int isolationLevel) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(isolationLevel);
        def.setPropagationBehavior(transactionDefinition);
        return transactionManager.getTransaction(def);
    }
}
