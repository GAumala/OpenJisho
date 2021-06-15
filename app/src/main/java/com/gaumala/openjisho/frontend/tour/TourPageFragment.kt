package com.gaumala.openjisho.frontend.tour

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R
import com.gaumala.openjisho.databinding.TourPageBinding
import com.gaumala.openjisho.utils.image.MatrixCalculator

class TourPageFragment: Fragment() {
    private lateinit var binding: TourPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val animDrawable = binding.pageArt.drawable as? AnimationDrawable ?: return
        animDrawable.start()
    }

    override fun onStop() {
        super.onStop()

        val animDrawable = binding.pageArt.drawable as? AnimationDrawable ?: return
        animDrawable.stop()
    }

    private fun inflateBinding(inflater: LayoutInflater,
                               container: ViewGroup?): TourPageBinding {
        val index = requireArguments().getInt(INDEX_KEY, 0)
        val binding = TourPageBinding.inflate(inflater, container, false)
        binding.pageArt.matrixCalculator = MatrixCalculator.FitBottom()
        when (index) {
            0 -> {
                binding.pageArt.setImageResource(R.drawable.tour_tabs_animation)
                binding.textTitle.setText(R.string.tour_tabs_title)
                binding.textBody.setText(R.string.tour_tabs_body)
            }
            1 -> {
                binding.pageArt.setImageResource(R.drawable.tour_wildcards_art)
                binding.textTitle.setText(R.string.tour_wildcards_title)
                binding.textBody.setText(R.string.tour_wildcards_body)
            }
            2 -> {
                binding.pageArt.setImageResource(R.drawable.tour_history_art)
                binding.textTitle.setText(R.string.tour_history_title)
                binding.textBody.setText(R.string.tour_history_body)
            }
            else -> {
                binding.pageArt.setImageResource(R.drawable.tour_radicals_art)
                binding.textTitle.setText(R.string.tour_radicals_title)
                binding.textBody.setText(R.string.tour_radicals_body)
            }
        }
        return binding
    }

    companion object {
        const val INDEX_KEY = "index"

        fun newInstance(index: Int): TourPageFragment {
            val args = Bundle()
            args.putInt(INDEX_KEY, index)

            val f = TourPageFragment()
            f.arguments = args
            return f
        }
    }
}