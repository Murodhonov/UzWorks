package dev.goblingroup.uzworks.fragments.main.profile.admin.job_category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.goblingroup.uzworks.databinding.FragmentJobCategoryControlBinding
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class JobCategoryControlFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentJobCategoryControlBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkHelper: NetworkHelper

    private lateinit var jobCategoryViewModel: SecuredJobCategoryViewModel
    private lateinit var jobCategoryViewModelFactory: SecuredJobCategoryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobCategoryControlBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {

        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            JobCategoryControlFragment().apply {
                arguments = Bundle().apply {
//                    putString("ARG_PARAM1", param1)
//                    putString("ARG_PARAM2", param2)
                }
            }
    }
}