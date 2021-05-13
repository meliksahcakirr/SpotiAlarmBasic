package com.meliksahcakir.spotialarm.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.BuildConfig
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.databinding.FragmentMainBinding
import com.meliksahcakir.spotialarm.navigate
import com.meliksahcakir.spotialarm.setNearestAlarm
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment(), AlarmListener {

    private lateinit var alarmAdapter: AlarmAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    val viewModel: MainViewModel by sharedViewModel()

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            viewModel.refreshData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        requireActivity().registerReceiver(receiver, IntentFilter(AlarmReceiver.ACTION_FINISH))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmAdapter = AlarmAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alarmAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        viewModel.alarms.observe(viewLifecycleOwner) {
            val list = it ?: emptyList()
            alarmAdapter.submitList(list)
            binding.recyclerView.isVisible = list.isNotEmpty()
            binding.emptyGroup.isVisible = list.isEmpty()
        }

        viewModel.nearestAlarm.observe(viewLifecycleOwner) {
            binding.setNearestAlarm(it)
        }

        viewModel.goToEditPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToEditFragment(it)
            }
        )

        viewModel.goToPreferencesPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToPreferencesFragment()
            }
        )

        viewModel.goToFeedbackPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToFeedbackFragment()
            }
        )

        viewModel.goToReviewPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToRatingFragment()
            }
        )

        binding.fab.setOnClickListener {
            viewModel.onAlarmSelected()
        }

        binding.menuButton.setOnClickListener {
            if (!binding.drawerLayout.isOpen) {
                binding.drawerLayout.open()
            } else {
                binding.drawerLayout.close()
            }
        }
        viewModel.refreshData()

        setupDrawerLayout()
    }

    override fun onClick(alarmId: Int) {
        viewModel.onAlarmSelected(alarmId)
    }

    override fun onAlarmEnableStatusChanged(alarmId: Int, enabled: Boolean) {
        viewModel.onAlarmEnableStatusChanged(alarmId, enabled)
    }

    private fun navigateToEditFragment(id: Int) {
        val direction = MainFragmentDirections.actionMainFragmentToAlarmEditFragment(id)
        navigate(direction)
    }

    private fun navigateToPreferencesFragment() {
        val direction = MainFragmentDirections.actionMainFragmentToPreferencesFragment()
        navigate(direction)
    }

    private fun navigateToFeedbackFragment() {
        val direction = MainFragmentDirections.actionMainFragmentToFeedbackFragment()
        navigate(direction)
    }

    private fun navigateToRatingFragment() {
        val direction = MainFragmentDirections.actionMainFragmentToRatingFragment()
        navigate(direction)
    }

    private fun setupDrawerLayout() {
        val sb = StringBuilder()
        sb.append(getString(R.string.app_name)).append(" ").append(BuildConfig.VERSION_NAME)
        binding.drawerContentView.appInfoTextView.text = sb.toString()

        binding.drawerContentView.preferencesCardView.setOnClickListener {
            viewModel.onPreferencesButtonClicked()
            binding.drawerLayout.close()
        }
        binding.drawerContentView.disableCardView.setOnClickListener {
            binding.drawerLayout.close()
            viewModel.onDisableButtonClicked()
        }
        binding.drawerContentView.deleteCardView.setOnClickListener {
            binding.drawerLayout.close()
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setCancelable(true)
                .setTitle(R.string.warning)
                .setMessage(getString(R.string.delete_all_warning))
                .setPositiveButton(R.string.delete_all) { dialog, _ ->
                    dialog.cancel()
                    viewModel.onDeleteAllButtonClicked()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }.create()
            dialog.show()
        }
        binding.drawerContentView.feedbackCardView.setOnClickListener {
            viewModel.onFeedbackButtonClicked()
            binding.drawerLayout.close()
        }
        binding.drawerContentView.googlePlayReviewCardView.setOnClickListener {
            viewModel.onReviewButtonClicked()
            binding.drawerLayout.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(receiver)
        _binding = null
    }
}
