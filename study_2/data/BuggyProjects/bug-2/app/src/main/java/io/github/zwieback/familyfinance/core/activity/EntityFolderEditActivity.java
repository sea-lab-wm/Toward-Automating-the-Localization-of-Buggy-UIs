package io.github.zwieback.familyfinance.core.activity;

import android.databinding.ViewDataBinding;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.johnpetitto.validator.ValidatingTextInputLayout;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.model.IBaseEntityFolder;
import io.github.zwieback.familyfinance.util.SqliteUtils;
import io.requery.meta.QueryAttribute;
import io.requery.query.Condition;
import io.requery.query.Tuple;
import io.requery.reactivex.ReactiveResult;

import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;

public abstract class EntityFolderEditActivity<
        ENTITY extends IBaseEntityFolder,
        BINDING extends ViewDataBinding>
        extends EntityEditActivity<ENTITY, BINDING> {

    public static final String INPUT_PARENT_ID = "parentId";
    public static final String INPUT_IS_FOLDER = "isFolder";

    protected final boolean isParentValid(String input, ENTITY parent, String tableName) {
        if (parent == null || isTextEmpty(input)) {
            return true;
        }
        if (!parent.isFolder()) {
            getParentLayout().setErrorLabel(R.string.parent_must_be_a_folder);
            return false;
        }
        if (parent.getId() == entity.getId()) {
            getParentLayout().setErrorLabel(R.string.parent_can_not_be_a_parent_of_itself);
            return false;
        }
        boolean parentInsideItself;
        if (SqliteUtils.cteSupported()) {
            parentInsideItself = isParentInsideItselfUsingCte(entity.getId(), parent.getId(),
                    tableName);
        } else {
            parentInsideItself = isParentInsideItself(entity.getId(), parent.getId());
        }
        if (parentInsideItself) {
            getParentLayout().setErrorLabel(R.string.parent_can_not_be_inside_itself);
            return false;
        }
        return true;
    }

    protected abstract ValidatingTextInputLayout getParentLayout();

    protected abstract boolean isParentInsideItself(int parentId, int newParentId);

    @SuppressWarnings("SimplifiableIfStatement")
    protected final boolean
    isParentInsideItself(int newParentId,
                         QueryAttribute<ENTITY, Integer> entityIdAttribute,
                         Condition<?, ?> whereCondition,
                         BiFunction<Integer, Integer, Boolean> recursiveFunction) {
        Iterator<ENTITY> iterator = data
                .select(getEntityClass(), entityIdAttribute)
                .where(whereCondition)
                .get().iterator();

        List<Integer> childIds = Stream.of(iterator)
                .map(ENTITY::getId)
                .collect(Collectors.toList());

        if (childIds.isEmpty()) {
            return false;
        }
        if (childIds.contains(newParentId)) {
            return true;
        }
        return Stream.of(childIds)
                .anyMatch(childId -> recursiveFunction.apply(childId, newParentId));
    }

    private boolean isParentInsideItselfUsingCte(int parentId,
                                                 int newParentId,
                                                 String tableName) {
        String query = "with recursive subtree" +
                " as (select id" +
                "       from :tableName" +
                "      where id = :parentId" +
                "      union all" +
                "     select child.id" +
                "       from :tableName as child" +
                "       join subtree on child.parent_id = subtree.id)" +
                " select id" +
                " from subtree" +
                " where id <> :parentId";
        query = query.replaceAll(":parentId", String.valueOf(parentId));
        query = query.replaceAll(":tableName", tableName);

        ReactiveResult<Tuple> result = data.raw(query);
        Set<Integer> childIds = Stream.of(result.iterator())
                .map(tuple -> (Integer) tuple.get("id"))
                .collect(Collectors.toSet());
        return childIds.contains(newParentId);
    }

    protected boolean extractInputBoolean(String name) {
        return getIntent().getBooleanExtra(name, false);
    }
}
