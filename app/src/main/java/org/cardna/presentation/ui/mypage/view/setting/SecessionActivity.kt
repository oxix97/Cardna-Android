package org.cardna.presentation.ui.mypage.view.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import com.example.cardna.R
import com.example.cardna.databinding.ActivitySecessionBinding
import dagger.hilt.android.AndroidEntryPoint
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.mypage.viewmodel.SettingViewModel

@AndroidEntryPoint
class SecessionActivity : BaseViewUtil.BaseAppCompatActivity<ActivitySecessionBinding>(R.layout.activity_secession) {
    private val settingViewModel: SettingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.settingViewModel = settingViewModel
        initView()
    }

    override fun initView() {
        setObserve()
        setEtcContentListener()
    }

    private fun setObserve() {
        settingViewModel.isSecessionReasonValid.observe(this) { isSecessionReasonValid ->
            with(binding.buttonSecession) {
                if (isSecessionReasonValid) {
                    setBackgroundResource(R.drawable.bg_mainpurple_maingreen_gradient_radius_10dp)
                } else {
                    setBackgroundResource(R.drawable.bg_white_3_radius_10dp)
                }
            }
        }
    }

    private fun setEtcContentListener() {
        binding.etSecessionReason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    settingViewModel.setEtcContent("")
                } else {
                    settingViewModel.setEtcContent(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    companion object {
        const val ONE = 1
        const val TWO = 2
        const val THREE = 3
        const val FOUR = 4
        const val FIVE = 5
        const val SIX = 6
    }
}