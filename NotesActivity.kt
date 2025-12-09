package isim.ia2kotlin.projet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: NoteAdapter
    private var notes = mutableListOf<Note>()
    private var subjectId = 0
    private lateinit var tvAverage: TextView
    private lateinit var tvSubjectName: TextView
    private lateinit var tvCoefficient: TextView
    private lateinit var tvWarning: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        db = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerNotes)
        fabAdd = findViewById(R.id.fabAddNote)
        tvAverage = findViewById(R.id.tvSubjectAverage)
        tvSubjectName = findViewById(R.id.tvSubjectName)
        tvCoefficient = findViewById(R.id.tvCoefficient)
        tvWarning = findViewById(R.id.tvWarning)

        subjectId = intent.getIntExtra("subjectId", 0)
        val subjectName = intent.getStringExtra("subjectName") ?: "Matière"
        val coefficient = intent.getIntExtra("subjectCoefficient", 1)
        val average = intent.getDoubleExtra("subjectAverage", 0.0)

        tvSubjectName.text = subjectName
        tvCoefficient.text = "Coefficient: $coefficient"
        tvAverage.text = "Moyenne: ${"%.2f".format(average)}"

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoteAdapter(notes) { note -> openEditNote(note) }
        recyclerView.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = notes[viewHolder.adapterPosition]
                db.deleteNote(note.id)
                notes.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                displayAverage()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)

        fabAdd.setOnClickListener {
            val i = Intent(this, AddEditNoteActivity::class.java)
            i.putExtra("subjectId", subjectId)
            startActivity(i)
        }
    }

    override fun onResume() {
        super.onResume()
        notes.clear()
        notes.addAll(db.getNotes(subjectId))
        adapter.notifyDataSetChanged()
        displayAverage()
    }

    private fun displayAverage() {
        val avg = db.calculateSubjectAverage(subjectId)
        tvAverage.text = "Moyenne: ${"%.2f".format(avg)}"

        // Afficher un avertissement si DS ou Examen manque
        val notes = db.getNotes(subjectId)
        val hasDS = notes.any { it.type.equals("DS", ignoreCase = true) }
        val hasExamen = notes.any { it.type.equals("Examen", ignoreCase = true) }

        if (!hasDS || !hasExamen) {
            tvWarning.text = " DS et Examen sont obligatoires pour calculer la moyenne"
            tvWarning.visibility = TextView.VISIBLE
        } else {
            tvWarning.visibility = TextView.GONE
        }
    }

    private fun openEditNote(note: Note) {
        val i = Intent(this, AddEditNoteActivity::class.java)
        i.putExtra("noteId", note.id)
        i.putExtra("subjectId", note.subjectId)
        startActivity(i)
    }
}