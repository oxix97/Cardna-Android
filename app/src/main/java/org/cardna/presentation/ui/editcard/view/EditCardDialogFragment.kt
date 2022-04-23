package org.cardna.presentation.ui.editcard.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.cardna.R
import com.example.cardna.databinding.FragmentEditCardDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import org.cardna.presentation.base.BaseViewUtil
import org.cardna.presentation.ui.editcard.adapter.EditCardTabAdapter
import org.cardna.presentation.ui.editcard.viewmodel.EditCardDialogViewModel
import kotlin.math.roundToInt

class EditCardDialogFragment : BaseViewUtil.BaseBottomDialogFragment<FragmentEditCardDialogBinding>(R.layout.fragment_edit_card_dialog) {

    private lateinit var editCardTabAdapter: EditCardTabAdapter
    private val editCardDialogViewModel: EditCardDialogViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editCardDialogViewModel = editCardDialogViewModel
        initView()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun initView() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.clBottomSheet.layoutParams.height =
            (resources.displayMetrics.heightPixels * 0.94).roundToInt()

      //  initData()  TODO 왜필요?
        initAdapter()
        initTabLayout()
        mainCardCount()
        setClickListener()
    }

    private fun initTabLayout() {
        val tabLabel = listOf("카드나", "카드너")
        TabLayoutMediator(
            binding.tlRepresentcardedit,
            binding.rvEditcarddialogContainer
        ) { tab, position ->
            tab.text = tabLabel[position]
        }.attach()
    }

    private fun initData() {
       // editCardDialogViewModel.getCardAll()  todo 여기서 데이터 가져올 필요없지 않나,,?
     //   editCardDialogViewModel.representCardCheck() todo 이건 왜 필요?
    }

    private fun initAdapter() {
        val fragmentList = listOf(CardMeTabFragment(), CardYouTabFragment())
        editCardTabAdapter = EditCardTabAdapter(this)
        editCardTabAdapter.fragments.addAll(fragmentList)

        binding.rvEditcarddialogContainer.adapter = editCardTabAdapter

    }

    private fun mainCardCount() {
        editCardDialogViewModel.selectedCardList.observe(viewLifecycleOwner) {
            binding.tvRepresentcardeditCardListCount.text = it.size.toString()
        }
    }

    private fun setClickListener() {
        binding.tvRepresentcardeditFinish.setOnClickListener {
            dismiss()
        }
    }
}