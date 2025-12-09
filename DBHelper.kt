package isim.ia2kotlin.projet

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "SchoolDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE subject (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                coefficient INTEGER NOT NULL
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE note (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id INTEGER NOT NULL,
                type TEXT,
                value REAL,
                FOREIGN KEY(subject_id) REFERENCES subject(id) ON DELETE CASCADE
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS note")
        db?.execSQL("DROP TABLE IF EXISTS subject")
        onCreate(db)
    }

    fun addSubject(s: Subject) {
        val db = this.writableDatabase
        db.execSQL("INSERT INTO subject(name,coefficient) VALUES(?,?)",
            arrayOf(s.name, s.coefficient))
        db.close()
    }

    fun updateSubject(s: Subject) {
        val db = this.writableDatabase
        db.execSQL("UPDATE subject SET name=?, coefficient=? WHERE id=?",
            arrayOf(s.name, s.coefficient, s.id))
        db.close()
    }

    fun deleteSubject(id: Int) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM subject WHERE id=?", arrayOf(id))
        db.close()
    }

    fun getAllSubjects(): MutableList<Subject> {
        val list = mutableListOf<Subject>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, coefficient FROM subject", null)
        while (cursor.moveToNext()) {
            list.add(Subject(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)))
        }
        cursor.close()
        db.close()
        return list
    }


    fun getAllSubjectsWithAverage(): MutableList<Subject> {
        val list = mutableListOf<Subject>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, coefficient FROM subject", null)
        while (cursor.moveToNext()) {
            val subject = Subject(cursor.getInt(0), cursor.getString(1), cursor.getInt(2))
            subject.average = calculateSubjectAverage(subject.id)
            list.add(subject)
        }
        cursor.close()
        db.close()
        return list
    }

    fun addNote(n: Note) {
        val db = this.writableDatabase
        db.execSQL("INSERT INTO note(subject_id,type,value) VALUES(?,?,?)",
            arrayOf(n.subjectId, n.type, n.value))
        db.close()
    }

    fun updateNote(n: Note) {
        val db = this.writableDatabase
        db.execSQL("UPDATE note SET type=?, value=? WHERE id=?",
            arrayOf(n.type, n.value, n.id))
        db.close()
    }

    fun deleteNote(id: Int) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM note WHERE id=?", arrayOf(id))
        db.close()
    }

    fun getNotes(subjectId: Int): MutableList<Note> {
        val list = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, subject_id, type, value FROM note WHERE subject_id=?",
            arrayOf(subjectId.toString()))
        while (cursor.moveToNext()) {
            list.add(Note(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getDouble(3)))
        }
        cursor.close()
        db.close()
        return list
    }


    fun doesNoteTypeExist(subjectId: Int, noteType: String): Boolean {
        val notes = getNotes(subjectId)
        return notes.any { it.type.equals(noteType, ignoreCase = true) }
    }


    fun getNoteByType(subjectId: Int, noteType: String): Note? {
        val notes = getNotes(subjectId)
        return notes.find { it.type.equals(noteType, ignoreCase = true) }
    }


    fun calculateSubjectAverage(subjectId: Int): Double {
        val notes = getNotes(subjectId)
        if (notes.isEmpty()) return 0.0


        val notesByType = notes.groupBy { it.type.uppercase() }

        val hasDS = notesByType.keys.any { it.contains("DS", ignoreCase = true) }
        val hasExamen = notesByType.keys.any { it.contains("EXAMEN", ignoreCase = true) }
        val hasTP = notesByType.keys.any { it.contains("TP", ignoreCase = true) }
        val hasOral = notesByType.keys.any { it.contains("ORAL", ignoreCase = true) }

        if (!hasDS && !hasExamen) {
            return 0.0
        }

        if (!hasDS || !hasExamen) {
            return 0.0
        }

        val moyenneDS = notesByType
            .filter { it.key.contains("DS", ignoreCase = true) }
            .flatMap { it.value }
            .firstOrNull()?.value ?: 0.0

        val moyenneExamen = notesByType
            .filter { it.key.contains("EXAMEN", ignoreCase = true) }
            .flatMap { it.value }
            .firstOrNull()?.value ?: 0.0

        var moyenneTP = 0.0
        var moyenneOral = 0.0


        var coefDS = 0.0
        var coefExamen = 0.0
        var coefTP = 0.0
        var coefOral = 0.0


        when {
            hasDS && hasExamen && hasTP && hasOral -> {

                moyenneTP = notesByType
                    .filter { it.key.contains("TP", ignoreCase = true) }
                    .flatMap { it.value }
                    .firstOrNull()?.value ?: 0.0

                moyenneOral = notesByType
                    .filter { it.key.contains("ORAL", ignoreCase = true) }
                    .flatMap { it.value }
                    .firstOrNull()?.value ?: 0.0

                coefDS = 0.2
                coefExamen = 0.5
                coefTP = 0.2
                coefOral = 0.1
            }

            hasDS && hasExamen && hasTP -> {

                moyenneTP = notesByType
                    .filter { it.key.contains("TP", ignoreCase = true) }
                    .flatMap { it.value }
                    .firstOrNull()?.value ?: 0.0

                coefDS = 0.1
                coefExamen = 0.65
                coefTP = 0.25
            }

            hasDS && hasExamen && hasOral -> {

                moyenneOral = notesByType
                    .filter { it.key.contains("ORAL", ignoreCase = true) }
                    .flatMap { it.value }
                    .firstOrNull()?.value ?: 0.0

                coefDS = 0.2
                coefExamen = 0.7
                coefOral = 0.1
            }

            hasDS && hasExamen -> {

                coefDS = 0.3
                coefExamen = 0.7
            }

            else -> {
                return 0.0
            }
        }

        val totalCoef = coefDS + coefExamen + coefTP + coefOral
        if (totalCoef == 0.0) return 0.0

        val moyennePonderee = (moyenneDS * coefDS +
                moyenneExamen * coefExamen +
                moyenneTP * coefTP +
                moyenneOral * coefOral) / totalCoef

        return moyennePonderee
    }
}