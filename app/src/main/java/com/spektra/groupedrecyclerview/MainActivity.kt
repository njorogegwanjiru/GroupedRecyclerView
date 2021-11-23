package com.spektra.groupedrecyclerview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private val dummyList = mutableListOf(
        Pojo("abc", "xyz", "123", "2016-06-21"),
        Pojo("abc", "xyz", "123", "2016-06-21"),
        Pojo("abc", "xyz", "123", "2016-04-21"),
        Pojo("abc", "xyz", "123", "2016-04-21"),
        Pojo("abc", "xyz", "123", "2016-06-21"),
        Pojo("abc", "xyz", "123", "2016-08-21"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataRv: RecyclerView = findViewById(R.id.dataRv)

        Log.d("unsorted", dummyList.toString())
        dummyList.sortBy { it.key4 }
        Log.d("sorted", dummyList.toString())

        val groupedHashMap = groupDataIntoHashMap(dummyList)

        /**
         * Converting the HashMap data into a sorted list
         * We consolidate the HashMap into a ArrayList (of our newly created ListItem type)
         * called consolidatedList with DateItem injected between GeneralItem at the required places.
         */

        val consolidatedList = arrayListOf<ListItem>()

        for (date: String in groupedHashMap!!.keys) {
            val dateItem = DateItem()
            dateItem.setDate(date)
            consolidatedList.add(dateItem)

            for (dummyObject: Pojo in groupedHashMap[date]!!) {
                val generalItem = GeneralItem()
                generalItem.setDummyObject(dummyObject)
                consolidatedList.add(generalItem)
            }
        }

        dataRv.apply {
            adapter = GroupedListRecyclerAdapter(consolidatedList)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun groupDataIntoHashMap(dummyList: List<Pojo>): HashMap<String, MutableList<Pojo>>? {
        val groupedHashMap: HashMap<String, MutableList<Pojo>> = HashMap()
        for (dummyObject in dummyList) {

            val hashMapKey: String = dummyObject.key4

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the object against the existing key.
                groupedHashMap[hashMapKey]!!.add(dummyObject)
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                val list: MutableList<Pojo> = ArrayList()
                list.add(dummyObject)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }
}


/**Creating required data structures for the RecyclerView
 * Now we make an abstract class called ListItem that will be inherited by two classes
 * which will represent the two different types of list items:
 * - DateItem, which will show the month;
 * - GeneralItem, which will show the required data.
 **/


/**
 * ListItem -
 */
abstract class ListItem {
    abstract val type: Int

    companion object {
        const val TYPE_DATE = 0
        const val TYPE_GENERAL = 1
    }
}

/**
 * GeneralItem -
 */
class GeneralItem : ListItem() {

    private var dummyObject: Pojo? = null

    fun getDummyObject(): Pojo? {
        return dummyObject
    }

    fun setDummyObject(pojoOfJsonArray: Pojo?) {
        this.dummyObject = pojoOfJsonArray
    }

    override val type: Int
        get() = TYPE_GENERAL
}

/**
 * DateItem -
 */
class DateItem : ListItem() {

    private var date: String? = null

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    override val type: Int
        get() = TYPE_DATE
}


/**
 * Recyclerview adapter
 */

class GroupedListRecyclerAdapter(private var consolidatedList: List<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ListItem.TYPE_GENERAL -> GeneralViewHolder(parent)
            else -> DateViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ListItem.TYPE_GENERAL -> {

                val generalItem: GeneralItem = consolidatedList[position] as GeneralItem
                val generalViewHolder = holder as GeneralViewHolder

                generalViewHolder.key1.text = generalItem.getDummyObject()?.key1
                generalViewHolder.key2.text = generalItem.getDummyObject()?.key2
                generalViewHolder.key3.text = generalItem.getDummyObject()?.key3
                generalViewHolder.key4.text = generalItem.getDummyObject()?.key4
            }
            else -> {
                val dateItem:DateItem = consolidatedList[position] as DateItem
                val dateViewHolder = holder as DateViewHolder

                dateViewHolder.date.text = dateItem.getDate()

            }
        }
    }

    inner class GeneralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        constructor(parent: ViewGroup)
                : this(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.general_layout, parent, false)
        )

        val key1: TextView = itemView.findViewById(R.id.key1)
        val key2: TextView = itemView.findViewById(R.id.key2)
        val key3: TextView = itemView.findViewById(R.id.key3)
        val key4: TextView = itemView.findViewById(R.id.key4)
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        constructor(parent: ViewGroup)
                : this(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.date_layout, parent, false)
        )

        val date: TextView = itemView.findViewById(R.id.date)
    }


    override fun getItemCount(): Int {
        return consolidatedList.size
    }

    override fun getItemViewType(position: Int): Int {
        return consolidatedList[position].type
    }
}

/**
 * Data class
 */
data class Pojo(val key1: String, val key2: String, val key3: String, val key4: String)








