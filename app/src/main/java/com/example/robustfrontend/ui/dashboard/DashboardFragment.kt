package com.example.robustfrontend.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.robustfrontend.R
import com.example.robustfrontend.databinding.FragmentDashboardBinding
import com.example.robustfrontend.viewmodel.Dashboard.ChartUiData
import com.example.robustfrontend.viewmodel.Dashboard.DashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by activityViewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            activity?.finish()
            return
        }

        val welcomeMessage = getString(R.string.dashboard_welcome, currentUser.displayName?.split(" ")?.get(0) ?: getString(R.string.user_default_name))
        binding.textViewWelcome.text = welcomeMessage

        setupBarChart()
        setupObservers()

        viewModel.fetchUserActivityScores(currentUser.uid)
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)
            legend.isEnabled = false

            // --- Configuración del Eje X ---
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                isGranularityEnabled = true
                textColor = ContextCompat.getColor(context, R.color.md_theme_light_onSurfaceVariant)
            }

            // --- Configuración del Eje Y ---
            axisLeft.apply {
                axisMinimum = 0f
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(context, R.color.md_theme_light_surfaceVariant)
                textColor = ContextCompat.getColor(context, R.color.md_theme_light_onSurfaceVariant)
            }
            axisRight.isEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.chartData.observe(viewLifecycleOwner, Observer { chartData ->
            if (chartData.entries.isNotEmpty()) {
                loadChartData(chartData)
            } else {
                binding.barChart.clear()
                binding.barChart.invalidate()
            }
        })
    }

    private fun loadChartData(chartData: ChartUiData) {
        // Asigna las etiquetas al eje X
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(chartData.labels)

        val dataSet = BarDataSet(chartData.entries, "Puntaje Diario").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.md_theme_light_onSurface)
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.animateY(1200)
        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
