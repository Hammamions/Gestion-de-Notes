package isim.ia2kotlin.projet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var tvAverage: TextView
    private lateinit var tvResult: TextView
    private lateinit var btnSubjects: Button
    private lateinit var btnShare: Button
    private lateinit var tvSubjectsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DBHelper(this)
        tvAverage = findViewById(R.id.txtGeneralAverage)
        tvResult = findViewById(R.id.txtResult)
        btnSubjects = findViewById(R.id.btnSubjects)
        btnShare = findViewById(R.id.btnShare)
        tvSubjectsCount = findViewById(R.id.txtSubjectsCount)

        btnSubjects.setOnClickListener {
            startActivity(Intent(this, SubjectListActivity::class.java))
        }

        btnShare.setOnClickListener {
            val avg = calculateGeneralAverage()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Ma moyenne générale est %.2f".format(avg))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Partager via"))
        }
    }

    override fun onResume() {
        super.onResume()
        displayStats()
    }

    private fun calculateGeneralAverage(): Double {
        val subjects = db.getAllSubjectsWithAverage()
        val totalCoef = subjects.sumOf { it.coefficient }
        if (totalCoef == 0) return 0.0
        val weighted = subjects.sumOf { subj -> subj.average * subj.coefficient }
        return weighted / totalCoef
    }

    private fun displayStats() {
        val subjects = db.getAllSubjectsWithAverage()
        val avg = calculateGeneralAverage()

        tvAverage.text = "%.2f".format(avg)
        tvSubjectsCount.text = "Matières: ${subjects.size}"

        val message = when {
            subjects.isEmpty() -> "Ajoutez des matières"
            avg >= 10 -> "Admis "
            avg >= 8 -> "Contrôle "
            else -> "Redoublement "
        }
        tvResult.text = message
    }
}