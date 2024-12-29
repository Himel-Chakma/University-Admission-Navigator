package com.edgeproject.universityadmissionnavigator2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.edgeproject.universityadmissionnavigator2.databinding.FragmentUniversityBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UniversityFragment : Fragment() {

    private var _binding: FragmentUniversityBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUniversityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UnitAdapter { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        binding.recyclerView.adapter = adapter

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val sscgpa = document.getDouble("sscgpa") ?: return@addOnSuccessListener
            val hscgpa = document.getDouble("hscgpa") ?: return@addOnSuccessListener
            val totalgpa = sscgpa + hscgpa

            // Query all universities
            // Assume adapter is already initialized
            val unitList = mutableListOf<UnitWithUniversity>() // Collect all units
            val tasks = mutableListOf<Task<QuerySnapshot>>()   // Collect all Firebase tasks

// Step 1: Get all universities
            db.collection("universities").get().addOnSuccessListener { universitySnapshot ->
                universitySnapshot.documents.forEach { universityDoc ->
                    val universityName = universityDoc.getString("name") ?: ""
                    val applicationLink = universityDoc.getString("application_link") ?: ""
                    val universityId = universityDoc.id

                    // Step 2: Query units for each university
                    val unitTask = db.collection("universities").document(universityId).collection("units")
                        .whereLessThanOrEqualTo("sscgpa", sscgpa)
                        .whereLessThanOrEqualTo("hscgpa", hscgpa)
                        .whereLessThanOrEqualTo("totalgpa", totalgpa)
                        .get()
                        .addOnSuccessListener { unitSnapshot ->
                            val units = unitSnapshot.documents.mapNotNull { unitDoc ->
                                UnitWithUniversity(
                                    universityName = universityName,
                                    applicationLink = applicationLink,
                                    unitName = unitDoc.getString("unit_name") ?: "",
                                    unitTitle = unitDoc.getString("unit_title") ?: "",
                                    sscgpa = unitDoc.getDouble("sscgpa") ?: 0.0,
                                    hscgpa = unitDoc.getDouble("hscgpa") ?: 0.0,
                                    totalgpa = unitDoc.getDouble("totalgpa") ?: 0.0
                                )
                            }
                            unitList.addAll(units) // Add to the main list
                        }
                    tasks.add(unitTask) // Add task to the list
                }

                // Step 3: Wait for all tasks to complete
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    // Update the RecyclerView
                    Log.d("FinalUnits", "Total units found: ${unitList.size}")
                    adapter.submitList(unitList.toList())
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}