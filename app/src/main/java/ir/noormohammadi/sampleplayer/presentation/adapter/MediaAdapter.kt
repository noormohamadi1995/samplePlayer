package ir.noormohammadi.sampleplayer.presentation.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import coil.size.Scale
import ir.noormohammadi.sampleplayer.R
import ir.noormohammadi.sampleplayer.databinding.RecyclerviewImageItemBinding
import ir.noormohammadi.sampleplayer.databinding.RecyclerviewVideoItemBinding
import ir.noormohammadi.sampleplayer.presentation.data.MediaDataModel

class MediaAdapter : ListAdapter<MediaDataModel, ViewHolder>(
    object : DiffUtil.ItemCallback<MediaDataModel>() {
        override fun areItemsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: MediaDataModel, newItem: MediaDataModel) =
            oldItem == newItem
    }
) {
    private var currentPlayer: ExoPlayer? = null
    private var currentPlayerView: PlayerView? = null
    private var currentThumbnail: ImageView? = null
    private var currentPlayIcon: ImageView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MediaDataModel.Video.VIEW_TYPE -> {
                VideoViewHolder(
                    RecyclerviewVideoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                ImageViewHolder(
                    RecyclerviewImageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val media = getItem(position)) {
            is MediaDataModel.Image -> (holder as ImageViewHolder).bind(media)
            is MediaDataModel.Video -> (holder as VideoViewHolder).bind(media)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MediaDataModel.Image -> MediaDataModel.Image.VIEW_TYPE
            is MediaDataModel.Video -> MediaDataModel.Video.VIEW_TYPE
        }
    }

    inner class ImageViewHolder(binding: RecyclerviewImageItemBinding) :
        ViewHolder(binding.root) {
        private val imageThumbnail: ImageView = itemView.findViewById(R.id.image_thumbnail)

        fun bind(media: MediaDataModel.Image) {
            imageThumbnail.colorFilter = if (media.isFiltered) {
                val matrix = ColorMatrix().apply { setSaturation(0f) }
                ColorMatrixColorFilter(matrix)
            } else {
                null
            }

            // لود تصویر
            imageThumbnail.load(media.thumbnail ?: media.uri) {
                scale(Scale.FILL)
                crossfade(true)
            }

            itemView.setOnClickListener {
                media.toggleFilter(media.uri)
            }
        }
    }

    inner class VideoViewHolder(val binding: RecyclerviewVideoItemBinding) :
        ViewHolder(binding.root) {

        fun bind(media: MediaDataModel.Video) {
            binding.apply {
                videoPlayerView.visibility = View.GONE
                iconPlay.visibility = View.GONE
                imageThumbnail.visibility = View.VISIBLE


                imageThumbnail.load(media.thumbnail) {
                    scale(Scale.FILL)
                    crossfade(true)
                }

                if (media.isPlaying) {
                    playVideo(media.uri)
                    videoPlayerView.visibility = View.VISIBLE
                    imageThumbnail.visibility = View.GONE
                    iconPlay.visibility = View.GONE
                } else {
                    videoPlayerView.visibility = View.GONE
                    imageThumbnail.visibility = View.VISIBLE
                    iconPlay.visibility = View.VISIBLE
                }

                itemView.setOnClickListener {
                    media.togglePlay.invoke(media.uri)
                }
            }
        }

        private fun playVideo(videoUri: Uri) {
            currentPlayer?.release()
            currentPlayer = ExoPlayer.Builder(itemView.context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
                playWhenReady = true
            }
            binding.videoPlayerView.player = currentPlayer
        }
    }

    fun releasePlayer() {
        currentPlayer?.release()
        currentPlayer = null

        currentPlayerView?.visibility = View.GONE
        currentThumbnail?.visibility = View.VISIBLE
        currentPlayIcon?.visibility = View.VISIBLE

        currentPlayerView = null
        currentThumbnail = null
        currentPlayIcon = null
    }
}