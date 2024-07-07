package io.github.zwieback.familyfinance.business.template.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Template;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

class TemplateForceDestroyer extends EntityForceDestroyer<Template> {

    TemplateForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<Template> getEntityClass() {
        return Template.class;
    }

    @Override
    protected QueryAttribute<Template, Integer> getIdAttribute() {
        return Template.ID;
    }
}
