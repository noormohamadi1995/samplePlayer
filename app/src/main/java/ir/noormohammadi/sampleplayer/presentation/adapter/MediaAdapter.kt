package ir.noormohammadi.sampleplayer.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import ir.noormohammadi.sampleplayer.databinding.ItemMediaBinding
import ir.noormohammadi.sampleplayer.domain.model.Media

class MediaAdapter(
    private var mediaList: List<Media>
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(media: Media) {
            binding.imageViewThumbnail.load(media.thumbnail){
                size(110,110)
                scale(Scale.FIT)
                crossfade(true)
            }
            binding.videoIcon.visibility = if (media.isVideo) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(mediaList[position])
    }

    override fun getItemCount() = mediaList.size

    fun updateData(newMediaList: List<Media>) {
        this.mediaList = newMediaList
        Log.e("updateData", "updateData: $newMediaList")
        notifyDataSetChanged()
    }
}