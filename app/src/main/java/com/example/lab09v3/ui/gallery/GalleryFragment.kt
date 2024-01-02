package com.example.lab09v3.ui.gallery

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lab09v3.R
import com.example.lab09v3.databinding.FragmentGalleryBinding
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sendLoginButton: Button = binding.SendLogin
        val describeLoginEditText: EditText = binding.describeLogin
        val savedLogin = readLoginFromSharedPreferences()
        describeLoginEditText.setText(savedLogin)

        sendLoginButton.setOnClickListener {
            val enteredLogin = describeLoginEditText.text.toString()

            if (isValidLogin(enteredLogin)) {
                saveLoginToSharedPreferences(enteredLogin)

                findNavController().popBackStack()
                findNavController().navigate(R.id.nav_home)
            } else {
                saveLoginData(enteredLogin)
                Toast.makeText(requireContext(), "Nieprawidłowy login", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun readLoginFromSharedPreferences(): String {
        val sharedPreferences: SharedPreferences =
            requireActivity().getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getString("userLogin", "") ?: ""
    }

    private fun saveLoginData(loginOutput: String) {
        val sharedPreferences: SharedPreferences =
            requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("key_login_application", loginOutput)
        editor.apply()
    }

    private fun saveLoginToSharedPreferences(userLogin: String) {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userLogin", userLogin).apply()
        Log.d("UserLoginDebug", "Zalogowano użytkownika: $userLogin")
    }

    private fun isValidLogin(login: String): Boolean {
        return login.equals("Kamil", ignoreCase = true)
    }

    private fun resetSharedPreferencesData() {
        val sharedPreferences: SharedPreferences =
            requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
