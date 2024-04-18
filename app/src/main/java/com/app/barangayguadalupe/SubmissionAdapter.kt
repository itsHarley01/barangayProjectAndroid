package com.app.barangayguadalupe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubmissionAdapter(private val submissions: List<Submission>) : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {

    interface OnViewButtonClickListener {
        fun onViewButtonClick(submission: Submission)
    }

    private var onViewButtonClickListener: OnViewButtonClickListener? = null

    fun setOnViewButtonClickListener(listener: OnViewButtonClickListener) {
        onViewButtonClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_submission, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val submission = submissions[position]

        holder.titleTextView.text = submission.type
        holder.nameTextView.text = "${submission.firstName} ${submission.lastName}"
        holder.dateTextView.text = "Date Submitted: ${submission.dateSubmitted}"
        holder.statusTextView.text = "${submission.status}  ${submission.dateApproved}"

        holder.viewButton.setOnClickListener {
            onViewButtonClickListener?.onViewButtonClick(submission)
        }
    }

    override fun getItemCount(): Int {
        return submissions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val viewButton: Button = itemView.findViewById(R.id.viewButton)
    }
}
