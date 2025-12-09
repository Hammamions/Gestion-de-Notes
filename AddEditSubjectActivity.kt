package isim.ia2kotlin.projet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddEditSubjectActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var etName: EditText
    private lateinit var etCoef: EditText
    private var subjectId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_subject)

        db = DBHelper(this)
        etName = findViewById(R.id.etSubjectName)
        etCoef = findViewById(R.id.etSubjectCoef)
        val btn = findViewById<Button>(R.id.btnSaveSubject)

        subjectId = intent.getIntExtra("subjectId", 0)
        if (subjectId != 0) {
            val sub = db.getAllSubjects().find { it.id == subjectId }
            sub?.let {
                etName.setText(it.name)
                etCoef.setText(it.coefficient.toString())
            }
        }

        btn.setOnClickListener {
            val name = etName.text.toString().trim()
            val coef = etCoef.text.toString().toIntOrNull() ?: 1
            if (subjectId == 0) {
                db.addSubject(Subject(name = name, coefficient = coef))
            } else {
                db.updateSubject(Subject(subjectId, name, coef))
            }
            finish()
        }
    }
}