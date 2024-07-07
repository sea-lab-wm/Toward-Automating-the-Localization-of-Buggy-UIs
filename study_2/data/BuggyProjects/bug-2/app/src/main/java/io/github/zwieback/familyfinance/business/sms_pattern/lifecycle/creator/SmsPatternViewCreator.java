package io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class SmsPatternViewCreator extends EntityViewCreator {

    public SmsPatternViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_sms_pattern";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT sp.id            AS id," +
                "       sp.icon_name     AS icon_name," +
                "       sp.name          AS name," +
                "       sp.regex         AS regex," +
                "       sp.sender        AS sender," +
                "       sp.date_group    AS date_group," +
                "       sp.value_group   AS value_group," +
                "       sp.common        AS common," +
                "       te.id            AS template_id," +
                "       te.name          AS template_name" +
                "  FROM t_sms_pattern sp" +
                "       LEFT JOIN t_template te ON te.id = sp.template_id";
    }
}
