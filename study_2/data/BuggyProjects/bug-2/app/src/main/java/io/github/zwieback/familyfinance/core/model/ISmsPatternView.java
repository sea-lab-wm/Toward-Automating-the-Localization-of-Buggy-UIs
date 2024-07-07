package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import io.github.zwieback.familyfinance.core.model.restriction.SmsPatternRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.TemplateRestriction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.Nullable;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_sms_pattern")
public interface ISmsPatternView extends IBaseEntity {

    @Column(name = "template_id")
    int getTemplateId();

    @Bindable
    @Column(nullable = false, name = "template_name", length = TemplateRestriction.NAME_MAX_LENGTH)
    String getTemplateName();

    @Bindable
    @Column(nullable = false, length = SmsPatternRestriction.NAME_MAX_LENGTH)
    String getName();

    @Bindable
    @Column(nullable = false, length = SmsPatternRestriction.REGEX_MAX_LENGTH)
    String getRegex();

    @Bindable
    @Column(nullable = false, length = SmsPatternRestriction.SENDER_MAX_LENGTH)
    String getSender();

    @Bindable
    @Nullable
    @Column(name = "date_group")
    Integer getDateGroup();

    @Bindable
    @Nullable
    @Column(name = "value_group")
    Integer getValueGroup();

    @Bindable
    @Column(nullable = false, value = "false")
    boolean isCommon();
}
