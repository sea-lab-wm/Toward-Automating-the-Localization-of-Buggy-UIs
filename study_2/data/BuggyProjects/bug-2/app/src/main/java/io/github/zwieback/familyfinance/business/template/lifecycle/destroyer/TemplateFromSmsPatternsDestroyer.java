package io.github.zwieback.familyfinance.business.template.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.github.zwieback.familyfinance.core.model.Template;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

public class TemplateFromSmsPatternsDestroyer extends EntityFromDestroyer<Template, SmsPattern> {

    public TemplateFromSmsPatternsDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Template> next() {
        return new TemplateForceDestroyer(context, data);
    }

    @Override
    protected Class<SmsPattern> getFromClass() {
        return SmsPattern.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Template template) {
        return SmsPattern.TEMPLATE_ID.eq(template.getId());
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.sms_patterns_with_template_exists;
    }
}
