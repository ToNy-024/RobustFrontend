package com.example.robustfrontend.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Actividad
import com.example.robustfrontend.databinding.ItemPendingActivityCardBinding

/**
 * Adaptador para el RecyclerView que muestra las tarjetas de actividades pendientes de votación.
 * Gestiona la visualización de los datos y los eventos de clic para aprobar o rechazar una actividad.
 */
class PendingActivityAdapter(
    private val onApproveClicked: (Actividad) -> Unit,
    private val onRejectClicked: (Actividad) -> Unit
) : ListAdapter<Actividad, PendingActivityAdapter.PendingActivityViewHolder>(ActivityDiffCallback()) {

    /**
     * Crea y devuelve un PendingActivityViewHolder, inflando el layout de la tarjeta de votación.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingActivityViewHolder {
        val binding = ItemPendingActivityCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingActivityViewHolder(binding, onApproveClicked, onRejectClicked)
    }

    /**
     * Vincula los datos de una actividad pendiente específica a un ViewHolder.
     */
    override fun onBindViewHolder(holder: PendingActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder para una tarjeta de actividad pendiente. Contiene la lógica para vincular los datos
     * y gestionar los eventos de los botones de votación.
     */
    class PendingActivityViewHolder(
        private val binding: ItemPendingActivityCardBinding,
        private val onApproveClicked: (Actividad) -> Unit,
        private val onRejectClicked: (Actividad) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de un objeto Actividad a los elementos de la vista de la tarjeta.
         * Muestra el estado de la votación y habilita/deshabilita los botones según si el usuario ya ha votado.
         * @param actividad La actividad pendiente a mostrar.
         */
        fun bind(actividad: Actividad) {
            binding.textViewActivityName.text = actividad.nombre
            binding.textViewActivityCreator.text = itemView.context.getString(R.string.creator_prefix, actividad.creador)

            // Muestra el estado de la votación (ej. "Votos: 3/5")
            val voteStatus = itemView.context.getString(R.string.voting_card_status, actividad.votos_a_favor ?: 0, actividad.total_miembros_grupo ?: 0)
            binding.textViewVoteStatus.text = voteStatus

            // Deshabilita los botones si el usuario actual ya ha votado
            val hasVoted = actividad.ha_votado_usuario ?: false
            binding.buttonApprove.isEnabled = !hasVoted
            binding.buttonReject.isEnabled = !hasVoted

            binding.buttonApprove.setOnClickListener { onApproveClicked(actividad) }
            binding.buttonReject.setOnClickListener { onRejectClicked(actividad) }
        }
    }

    /**
     * Callback para calcular las diferencias entre dos listas de actividades, permitiendo
     * animaciones eficientes en el RecyclerView.
     */
    class ActivityDiffCallback : DiffUtil.ItemCallback<Actividad>() {
        override fun areItemsTheSame(oldItem: Actividad, newItem: Actividad): Boolean {
            return oldItem.idAct == newItem.idAct
        }

        override fun areContentsTheSame(oldItem: Actividad, newItem: Actividad): Boolean {
            return oldItem == newItem
        }
    }
}
