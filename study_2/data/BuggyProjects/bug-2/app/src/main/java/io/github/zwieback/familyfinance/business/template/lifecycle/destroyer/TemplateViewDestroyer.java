package io.github.zwieback.familyfinance.business.template.lifecycle.destroyer;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityViewDestroyer;

public class TemplateViewDestroyer extends EntityViewDestroyer {

    public TemplateViewDestroyer(Connection connection) {
        super(connection);
    }

    @NonNull
    @Override
    protected String getViewName() {
        return "v_template";
    }
}
