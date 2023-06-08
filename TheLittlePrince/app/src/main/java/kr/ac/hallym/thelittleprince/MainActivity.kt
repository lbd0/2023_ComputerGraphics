package kr.ac.hallym.thelittleprince

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.thelittleprince.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        setContentView(binding.root)

        binding.cover.setOnClickListener{
            startActivity(Intent(this, Moon::class.java))
        }
    }
}