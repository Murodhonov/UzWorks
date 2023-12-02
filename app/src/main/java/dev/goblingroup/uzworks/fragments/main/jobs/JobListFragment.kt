package dev.goblingroup.uzworks.fragments.main.jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.goblingroup.uzworks.adapters.rv_adapters.WorksAdapter
import dev.goblingroup.uzworks.databinding.FragmentJobListBinding

class JobListFragment : Fragment() {

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!!

    private var jobClickListener: OnJobClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val title = arguments?.getString("title")
            when (title) {
                "Barcha" -> {
                    emptyLayout.visibility = View.GONE
                    recommendedWorkAnnouncementsRv.visibility = View.VISIBLE
                }

                "Saqlanganlar" -> {
                    emptyLayout.visibility = View.VISIBLE
                    recommendedWorkAnnouncementsRv.visibility = View.GONE
                }
            }

            recommendedWorkAnnouncementsRv.adapter = WorksAdapter { position ->
                jobClickListener?.onJobClick(position)
            }
        }
    }

    interface OnJobClickListener {
        fun onJobClick(position: Int)
    }

    fun setOnJobClickListener(listener: OnJobClickListener) {
        jobClickListener = listener
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            JobListFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}