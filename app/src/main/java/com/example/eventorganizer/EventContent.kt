package com.example.eventorganizer

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

object EventContent{
    /**
     * An array of sample (Event) items.
     */
    val ITEMS: MutableList<EventItem> = ArrayList()

    /**
     * A map of sample (Event) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, EventItem> = HashMap<String, EventItem>()

    private val COUNT = 3

    init {
        /*
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createEventItem(i,R.drawable.abc))
        }
        */
        addItem(createEventItem(1,R.drawable.trueasf,title="Adam zrób projekt",place="PWR"))
        addItem(createEventItem(3,R.drawable.abc,title = "Piwko",place="Warka"))
        addItem(createEventItem(2,R.drawable.mo,title = "Meczyk",place="Orlik spółdzielcza"))


    }

    private fun addItem(item: EventItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createEventItem(position: Int,imageID: Int,title: String = "title",place: String="place"): EventItem {
        return EventItem(position.toString(), "Item " + position,imageID = imageID,title = title,place = place)
    }

    /*
    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }
    */

    /**
     * A Event item representing a piece of content.
     */
    data class EventItem(val id: String,val data:String="data",val title:String,val place:String,val spots:String = "0/12", val imageID : Int = R.drawable.abc): Serializable {
        override fun toString(): String = title
    }
}