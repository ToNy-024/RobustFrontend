package com.example.robustfrontend.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Usuario
import com.example.robustfrontend.databinding.ItemUserBinding

/**
 * Adaptador para el RecyclerView que muestra la lista de usuarios en la pantalla de administración.
 */
class UserAdapter(private val onUserClicked: (Usuario) -> Unit) : ListAdapter<Usuario, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    /**
     * Crea y devuelve un UserViewHolder, inflando el layout del item de usuario.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onUserClicked)
    }

    /**
     * Vincula los datos de un usuario específico a un ViewHolder.
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder para un item de usuario. Contiene la lógica para vincular los datos
     * del usuario a la vista.
     */
    class UserViewHolder(private val binding: ItemUserBinding, private val onUserClicked: (Usuario) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Vincula los datos de un objeto Usuario a los elementos de la vista.
         * Carga el avatar del usuario usando Coil y muestra un chip si es administrador.
         * @param usuario El usuario a mostrar.
         */
        fun bind(usuario: Usuario) {
            binding.textViewUserName.text = usuario.nombre

            // Carga la imagen del avatar del usuario con Coil
            binding.imageViewUserAvatar.load(usuario.imagen) {
                crossfade(true)
                placeholder(R.drawable.ic_avatar_placeholder) // Imagen mientras carga
                error(R.drawable.ic_avatar_placeholder) // Imagen si hay error
            }

            // Muestra u oculta el chip de "Admin" según el estado del usuario
            binding.chipAdminStatus.visibility = if (usuario.esAdmin) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onUserClicked(usuario) }
        }
    }

    /**
     * Callback para calcular las diferencias entre dos listas de usuarios, permitiendo
     * animaciones eficientes en el RecyclerView.
     */
    class UserDiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.idUsu == newItem.idUsu
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem
        }
    }
}
