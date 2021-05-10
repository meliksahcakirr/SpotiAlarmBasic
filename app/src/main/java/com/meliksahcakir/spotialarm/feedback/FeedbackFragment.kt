package com.meliksahcakir.spotialarm.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.databinding.FragmentFeedbackBinding
import com.meliksahcakir.spotialarm.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FeedbackFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding: FragmentFeedbackBinding get() = _binding!!

    val viewModel: MainViewModel by sharedViewModel()

    override var alphaAnimationForFragmentTransitionEnabled = false
    override var draggable = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.warningEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
        binding.sendButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val message = binding.messageEditText.text.toString()
            viewModel.onFeedbackSendButtonClicked(name, email, message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
