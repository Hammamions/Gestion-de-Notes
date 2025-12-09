package isim.ia2kotlin.projet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val items: List<Note>,
    private val onClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.textType)
        val tvValue: TextView = view.findViewById(R.id.textValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        holder.tvType.text = n.type
        holder.tvValue.text = "%.2f / 20".format(n.value)
        holder.itemView.setOnClickListener { onClick(n) }
    }

    override fun getItemCount(): Int = items.size
}
