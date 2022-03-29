package org.cardna.presentation.ui.detailcard.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.IdRes
import com.example.cardna.R
import com.example.cardna.databinding.ActivityDetailCardBinding
import dagger.hilt.android.AndroidEntryPoint
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.detailcard.viewmodel.DetailCardViewModel
import org.cardna.presentation.util.setHandler
import org.cardna.presentation.util.setSrcWithGlide
import org.cardna.presentation.util.showCustomDialog
import org.cardna.presentation.util.showLottie

@AndroidEntryPoint
class DetailCardActivity : BaseViewUtil.BaseAppCompatActivity<ActivityDetailCardBinding>(R.layout.activity_detail_card) {
    private val detailCardViewModel: DetailCardViewModel by viewModels()
    private var cardType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.detailCardViewModel = detailCardViewModel
        binding.detailCardActivity = this
        initView()
    }

    override fun initView() {
        initData()  //TODO API완성 후 다시 test
        setObserve()
    }

    private fun initData() {
        val id = intent.getIntExtra(BaseViewUtil.CARD_ID, 0)
        detailCardViewModel.getDetailCard(id)  //TODO API완성 후 다시 test
    }

    @SuppressLint("ResourceType")
    private fun setObserve() {
        detailCardViewModel.detailCard.observe(this) { detailCard ->
            // cardType = detailCard.type   //TODO API완성 후 다시 test
            cardType = "me"
            setSrcWithGlide(detailCard.cardImg, binding.ivDetailcardImage)

            with(binding) {
                when (cardType) {
                    CARD_ME -> {
                        ctlDetailcardFriend.visibility = View.GONE
                        tvDetailcardTitle.setBackgroundResource(R.drawable.bg_maingreen_stroke_real_black_2dp)
                        ibtnDetailcardEdit.setImageResource(R.drawable.ic_detail_card_me_trash)
                        showEditDialog(R.layout.dialog_detail_cardme, null, null)
                    }
                    CARD_YOU -> {
                        tvDetailcardTitle.setBackgroundResource(R.drawable.bg_mainpurple_stroke_real_black_2dp)
                        showEditDialog(R.layout.dialog_detail_cardyou)
                    }
                    STORAGE -> {
                        tvDetailcardTitle.setBackgroundResource(R.drawable.bg_white_1_5_stroke_real_black_2dp)
                        showEditDialog(R.layout.dialog_detail_storage)
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun showEditDialog(@IdRes layout: Int, paramsX: Int? = 1400, paramsY: Int? = 410) {
        binding.ibtnDetailcardEdit.setOnClickListener {
            val dialog = this.showCustomDialog(layout, paramsX, paramsY)
            val deleteBtn = dialog.findViewById<Button>(R.id.tv_dialog_delete)

            when (cardType) {
                CARD_ME -> {
                    val noBtn = dialog.findViewById<Button>(R.id.tv_dialog_cardme_no)
                    noBtn.setOnClickListener {
                        setHandler(dialog)
                    }
                }
                CARD_YOU -> {
                    val saveBtn = dialog.findViewById<Button>(R.id.tv_dialog_cardyou_save)
                    saveBtn.setOnClickListener {
                        detailCardViewModel.keepOrAddCard() //TODO API완성 후 다시 test
                        setHandler(dialog)
                    }
                }
                STORAGE -> {
                    val declarationBtn = dialog.findViewById<Button>(R.id.tv_dialog_storage_report)
                    declarationBtn.setOnClickListener {
                        showUerReportDialog()
                        setHandler(dialog)
                    }
                }
            }
            deleteBtn.setOnClickListener {
                detailCardViewModel.deleteCard()  //TODO API완성 후 다시 test
                finish()
            }
        }
    }

    @SuppressLint("ResourceType")
    fun showUerReportDialog() {
        val dialog = this.showCustomDialog(R.layout.dialog_user_report)
        val reasonOneBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_one)
        val reasonTwoBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_two)
        val reasonThreeBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_three)
        val reasonFourBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_reason_four)
        val cancelBtn = dialog.findViewById<Button>(R.id.tv_dialog_report_cancel)

        //TODO API완성 후 다시 test
        reasonOneBtn.setOnClickListener {
            detailCardViewModel.reportUser()
            setHandler(dialog)
        }

        reasonTwoBtn.setOnClickListener {
            detailCardViewModel.reportUser()
            setHandler(dialog)
        }

        reasonThreeBtn.setOnClickListener {
            detailCardViewModel.reportUser()
            setHandler(dialog)
        }

        reasonFourBtn.setOnClickListener {
            detailCardViewModel.reportUser()
            setHandler(dialog)
        }
        cancelBtn.setOnClickListener {
            setHandler(dialog)
        }
    }

    fun setCardShareClickListener() {
        startActivity(Intent(this@DetailCardActivity, CardShareActivity::class.java))
    }

    fun setLikeClickListener() {
        with(binding) {
            ctvDetailcardLike.toggle()
            // detailCardViewModel?.postLike() ?: return   //TODO API완성 후 다시 test

            if (ctvDetailcardLike.isChecked) {
                showLikeLottie()
                tvDetailcardLikecount.text = (++detailCardViewModel!!.currentLikeCount).toString()
            } else {
                tvDetailcardLikecount.text = (--detailCardViewModel!!.currentLikeCount).toString()
            }
        }
    }

    private fun showLikeLottie() {
        with(binding) {
            when (cardType) {
                CARD_ME -> showLottie(laDetailcardLottie, CARD_ME, "lottie_cardme.json")
                CARD_YOU -> showLottie(laDetailcardLottie, CARD_YOU, "lottie_cardyou.json")
            }
        }
    }

    companion object {
        const val CARD_ME = "me"
        const val CARD_YOU = "you"
        const val STORAGE = "storage"
    }
}