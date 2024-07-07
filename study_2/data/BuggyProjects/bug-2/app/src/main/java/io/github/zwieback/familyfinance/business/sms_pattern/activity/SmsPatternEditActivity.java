package io.github.zwieback.familyfinance.business.sms_pattern.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.sms_pattern.adapter.SmsPatternProvider;
import io.github.zwieback.familyfinance.business.template.activity.TemplateActivity;
import io.github.zwieback.familyfinance.core.activity.EntityEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.github.zwieback.familyfinance.core.model.Template;
import io.github.zwieback.familyfinance.databinding.ActivityEditSmsPatternBinding;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_TEMPLATE_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.TEMPLATE_CODE;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToInteger;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;

public class SmsPatternEditActivity
        extends EntityEditActivity<SmsPattern, ActivityEditSmsPatternBinding> {

    public static final String INPUT_SMS_PATTERN_ID = "smsPatternId";
    public static final String OUTPUT_SMS_PATTERN_ID = "resultSmsPatternId";

    @Override
    protected int getTitleStringId() {
        return R.string.sms_pattern_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_sms_pattern;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_SMS_PATTERN_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_SMS_PATTERN_ID;
    }

    @Override
    protected Class<SmsPattern> getEntityClass() {
        return SmsPattern.class;
    }

    @Override
    protected EntityProvider<SmsPattern> createProvider() {
        return new SmsPatternProvider(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TEMPLATE_CODE:
                int templateId = extractOutputId(resultIntent, RESULT_TEMPLATE_ID);
                loadTemplate(templateId);
                break;
        }
    }

    public void onTemplateClick(View view) {
        Intent intent = new Intent(this, TemplateActivity.class);
        startActivityForResult(intent, TEMPLATE_CODE);
    }

    @SuppressWarnings("unused")
    public void onCommonChange(CompoundButton buttonView, boolean isChecked) {
        if (entity.getIconName() == null) {
            SmsPattern smsPatternCopy = entity.copy();
            smsPatternCopy.setCommon(isChecked);
            provider.setupIcon(binding.icon.getIcon(), smsPatternCopy);
        }
    }

    private void loadTemplate(int templateId) {
        loadEntity(Template.class, templateId, onSuccessfulTemplateFound());
    }

    private Consumer<Template> onSuccessfulTemplateFound() {
        return foundTemplate -> entity.setTemplate(foundTemplate);
    }

    @Override
    protected void createEntity() {
        SmsPattern smsPattern = new SmsPattern();
        bind(smsPattern);
    }

    @Override
    protected void bind(SmsPattern smsPattern) {
        entity = smsPattern;
        binding.setSmsPattern(smsPattern);
        provider.setupIcon(binding.icon.getIcon(), smsPattern);
        super.bind(smsPattern);
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.templateName.setOnClickListener(this::onTemplateClick);
        binding.templateName.setOnClearTextListener(() -> entity.setTemplate(null));
        binding.common.setOnCheckedChangeListener(this::onCommonChange);
    }

    @Override
    protected void updateEntityProperties(SmsPattern smsPattern) {
        smsPattern.setName(binding.name.getText().toString());
        smsPattern.setSender(binding.sender.getText().toString());
        smsPattern.setRegex(binding.regex.getText().toString());
        smsPattern.setDateGroup(stringToInteger(binding.dateGroup.getText().toString()));
        smsPattern.setValueGroup(stringToInteger(binding.valueGroup.getText().toString()));
        smsPattern.setCommon(binding.common.isChecked());
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        List<ValidatingTextInputLayout> layouts = new ArrayList<>(Arrays.asList(binding.nameLayout,
                binding.senderLayout, binding.templateNameLayout, binding.regexLayout));
        if (isTextNotEmpty(binding.dateGroup.getText().toString())) {
            layouts.add(binding.dateGroupLayout);
        }
        if (isTextNotEmpty(binding.valueGroup.getText().toString())) {
            layouts.add(binding.valueGroupLayout);
        }
        return layouts;
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }
}
