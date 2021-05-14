package com.meliksahcakir.spotialarm.rate

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.databinding.FragmentRatingBinding
import com.meliksahcakir.spotialarm.rateApp
import org.koin.androidx.viewmodel.ext.android.viewModel

class RatingFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRatingBinding? = null
    private val binding: FragmentRatingBinding get() = _binding!!

    val viewModel: RatingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentState.observe(viewLifecycleOwner) {
            when (it) {
                ReviewState.RATING_SELECTION -> {
                    binding.ratingTextView.text = getString(R.string.rating_explanation)
                    binding.smileRating.isVisible = true
                    binding.requestReviewTextView.isVisible = false
                    binding.submitButton.text = getString(R.string.submit)
                }
                ReviewState.GOOGLE_PLAY_REVIEW_REQUEST -> {
                    binding.ratingTextView.text = getString(R.string.thanks_for_rating)
                    binding.smileRating.isInvisible = true
                    binding.requestReviewTextView.isVisible = true
                    binding.submitButton.text = getString(R.string.google_play_review)
                }
                else -> {
                    dismiss()
                }
            }
        }

        viewModel.googlePlayReviewEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                requireActivity().rateApp()
            }
        )

        binding.submitButton.setOnClickListener {
            viewModel.onSubmitButtonClicked(binding.smileRating.rating)
        }

        binding.notNowButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(
            object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        val drawable = createMaterialShapeDrawable(bottomSheet)
                        ViewCompat.setBackground(bottomSheet, drawable)
                    }
                }
            }
        )
        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable? {
        val model = ShapeAppearanceModel.builder(
            requireContext(),
            0,
            R.style.CustomShapeAppearanceBottomSheetDialog
        ).build()
        val currentDrawable = bottomSheet.background as MaterialShapeDrawable
        val newDrawable = MaterialShapeDrawable(model)
        with(newDrawable) {
            initializeElevationOverlay(requireContext())
            fillColor = currentDrawable.fillColor
            tintList = currentDrawable.tintList
            elevation = currentDrawable.elevation
            strokeWidth = currentDrawable.strokeWidth
            strokeColor = currentDrawable.strokeColor
        }
        return newDrawable
    }
}
