package org.cardna.presentation.ui.maincard.view


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import dagger.hilt.android.AndroidEntryPoint
import land.sungbin.systemuicontroller.setNavigationBarColor
import land.sungbin.systemuicontroller.setSystemBarsColor
import org.cardna.R
import org.cardna.databinding.ActivityMainCardBinding
import org.cardna.databinding.DialogRelationBinding
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.alarm.viewmodel.AlarmViewModel
import org.cardna.presentation.ui.cardpack.view.CardCreateActivity
import org.cardna.presentation.ui.cardpack.view.FriendCardPackActivity
import org.cardna.presentation.ui.detailcard.view.DetailCardActivity
import org.cardna.presentation.ui.maincard.adapter.MainCardAdapter
import org.cardna.presentation.ui.maincard.viewmodel.MainCardViewModel
import org.cardna.presentation.ui.mypage.viewmodel.MyPageViewModel
import org.cardna.presentation.util.StatusBarUtil
import org.cardna.presentation.util.setBeforeGradientText
import org.cardna.presentation.util.setGradientText
import org.cardna.presentation.util.viewPagerAnimation
import timber.log.Timber


@AndroidEntryPoint
class MainCardActivity :
    BaseViewUtil.BaseAppCompatActivity<ActivityMainCardBinding>(R.layout.activity_main_card) {
    private val mainCardViewModel: MainCardViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val alarmViewModel: AlarmViewModel by viewModels()
    private lateinit var mainCardAdapter: MainCardAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainCardViewModel = mainCardViewModel
        initView()
    }

    //메인 프레그먼트 마이페이지 -> 타인꺼보는 것과 똑같은 구조 -> 내가 내껄 볼일은 없음
    override fun initView() {
        initAdapter()
        initData()
        initDialog()
        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        //todo 친구 코드액티비티에서 받아서 저장하고 초기 데이터 뿌림
        val friendId = intent.getIntExtra("friendId", -1)
        val name = intent.getStringExtra("name") ?: ""
        if(friendId!=-1) mainCardViewModel.setFriendNameAndId(name, friendId)

        mainCardViewModel.setFriendInfoSucccess.observe(this) {
            if (it) {
                mainCardViewModel.getMainCardList(mainCardViewModel.friendId.value)
                mainCardViewModel.getMainCardList(mainCardViewModel.friendId.value)
            }
        }

        mainCardViewModel.getMyPageUser(name)
        binding.vpMaincardList.setCurrentItem(mainCardViewModel.cardPosition.value ?: 0, false)
        binding.tvMaincardUserName.text = name
        relationObserve()
        setInitPagePosition()
    }

    private fun setClickListener() {
        setCardYouWrite()
        setCardPackActivity()
    }

    private fun setContainerColor(relation: String) {
        if (relation == FRIEND) {
            setGradientSetting()
        } else {
            setGrayBlackSetting()
        }
    }

    private fun setGradientSetting() {
        with(binding) {
            tvMaincardGotoCardpack.apply {
                isSelected = true
                this.text = setGradientText(this.text.toString())
            }
            ivMaincardGotoCardpackBackground.setBackgroundResource(R.drawable.bg_maincard_gradient_radius_45)
        }
    }

    private fun setGrayBlackSetting() {
        with(binding) {
            tvMaincardGotoCardpack.apply {
                isSelected = false
                this.text = ""
                this.text = getString(R.string.maincard_tv_cardpack_text)
            }
            ivMaincardGotoCardpackBackground.setBackgroundResource(R.drawable.bg_maincard_white_4_radius_45)
        }
    }

    private fun setCardPackActivity() {
        //todo 친구 카드팩 액티비티로 가는 경우임
        binding.ivMaincardGotoCardpackBackground.setOnClickListener {
            startActivity(
                Intent(this, FriendCardPackActivity::class.java)
                    .putExtra(BaseViewUtil.ID, mainCardViewModel.friendId.value)
                    .putExtra(BaseViewUtil.NAME, mainCardViewModel.friendName.value)
            )
        }
    }

    private fun relationObserve() {
        mainCardViewModel.relation.observe(this) {
            val relation = it.toString()
            setContainerColor(relation)
            friendRelationCheck(relation)
        }
    }

    private fun dialogDismiss(dialog: Dialog, relationDialog: DialogRelationBinding) {
        with(relationDialog) {
            clRelationAddFriend.visibility = View.INVISIBLE
            clRelationDisconnect.visibility = View.INVISIBLE
            clRelationProgressingCancel.visibility = View.INVISIBLE
            clRelationAcceptFriend.visibility = View.INVISIBLE
            btnRelationConfirm.setBackgroundResource(R.drawable.bg_white_4_5dp)
        }
        dialog.dismiss()
    }

    private fun initDialog() {
        val dialog = Dialog(this)
        val relationDialog = DialogRelationBinding.inflate(dialog.layoutInflater)
        binding.ivMaincardFriend.setOnClickListener {
            initRelationDialog(dialog, relationDialog)
        }
    }

    private fun friendRelationCheck(relation: String) {
        binding.ivMaincardFriend.apply {
            when (relation) {
                UNKNOWN, RESPONSE -> setBackgroundResource(R.drawable.ic_mypage_friend_unchecked)
                FRIEND -> setBackgroundResource(R.drawable.ic_mypage_friend_checked)
                REQUEST -> setBackgroundResource(R.drawable.ic_mypage_friend_ing)
            }
        }
    }

    private fun initRelationDialog(
        dialog: Dialog,
        dialogBinding: DialogRelationBinding,
    ) {
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val friendId = intent.getIntExtra("friendId", 0)

        //relation 이거를 observe해야함
        val relation = mainCardViewModel.relation.value.toString()
        // TODO: 요기
        val code = intent.getStringExtra("code").toString()
        myPageViewModel.updateSearchCodeQuery(code)
        myPageViewModel.searchCodePost()

        with(dialogBinding) {
            when (relation) {
                UNKNOWN -> {
                    clRelationAddFriend.visibility = View.VISIBLE
                }
                FRIEND -> {
                    clRelationDisconnect.visibility = View.VISIBLE
                }
                REQUEST -> {
                    clRelationProgressingCancel.visibility = View.VISIBLE
                }
                RESPONSE -> {
                    clRelationAcceptFriend.visibility = View.VISIBLE
                    val title =
                        intent.getStringExtra("name").plus(getString(R.string.dialog_apply_request))
                    tvRelationAcceptFriendTitle.text = title
                    btnRelationCancel.text = getString(R.string.dialog_apply_reject)
                    btnRelationConfirm.apply {
                        text = getString(R.string.dialog_apply_accept)
                        setBackgroundResource(R.drawable.bg_gradient_green_purple_radius_5)
                    }
                }
            }
            //enable : true -> white , enable : false -> dark_gray
            btnRelationConfirm.isSelected = relation != RESPONSE
            setCancelDialog(dialog, this)
            setConfirmDialog(dialog, this, friendId, relation)
        }
    }

    private fun setCancelDialog(
        dialog: Dialog,
        dialogBinding: DialogRelationBinding,
//        friendId: Int,
//        friendRelation: String
    ) {
        dialogBinding.btnRelationCancel.setOnClickListener {
            dialogDismiss(dialog, dialogBinding)
        }
    }

    private fun setConfirmDialog(
        dialog: Dialog,
        dialogBinding: DialogRelationBinding,
        friendId: Int,
        friendRelation: String
    ) {
        Timber.e("TTT friendId : $friendId")
        dialogBinding.btnRelationConfirm.setOnClickListener {
            if (friendRelation == RESPONSE) {
                //친추 받았을때
                alarmViewModel.acceptOrDenyFriend(friendId, true)
                mainCardViewModel.setRelation(FRIEND)
                Timber.d("success : ${mainCardViewModel.relation.value}")
            } else {
                if (friendRelation == UNKNOWN){
                    Amplitude.getInstance().logEvent("FriendMain_AddFriend")
                    mainCardViewModel.setRelation(REQUEST)}
                else {
                    Amplitude.getInstance().logEvent("FriendMain_CancelFriend")
                    mainCardViewModel.setRelation(UNKNOWN)
                }
                mainCardViewModel.postFriendRequest(friendId)
                mainCardViewModel.getMainCardList(friendId)
            }
            dialogDismiss(dialog, dialogBinding)
        }
    }

    private fun setInitPagePosition() {
        binding.vpMaincardList.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mainCardViewModel.saveInitCardPosition(position)
            }
        })
    }

    private fun initAdapter() {
        Timber.d("init adapter")
        mainCardAdapter = MainCardAdapter() {
            setDetailActivity()
        }
        mainCardViewModel.cardList.observe(this) {
            mainCardAdapter.submitList(it)
        }
        with(binding.vpMaincardList) {
            adapter = mainCardAdapter
            viewPagerAnimation(binding.vpMaincardList)
        }
    }

    private fun setDetailActivity() {
        Intent(this, DetailCardActivity::class.java).apply {
            mainCardViewModel.cardPosition.value?.let {
                mainCardViewModel.cardList.value?.get(it)?.let { card ->
                    putExtra(BaseViewUtil.CARD_ID, card.id)
                }
            }
            startActivity(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCardYouWrite() {
        binding.ivMaincardWrite.setOnClickListener {
            Amplitude.getInstance().logEvent("FriendMain_WritingCardner")
            val friendId = intent.getIntExtra("friendId", 0)
            val name = intent.getStringExtra("name")
            Intent(this, CardCreateActivity::class.java).apply {
                putExtra(BaseViewUtil.IS_CARD_ME_OR_YOU, BaseViewUtil.CARD_YOU)
                putExtra(BaseViewUtil.IS_CARDPACK_OR_MAINCARD, BaseViewUtil.FROM_MAINCARD)
                putExtra(BaseViewUtil.IS_CODE_OR_FRIEND, BaseViewUtil.FROM_CODE)
                putExtra("id", friendId)
                putExtra("name", name)
                startActivity(this)
            }
        }
    }

    companion object {
        const val UNKNOWN = "1.0"
        const val FRIEND = "2.0"
        const val REQUEST = "3.0"
        const val RESPONSE = "4.0"

    }
}