package com.edgeproject.universityadmissionnavigator2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.edgeproject.universityadmissionnavigator2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).get().addOnSuccessListener { documentSnapshot ->
            binding.apply {
                tvemail.setText(documentSnapshot.getString("email"))
                tvpassword.setText(documentSnapshot.getString("password"))
                tvsscgpa.setText(documentSnapshot.getDouble("sscgpa")?.toString() ?: "")
                tvhscgpa.setText(documentSnapshot.getDouble("hscgpa")?.toString() ?: "")
            }
        }

        binding.signUpButton.setOnClickListener {
            val updatedData = mapOf(
                "email" to binding.tvemail.text.toString(),
                "password" to binding.tvpassword.text.toString(),
                "sscgpa" to binding.tvsscgpa.text.toString().toDoubleOrNull(),
                "hscgpa" to binding.tvhscgpa.text.toString().toDoubleOrNull()
            )

            db.collection("users").document(userId).update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}