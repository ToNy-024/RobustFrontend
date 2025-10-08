package com.example.robustfrontend.ui.group

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Actividad
import com.example.robustfrontend.databinding.ItemActivityCardBinding
import com.example.robustfrontend.ui.activity.CreateActivityActivity
import com.example.robustfrontend.viewmodel.activity.CreateActivityViewModel

/**
 * Adaptador para el RecyclerView que muestra las tarjetas de actividades (aprobadas, en progreso, hechas).
 * Gestiona la visualización de los datos de cada actividad y el menú contextual para editar o eliminar.
 */
class ActivityAdapter(
    private val createActivityViewModel: CreateActivityViewModel,
    private val currentUserId: String,
    private val isUserAdmin: Boolean
) : ListAdapter<Actividad, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    /**
     * Crea y devuelve un ActivityViewHolder, inflando el layout de la tarjeta de actividad.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding, createActivityViewModel, currentUserId, isUserAdmin)
    }

    /**
     * Vincula los datos de una actividad específica a un ViewHolder.
     */
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        holder.itemView.tag = activity // Guarda la actividad en el tag para el swipe
        holder.bind(activity)
    }

    /**
     * ViewHolder para una tarjeta de actividad. Contiene la lógica para vincular los datos
     * y gestionar los eventos de la vista.
     */
    class ActivityViewHolder(
        private val binding: ItemActivityCardBinding,
        private val createActivityViewModel: CreateActivityViewModel,
        private val currentUserId: String,
        private val isUserAdmin: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de un objeto Actividad a los elementos de la vista de la tarjeta
         * y configura el listener de pulsación larga para el menú contextual.
         * @param actividad La actividad a mostrar.
         */
        fun bind(actividad: Actividad) {
            binding.textViewActivityName.text = actividad.nombre
            binding.textViewActivityScore.text = actividad.puntaje.toString()
            binding.textViewActivityCreator.text = itemView.context.getString(R.string.creator_prefix, actividad.creador)

            itemView.setOnLongClickListener {
                val canModify = isUserAdmin || currentUserId == actividad.creador
                if (canModify) {
                    showPopupMenu(actividad)
                }
                true
            }
        }

        /**
         * Muestra un menú contextual con las opciones "Editar" y "Eliminar".
         * @param actividad La actividad a la que se aplicarán las acciones.
         */
        private fun showPopupMenu(actividad: Actividad) {
            val popup = PopupMenu(itemView.context, itemView)
            popup.menuInflater.inflate(R.menu.activity_context_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit_activity -> {
                        val intent = Intent(itemView.context, CreateActivityActivity::class.java).apply {
                            putExtra(CreateActivityActivity.EXTRA_ACTIVITY_ID, actividad.idAct)
                        }
                        itemView.context.startActivity(intent)
                        true
                    }
                    R.id.menu_delete_activity -> {
                        showDeleteConfirmationDialog(actividad)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        /**
         * Muestra un diálogo de confirmación antes de eliminar una actividad.
         * @param actividad La actividad que se va a eliminar.
         */
        private fun showDeleteConfirmationDialog(actividad: Actividad) {
            AlertDialog.Builder(itemView.context)
                .setTitle(itemView.context.getString(R.string.delete_activity_dialog_title))
                .setMessage(itemView.context.getString(R.string.delete_activity_dialog_message))
                .setPositiveButton(itemView.context.getString(R.string.delete)) { _, _ ->
                    createActivityViewModel.deleteActivity(actividad.idAct)
                }
                .setNegativeButton(itemView.context.getString(R.string.cancel), null)
                .show()
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
