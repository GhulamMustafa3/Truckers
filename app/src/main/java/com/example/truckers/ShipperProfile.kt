package com.example.truckers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.truckers.databinding.FragmentShipperProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ShipperProfile : Fragment() {

    private var _binding: FragmentShipperProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShipperProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.editIcon.setOnClickListener { openGallery() }
        binding.signout.setOnClickListener { signOutUser() }
        binding.ShipperBroker.setOnClickListener { switchProfile() }

        fetchUserData()

        return root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                binding.imageView.setImageURI(imageUri) // Ensure XML ID matches here
            }
        }

    private fun fetchUserData() {
        auth.currentUser?.uid?.let { userId ->
            val userRef = database.child("shippers").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.email.text = snapshot.child("email").value.toString()
                        binding.phone.text = snapshot.child("phone").value.toString()
                    } else {
                        showToast("User data not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Failed to load data: ${error.message}")
                }
            })
        } ?: showToast("No user logged in")
    }

    private fun signOutUser() {
        auth.signOut()
        startActivity(Intent(requireContext(), Login::class.java))
        requireActivity().finish()
    }

    private fun switchProfile() {
        showToast("Switching Profile...")
        startActivity(Intent(requireContext(), Login::class.java))
        requireActivity().finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
