package isim.ia2kotlin.projet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectAdapter(
    private val items: List<Subject>,
    private val onClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.txtName)
        val tvCoef: TextView = view.findViewById(R.id.txtCoef)
        val tvAverage: TextView = view.findViewById(R.id.txtAverage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        holder.tvName.text = s.name
        holder.tvCoef.text = "Coef: ${s.coefficient}"
        holder.tvAverage.text = "Moyenne: ${"%.2f".format(s.average)}"
        holder.itemView.setOnClickListener { onClick(s) }
    }

    override fun getItemCount(): Int = items.size
}