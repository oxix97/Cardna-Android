package org.cardna.presentation.ui.cardpack.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.cardna.R
import org.cardna.databinding.FragmentCardMeBinding
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.cardpack.adapter.CardPackMeRecyclerViewAdapter
import org.cardna.presentation.ui.cardpack.viewmodel.CardPackViewModel
import org.cardna.presentation.ui.detailcard.view.DetailCardActivity
import org.cardna.presentation.util.SpacesItemDecoration
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class CardMeFragment : BaseViewUtil.BaseFragment<FragmentCardMeBinding>(R.layout.fragment_card_me) {

    private val cardPackViewModel: CardPackViewModel by activityViewModels() // cardPackFragment 의 뷰모델을 공유
    private lateinit var cardMeAdapter: CardPackMeRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardPackViewModel = cardPackViewModel
        initView()
    }

    override fun onResume() {
        super.onResume()
        cardPackViewModel.updateCardMeList() // 카드나 카드들을 서버로부터 불러오기
    }

    override fun initView() {
        initEmptyViewListener()
        initCardMeRvAdapter() // 리사이클러뷰 및 어댑터 설정
        Timber.e("CardMe isCardMeEmpty : ${cardPackViewModel.isCardMeEmpty.value}")
    }

    // Adapter 생성
    private fun initCardMeRvAdapter() {

        lateinit var cardMeAdapter: CardPackMeRecyclerViewAdapter

        // 내 카드, 친구 카드일 때 일단 둘다
        cardMeAdapter = CardPackMeRecyclerViewAdapter({ // 어댑터 초기화 =>
            // 1. 각 리사이클러뷰 아이템 카드 전체에 달아줄 람다 전달
            Intent(requireContext(), DetailCardActivity::class.java).apply {
                putExtra(BaseViewUtil.CARD_ID, it.id) // 리사이클러뷰의 아이템 중 카드 선택시 그 카드의 id를 전달
                startActivity(this)
            }
        }, {
            // 2. 타인의 카드나일 때는, 공감버튼에 달아줄 리스너 하나 더 전달해줘야 한다.
            it ->
                // 1. ctv 토글 -> 레이아웃에서 자동으로 selector로 변환되게


                // 2. 서버통신
                cardPackViewModel.postLikeCardPack(it.id)

                // 3. 로티 띄우기
            }

        )


        Timber.e("CardMe : Adapter 생성")

        with(binding) {
            rvCardme.adapter = cardMeAdapter
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            rvCardme.layoutManager = gridLayoutManager
            rvCardme.addItemDecoration(SpacesItemDecoration((12 * resources.displayMetrics.density).roundToInt())) // 화면 비율 조정
        }

        // cardMeList 에 observer 등록
        // onResume 될 때, cardMeList 를 업데이트 시키고 cardMeList 가 변경되면, 이를 observe 해서 알아서 리사이클러뷰를 갱신해주도록
        cardPackViewModel.cardMeList.observe(viewLifecycleOwner) { it ->
            it?.let { cardMeAdapter.submitList(it) }
        }
    }

    private fun initEmptyViewListener() {
        binding.ctlBgAddCardme.setOnClickListener {
            // CardCreateActivity 로 이동
            val intent = Intent(requireActivity(), CardCreateActivity::class.java).apply {
                putExtra(BaseViewUtil.IS_CARD_ME_OR_YOU, BaseViewUtil.CARD_ME) // 내 카드나 작성이므로
            }
            startActivity(intent)
        }
    }
}