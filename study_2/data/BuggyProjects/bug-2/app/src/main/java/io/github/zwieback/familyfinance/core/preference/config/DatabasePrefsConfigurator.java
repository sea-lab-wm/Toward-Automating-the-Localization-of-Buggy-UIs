package io.github.zwieback.familyfinance.core.preference.config;

import ds.gendalf.PrefsConfig;

import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;

@PrefsConfig("DatabasePrefs")
public interface DatabasePrefsConfigurator {

    int currencyId = ID_AS_NULL;
    int accountId = ID_AS_NULL;
    int personId = ID_AS_NULL;

    int incomesArticleId = ID_AS_NULL;
    int expensesArticleId = ID_AS_NULL;
    int transferArticleId = ID_AS_NULL;
}
