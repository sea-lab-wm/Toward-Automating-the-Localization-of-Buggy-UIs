package io.github.zwieback.familyfinance.business.template.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.TreeSet;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Template;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class TemplateCreator extends EntityCreator<Template> {

    public TemplateCreator(@NonNull Context context,
                           @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Template> buildEntities() {
        return new TreeSet<>(this);
    }

    @Override
    public int compare(Template left, Template right) {
        return Integer.compare(left.getId(), right.getId());
    }
}
