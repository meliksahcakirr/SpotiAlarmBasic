package com.meliksahcakir.spotialarm.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.meliksahcakir.spotialarm.databinding.TrackItemBinding
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.music.ui.TrackDiffCallback
import com.meliksahcakir.spotialarm.music.ui.TrackViewHolder

class TracksAdapter(
    private val modelListener: MusicUIModelListener,
    private val trackListener: TrackListener
) :
    ListAdapter<MusicUIModel.TrackItem, TrackViewHolder>(TrackDiffCallback) {

    private val _modelListener = object : MusicUIModelListener {
        override fun onClicked(model: MusicUIModel) {
            val prev = selectedTrackId
            selectedTrackId = (model as MusicUIModel.TrackItem).track.id
            if (selectedTrackId == prev) {
                selectedTrackId = null
                notifyItemChanged(currentList.indexOfFirst { it.track.id == prev })
            } else {
                notifyItemChanged(currentList.indexOfFirst { it.track.id == prev })
                notifyItemChanged(currentList.indexOfFirst { it.track.id == selectedTrackId })
            }
            modelListener.onClicked(model)
        }
    }

    private val _trackListener = object : TrackListener {
        override fun play(track: Track) {
            val prev = playedTrackId
            playedTrackId = track.id
            notifyItemChanged(currentList.indexOfFirst { it.track.id == prev })
            notifyItemChanged(currentList.indexOfFirst { it.track.id == playedTrackId })
            trackListener.play(track)
        }

        override fun stop(track: Track) {
            val prev = playedTrackId
            playedTrackId = null
            notifyItemChanged(currentList.indexOfFirst { it.track.id == prev })
            trackListener.stop(track)
        }

        override fun updateTrack(track: Track) {
            trackListener.updateTrack(track)
        }
    }

    var playedTrackId: String? = null
        private set
    private var selectedTrackId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TrackViewHolder(
            TrackItemBinding.inflate(inflater, parent, false),
            _modelListener,
            _trackListener
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackItem = getItem(position)
        trackItem.track.isPlaying = trackItem.track.id == playedTrackId
        holder.bind(trackItem)
        holder.updateView(trackItem.track.id == selectedTrackId, trackItem.track.isPlaying)
    }
}

interface TrackListener {
    fun play(track: Track)
    fun stop(track: Track)
    fun updateTrack(track: Track)
}
