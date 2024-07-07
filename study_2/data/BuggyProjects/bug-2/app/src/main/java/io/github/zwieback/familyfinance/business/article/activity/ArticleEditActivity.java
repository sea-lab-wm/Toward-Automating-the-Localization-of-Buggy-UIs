package io.github.zwieback.familyfinance.business.article.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.adapter.ArticleProvider;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.type.ArticleType;
import io.github.zwieback.familyfinance.databinding.ActivityEditArticleBinding;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ARTICLE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.nonNullId;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;
import static io.github.zwieback.familyfinance.util.TransliterationUtils.transliterate;

public abstract class ArticleEditActivity
        extends EntityFolderEditActivity<Article, ActivityEditArticleBinding> {

    public static final String INPUT_ARTICLE_ID = "articleId";
    public static final String OUTPUT_ARTICLE_ID = "resultArticleId";

    protected abstract ArticleType getArticleType();

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_article;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_ARTICLE_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_ARTICLE_ID;
    }

    @Override
    protected Class<Article> getEntityClass() {
        return Article.class;
    }

    @Override
    protected EntityProvider<Article> createProvider() {
        return new ArticleProvider(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ARTICLE_CODE:
                int parentId = extractOutputId(resultIntent, RESULT_ARTICLE_ID);
                loadParent(parentId);
                break;
        }
    }

    public void onParentClick(View view) {
        Intent intent = new Intent(this, AllArticleActivity.class);
        intent.putExtra(EntityActivity.INPUT_REGULAR_SELECTABLE, false);
        intent.putExtra(EntityFolderActivity.INPUT_PROHIBITED_FOLDER_ID, entity.getId());
        startActivityForResult(intent, ARTICLE_CODE);
    }

    private void onParentRemoved() {
        entity.setParent(null);
        binding.parentLayout.setError(null);
    }

    private void loadParent(int parentId) {
        if (nonNullId(parentId)) {
            loadEntity(Article.class, parentId, foundArticle -> {
                entity.setParent(foundArticle);
                binding.parentLayout.setError(null);
            });
        }
    }

    @Override
    protected void createEntity() {
        int parentId = extractInputId(INPUT_PARENT_ID);
        boolean folder = extractInputBoolean(INPUT_IS_FOLDER);
        Article article = new Article();
        article.setType(getArticleType());
        article.setFolder(folder);
        bind(article);
        loadParent(parentId);
        disableLayout(binding.parentLayout, R.string.hint_parent_disabled);
    }

    @Override
    protected void bind(Article article) {
        entity = article;
        binding.setArticle(article);
        provider.setupIcon(binding.icon.getIcon(), article);
        super.bind(article);
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.parent.setOnClickListener(this::onParentClick);
        binding.parent.setOnClearTextListener(this::onParentRemoved);
        binding.parentLayout.setValidator(this::isParentValid);
        if (!entity.isFolder()) {
            return;
        }
        disableLayout(binding.defaultValueLayout, R.string.hint_default_value_disabled);
    }

    @Override
    protected void updateEntityProperties(Article article) {
        article.setName(binding.name.getText().toString());
        article.setNameAscii(transliterate(article.getName()));
        if (!entity.isFolder()) {
            article.setDefaultValue(stringToBigDecimal(binding.defaultValue.getText().toString()));
        }
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        List<ValidatingTextInputLayout> layouts = new ArrayList<>();
        layouts.addAll(Arrays.asList(binding.parentLayout, binding.nameLayout));
        if (!entity.isFolder() && isTextNotEmpty(binding.defaultValue.getText().toString())) {
            layouts.add(binding.defaultValueLayout);
        }
        return Collections.unmodifiableList(layouts);
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }

    private boolean isParentValid(String input) {
        return isParentValid(input, (Article) entity.getParent(), Article.$TYPE.getName());
    }

    @Override
    protected ValidatingTextInputLayout getParentLayout() {
        return binding.parentLayout;
    }

    @Override
    protected boolean isParentInsideItself(int parentId, int newParentId) {
        return isParentInsideItself(newParentId, Article.ID,
                Article.PARENT_ID.eq(parentId).and(Article.FOLDER.eq(true)),
                this::isParentInsideItself);
    }
}
