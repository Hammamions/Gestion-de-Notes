package isim.ia2kotlin.projet


data class Note(
    var id: Int = 0,
    var subjectId: Int,
    var type: String = "Examen",
    var value: Double = 0.0
)
