package com.example.robustfrontend.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Actividad
import com.example.robustfrontend.data.model.Grupo
import com.example.robustfrontend.databinding.ActivityGroupBinding
import com.example.robustfrontend.ui.activity.CreateActivityActivity
import com.example.robustfrontend.viewmodel.Group.GroupViewModel
import com.example.robustfrontend.viewmodel.activity.CreateActivityViewModel
import com.google.firebase.auth.FirebaseAuth

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding
    private val groupViewModel: GroupViewModel by viewModels()
    private val createActivityViewModel: CreateActivityViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var pendingAdapter: PendingActivityAdapter
    private lateinit var todoAdapter: ActivityAdapter
    private lateinit var inProgressAdapter: ActivityAdapter
    private lateinit var doneAdapter: ActivityAdapter

    /**
     * Punto de entrada de la actividad. Llama a los métodos de configuración
     * de RecyclerViews, observadores y listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupObservers()
        setupListeners()
    }

    /**
     * Se llama cuando la actividad vuelve a primer plano. Recarga los datos del usuario
     * para asegurar que la información mostrada está actualizada.
     */
    override fun onResume() {
        super.onResume()
        firebaseAuth.currentUser?.uid?.let { groupViewModel.loadUserData(it) }
    }

    /**
     * Configura los cuatro RecyclerViews (Pendiente, Por Hacer, En Progreso, Hecho),
     * inicializando sus adaptadores y LayoutManagers.
     */
    private fun setupRecyclerViews() {
        val currentUserId = firebaseAuth.currentUser?.uid ?: ""
        val isUserAdmin = groupViewModel.usuario.value?.esAdmin ?: false

        pendingAdapter = PendingActivityAdapter(
            onApproveClicked = { act -> firebaseAuth.currentUser?.uid?.let { uid -> groupViewModel.voteForActivity(act.idAct, uid, true) } },
            onRejectClicked = { act -> firebaseAuth.currentUser?.uid?.let { uid -> groupViewModel.voteForActivity(act.idAct, uid, false) } }
        )

        todoAdapter = ActivityAdapter(createActivityViewModel, currentUserId, isUserAdmin)
        inProgressAdapter = ActivityAdapter(createActivityViewModel, currentUserId, isUserAdmin)
        doneAdapter = ActivityAdapter(createActivityViewModel, currentUserId, isUserAdmin)

        binding.recyclerViewPending.apply { layoutManager = LinearLayoutManager(this@GroupActivity); adapter = pendingAdapter }
        binding.recyclerViewTodo.apply { layoutManager = LinearLayoutManager(this@GroupActivity); adapter = todoAdapter }
        binding.recyclerViewInProgress.apply { layoutManager = LinearLayoutManager(this@GroupActivity); adapter = inProgressAdapter }
        binding.recyclerViewDone.apply { layoutManager = LinearLayoutManager(this@GroupActivity); adapter = doneAdapter }

        setupSwipeToMove()
    }

    /**
     * Configura los observadores de LiveData para reaccionar a los cambios en los ViewModels.
     * Actualiza la UI cuando se cargan datos, se completan operaciones o se reciben mensajes.
     */
    private fun setupObservers() {
        groupViewModel.toastMessage.observe(this, Observer { messageResId ->
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        })
        createActivityViewModel.toastMessage.observe(this, Observer { messageResId ->
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        })

        groupViewModel.grupo.observe(this, Observer { grupo ->
            if (grupo != null) {
                showGroupInfo(grupo)
                setupRecyclerViews() // Recarga los adaptadores para asegurar permisos correctos
            } else {
                showNoGroupUI()
            }
        })

        groupViewModel.joinSuccess.observe(this, Observer { success ->
            if (success) {
                firebaseAuth.currentUser?.uid?.let { groupViewModel.loadUserData(it) }
                groupViewModel.onJoinSuccessComplete()
            }
        })

        groupViewModel.groupDeleted.observe(this, Observer { isDeleted ->
            if (isDeleted) {
                firebaseAuth.currentUser?.uid?.let { groupViewModel.loadUserData(it) }
                groupViewModel.onGroupDeletedComplete()
            }
        })

        createActivityViewModel.operationComplete.observe(this, Observer { isComplete ->
            if (isComplete) {
                firebaseAuth.currentUser?.uid?.let { groupViewModel.loadUserData(it) } // Recarga los datos del grupo
                createActivityViewModel.onOperationCompleteHandled()
            }
        })

        groupViewModel.pendingActivities.observe(this, Observer { pendingAdapter.submitList(it) })
        groupViewModel.todoActivities.observe(this, Observer { todoAdapter.submitList(it) })
        groupViewModel.inProgressActivities.observe(this, Observer { inProgressAdapter.submitList(it) })
        groupViewModel.doneActivities.observe(this, Observer { doneAdapter.submitList(it) })
    }

    /**
     * Muestra la información del grupo y ajusta la visibilidad de los botones de edición/eliminación
     * basándose en si el usuario actual es el creador del grupo.
     * @param grupo El objeto Grupo con la información a mostrar.
     */
    private fun showGroupInfo(grupo: Grupo) {
        binding.groupContentLayout.visibility = View.VISIBLE
        binding.layoutNoGroup.visibility = View.GONE
        binding.textViewGroupName.text = grupo.nombre
        binding.textViewGroupDescription.text = grupo.descripcion

        val isCreator = firebaseAuth.currentUser?.uid == grupo.creador
        binding.buttonEditGroup.visibility = if (isCreator) View.VISIBLE else View.GONE
        binding.buttonDeleteGroup.visibility = if (isCreator) View.VISIBLE else View.GONE
    }

    /**
     * Muestra la interfaz para usuarios que no pertenecen a ningún grupo, ocultando la vista principal del grupo.
     */
    private fun showNoGroupUI() {
        binding.groupContentLayout.visibility = View.GONE
        binding.layoutNoGroup.visibility = View.VISIBLE
    }

    /**
     * Configura los listeners para los botones de la actividad (unirse, crear, editar, eliminar grupo y proponer actividad).
     */
    private fun setupListeners() {
        binding.buttonJoinGroup.setOnClickListener { showJoinGroupDialog() }
        binding.buttonNavigateToCreate.setOnClickListener { startActivity(Intent(this, CreateGroupActivity::class.java)) }

        binding.buttonEditGroup.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java).apply {
                putExtra(CreateGroupActivity.EXTRA_GROUP_ID, groupViewModel.grupo.value?.idGru)
            }
            startActivity(intent)
        }

        binding.buttonDeleteGroup.setOnClickListener { showDeleteGroupConfirmationDialog() }

        binding.fabProposeActivity.setOnClickListener {
            val groupId = groupViewModel.grupo.value?.idGru
            val isAdmin = groupViewModel.usuario.value?.esAdmin ?: false
            if (groupId != null) {
                val intent = Intent(this, CreateActivityActivity::class.java).apply {
                    putExtra(CreateActivityActivity.EXTRA_GROUP_ID, groupId)
                    putExtra(CreateActivityActivity.EXTRA_IS_ADMIN, isAdmin)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.group_must_belong_to_group_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Muestra un diálogo de confirmación antes de proceder con la eliminación de un grupo.
     */
    private fun showDeleteGroupConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.group_delete_dialog_title))
            .setMessage(getString(R.string.group_delete_dialog_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ -> groupViewModel.grupo.value?.idGru?.let { groupViewModel.deleteGroup(it) } }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * Configura el `ItemTouchHelper` para permitir el gesto de deslizar (swipe) en las tarjetas de actividad
     * y así cambiar su estado (ej. de "Por Hacer" a "En Progreso").
     */
    private fun setupSwipeToMove() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val activity = viewHolder.itemView.tag as? Actividad ?: return

                val newStatus = when (viewHolder.itemView.parent as RecyclerView) {
                    binding.recyclerViewTodo -> if (direction == ItemTouchHelper.RIGHT) "en_progreso" else null
                    binding.recyclerViewInProgress -> if (direction == ItemTouchHelper.RIGHT) "hecha" else "aprobada"
                    binding.recyclerViewDone -> if (direction == ItemTouchHelper.LEFT) "en_progreso" else null
                    else -> null
                }

                if (newStatus != null) {
                    groupViewModel.updateActivityStatus(activity.idAct, newStatus)
                } else {
                    viewHolder.bindingAdapterPosition.let {
                        if (it != RecyclerView.NO_POSITION) {
                            (viewHolder.bindingAdapter as? ActivityAdapter)?.notifyItemChanged(it)
                        }
                    }
                }
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerViewTodo)
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerViewInProgress)
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerViewDone)
    }

    /**
     * Muestra un diálogo que permite al usuario introducir un código de invitación para unirse a un grupo.
     */
    private fun showJoinGroupDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.group_join_dialog_title))

        val input = EditText(this)
        input.hint = getString(R.string.group_join_dialog_hint)
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.group_join_dialog_action)) { dialog, _ ->
            val code = input.text.toString().trim()
            if (code.isNotEmpty()) {
                firebaseAuth.currentUser?.uid?.let { groupViewModel.joinGroup(it, code) }
            } else {
                Toast.makeText(this, getString(R.string.group_join_code_empty_error), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}
