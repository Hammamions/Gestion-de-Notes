package isim.ia2kotlin.projet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SubjectListActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: SubjectAdapter
    private var subjects = mutableListOf<Subject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_list)

        db = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerSubjects)
        fabAdd = findViewById(R.id.fabAddSubject)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SubjectAdapter(subjects) { subject -> openNotes(subject) }
        recyclerView.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val subj = subjects[viewHolder.adapterPosition]
                db.deleteSubject(subj.id)
                subjects.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditSubjectActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        subjects.clear()
        subjects.addAll(db.getAllSubjectsWithAverage())
        adapter.notifyDataSetChanged()
    }

    private fun openNotes(subject: Subject) {
        val i = Intent(this, NotesActivity::class.java)
        i.putExtra("subjectId", subject.id)
        i.putExtra("subjectName", subject.name)
        i.putExtra("subjectCoefficient", subject.coefficient)
        i.putExtra("subjectAverage", subject.average)
        startActivity(i)
    }
}