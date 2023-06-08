package kr.ac.hallym.thelittleprince

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.thelittleprince.databinding.ActivityMainBinding
import kr.ac.hallym.thelittleprince.databinding.ActivityMoonBinding

class Moon : AppCompatActivity() {

    val binding : ActivityMoonBinding by lazy {
        ActivityMoonBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_moon)

        supportActionBar?.hide()


    }
}