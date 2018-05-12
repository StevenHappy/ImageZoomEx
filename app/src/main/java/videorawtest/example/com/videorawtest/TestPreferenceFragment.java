package videorawtest.example.com.videorawtest;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import java.util.Locale;


public class TestPreferenceFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnPreferenceClickListener  {

    private static final String LANGUAGE = "language";
    private static final String LANGUAGEGROUP = "language_group";

    private EditTextPreference mLanguageETP;

    private RadioGroup mGroup;
    private int mLanguageId;
    private Locale mLocale;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initView();
        setListener();
    }

    private void initView() {
        mLanguageETP = (EditTextPreference) findPreference(LANGUAGE);

        Resources res = getResources();
        Configuration config = res.getConfiguration();
        if (config.locale.equals(Locale.CHINA)) {
            mLanguageETP.setSummary(R.string.chinese);
            mLanguageId = R.id.radio0;
        } else {
            mLanguageETP.setSummary(R.string.english);
            mLanguageId = R.id.radio1;
        }
        mLanguageETP.getSharedPreferences().edit()
                .putInt(LANGUAGEGROUP, mLanguageId).commit();
    }

    private void setListener() {
        mLanguageETP.setOnPreferenceChangeListener(this);
        mLanguageETP.setOnPreferenceClickListener(this);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case LANGUAGE:
                // 保存数据
                mLanguageETP.getEditor().putInt(LANGUAGEGROUP, mLanguageId)
                        .commit();
                if (mLanguageId == R.id.radio0) {
                    mLocale = Locale.CHINA;
                } else {
                    mLocale = Locale.ENGLISH;
                }
                // 设置Summary的内容
                changeLanguage(mLocale);
                getActivity().finish();
                break;
        }
        return true;
    }

    /**
     * 切换语言
     *
     * @param locale
     *            本地语言的参数
     */
    private void changeLanguage(Locale locale) {
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
        getActivity().sendBroadcast(new Intent("language"));


        {
            Resources rs = getResources();
            Configuration configuration = rs.getConfiguration();
            configuration.locale = (mLanguageId == R.id.radio0)?Locale.CHINA:Locale.ENGLISH;
            DisplayMetrics dm2 = res.getDisplayMetrics();
            res.updateConfiguration(configuration, dm2);

        }
    }

    /**
     * 设置选择语言的种类
     */
    private void setLanguageRadioButton(final Preference preference) {
        mGroup = (RadioGroup) mLanguageETP.getDialog().findViewById(
                R.id.radioGroup1);
        mLanguageId = mLanguageETP.getSharedPreferences().getInt(LANGUAGEGROUP,
                0);
        mGroup.check(mLanguageId);
        ((RadioButton) mGroup.getChildAt(0)).setText(R.string.chinese);
        ((RadioButton) mGroup.getChildAt(1)).setText(R.string.english);

        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio0) {
                    mLanguageId = R.id.radio0;
                } else if (checkedId == R.id.radio1) {
                    mLanguageId = R.id.radio1;
                }
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case LANGUAGE:
                setLanguageRadioButton(preference);
                break;
        }
        return true;
    }

}
