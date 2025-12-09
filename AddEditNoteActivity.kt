package isim.ia2kotlin.projet

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditNoteActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var etValue: EditText
    private lateinit var spType: Spinner
    private var noteId = 0
    private var subjectId = 0
    private var originalNoteType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        db = DBHelper(this)
        etValue = findViewById(R.id.etNoteValue)
        spType = findViewById(R.id.spNoteType)
        val btn = findViewById<Button>(R.id.btnSaveNote)

        noteId = intent.getIntExtra("noteId", 0)
        subjectId = intent.getIntExtra("subjectId", 0)

        // Configurer le Spinner avec les types autorisés
        val noteTypes = arrayOf("DS", "Examen", "TP", "Oral")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, noteTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapter

        if (noteId != 0) {
            val notes = db.getNotes(subjectId)
            val note = notes.find { it.id == noteId }
            note?.let {
                val position = noteTypes.indexOfFirst { type ->
                    type.equals(note.type, ignoreCase = true)
                }
                if (position >= 0) {
                    spType.setSelection(position)
                }
                etValue.setText(it.value.toString())
                originalNoteType = it.type
            }
        } else {
            spType.setSelection(0)
        }

        btn.setOnClickListener {
            val type = spType.selectedItem.toString()
            val value = etValue.text.toString().toDoubleOrNull() ?: 0.0

            // Validation
            if (etValue.text.toString().trim().isEmpty()) {
                etValue.error = "Veuillez entrer une valeur"
                return@setOnClickListener
            }

            if (value < 0.0 || value > 20.0) {
                etValue.error = "La note doit être entre 0 et 20"
                return@setOnClickListener
            }

            if (noteId == 0) {
                if (db.doesNoteTypeExist(subjectId, type)) {
                    Toast.makeText(
                        this,
                        " existe déjà. Veuillez le modifier ou le supprimer d'abord.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                db.addNote(Note(subjectId = subjectId, type = type, value = value))
                Toast.makeText(this, "Note ajoutée avec succès", Toast.LENGTH_SHORT).show()
            } else {
                if (type != originalNoteType && db.doesNoteTypeExist(subjectId, type)) {
                    Toast.makeText(
                        this,
                        " existe déjà. Veuillez choisir un autre type ou modifier l'existant.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                db.updateNote(Note(noteId, subjectId, type, value))
                Toast.makeText(this, "Note modifiée avec succès", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
