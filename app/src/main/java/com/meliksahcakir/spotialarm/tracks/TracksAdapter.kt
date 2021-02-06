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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TrackViewHolder(
            TrackItemBinding.inflate(inflater, parent, false),
            modelListener,
            trackListener
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

interface TrackListener {
    fun play(track: Track)
    fun stop(track: Track)
    fun updateTrack(track: Track)
}
