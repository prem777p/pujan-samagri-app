package com.prem.pujansamagri.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.prem.pujansamagri.R
import com.prem.pujansamagri.`interface`.LocalParcheStorage
import com.prem.pujansamagri.models.Item
import com.prem.pujansamagri.models.PredefineParche
import com.prem.pujansamagri.models.UserInfo
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class LocalParcheStorageImpl(val context: Context, dir: String) : LocalParcheStorage {
    private val directory: File = File(context.getExternalFilesDir(null), dir)

    override fun initializePreDefineParche() {
        if (!directory.exists()) {
            directory.mkdirs()
            initializeAllParche()
        }
    }

    private fun initializeAllParche() {
        // save saman Item
        val samanList = context.resources.openRawResource(R.raw.saman_list).bufferedReader().lines().toArray().toList()
        saveSamanList(samanList)

        val parcheJSON = context.resources.openRawResource(R.raw.predefine_parche).bufferedReader().readText()

        val parchePair = jsonParsing(parcheJSON)
        for (i in parchePair.second.indices) {
            val it = parchePair.second[i]
            savedParcha(it.second, parchePair.first, it.third)
        }
    }

    override fun savedParcha( fileName: String, userInfo: UserInfo, itemList: ArrayList<Item>?): Boolean {

        try {
            val filename = "$fileName.json"
            val file = File(directory, filename)
            file.createNewFile()
            fileWriter(
                convertToJSON(Pair(userInfo, Triple(userInfo.title, fileName, itemList))),
                file
            )
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Error in Load Parche", Toast.LENGTH_SHORT).show()
            Log.e("LocalParcheStorageImpl savaedParcha File Writer Error", e.toString())
        }
        return false
    }

    private  fun saveSamanList(samanList: List<Any>){
        try {
            val filename = "SAMAN_LIST.txt"
            val file = File(directory, filename)
            file.createNewFile()
            parcheListFileWriter(samanList, file)
        } catch (e: Exception) {
            Toast.makeText(context, "Error in Load Parche", Toast.LENGTH_SHORT).show()
            Log.e("LocalParcheStorageImpl savaedParcha File Writer Error", e.toString())
        }
    }

    fun saveSamanListItem(samanListNewItem: String): Boolean{
        val samanList = ArrayList<String>()
        getParcheList(PredefineParche.SAMAN_LIST.name.plus(".txt")).forEach {
            samanList.add(it.name)
        }
        if (!samanList.contains(samanListNewItem)){
            samanList.add(samanListNewItem)
            saveSamanList(samanList)
            return true
        }
        return false
    }

    private fun fileWriter(jsonObject: JSONObject, file: File) {
        val bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file)))
        bufferedWriter.write(jsonObject.toString(4))
        bufferedWriter.close()
    }

    private fun parcheListFileWriter(data: List<Any>, file: File) {
        var count = 0
        val bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file)))

        for (i in data) {
            val line = i.toString().plus("\n")
            bufferedWriter.write(line)

            count++
            // Flush every 1000 lines to avoid memory overflow
            if (count % 1000 == 0) {
                count = 0
                bufferedWriter.flush()
            }
        }

        bufferedWriter.close()
    }


    override fun parcheExist(dir: String): Boolean =
        File(context.getExternalFilesDir(null), dir).exists()

    private fun deleteAllFilesInDirectory(directory: File) {
        // Check if the directory exists and is indeed a directory
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                file.delete()
            }
        }
    }

    fun getParche(pName: String): Pair<UserInfo, ArrayList<Item>>? {
        if (!directory.exists()) {
            initializePreDefineParche()
        }
        val file: File
        try {
            file = File(directory.path.plus("/${pName}.json"))
            return jsonParsing(file.readText(), file.name)
        } catch (e: Exception) {
            Log.e("LocalParchaStorageImp getParcha", e.toString())
            return null
        }
    }

    fun getAllParche(): Array<out String>? {
        return directory.list()
    }

    fun getParcheList(pName: String): ArrayList<Item> {
        if (!directory.exists()) {
            initializePreDefineParche()
        }

        val file = File(directory.path.plus("/${pName}"))

        val list = ArrayList<Item>()
        file.readLines().onEach {
            list.add(Item(it))
        }

        return list
    }

    private fun jsonParsing(json: String): Pair<UserInfo, ArrayList<Triple<String, String, ArrayList<Item>?>>> {
        val result = ArrayList<Triple<String, String, ArrayList<Item>?>>()

        // Parse the JSON string
        val jsonObject = JSONObject(json)

        // Parse the "info" section
        val infoObject = jsonObject.getJSONObject("info")
        val userInfo = UserInfo(
            title = infoObject.getString("title"),
            name = infoObject.getString("name"),
            phoneNo = infoObject.getString("phone no.")
        )

        // Get the "parche" array
        val parcheArray = jsonObject.getJSONArray("parche")

        for (i in 0 until parcheArray.length()) {
            val parcheObject = parcheArray.getJSONObject(i)

            // Extract the title
            val title = parcheObject.getString("title")

            // Loop through the keys to find nested arrays
            for (key in parcheObject.keys()) {
                if (key == "title") continue // Skip the title key

                val itemList = ArrayList<Item>()

                // Extract the items in the array
                val itemsArray = parcheObject.getJSONArray(key)
                for (j in 0 until itemsArray.length()) {
                    val itemData = itemsArray.getJSONArray(j)
                    val name = itemData.getString(0)
                    val quantity = itemData.getString(1)
                    val unit = itemData.getString(2)

                    itemList.add(Item(name, quantity, unit, false))
                }

                // Add the Triple to the result list
                result.add(Triple(title, key, itemList))

            }
        }
        return Pair(userInfo, result)
    }

    private fun jsonParsing(json: String, key: String): Pair<UserInfo, ArrayList<Item>> {

        // Parse the JSON string
        val jsonObject = JSONObject(json)

        // Parse the "info" section
        val userInfo = UserInfo(
            title = jsonObject.getString("title"),
            name = jsonObject.getString("name"),
            phoneNo = jsonObject.getString("phone no.")

        )

        // Get the "parche" array
        val parcheArray = jsonObject.getJSONArray("parche")

        val parcheObject = parcheArray.getJSONObject(0)
        val itemList = ArrayList<Item>()
        for (key in parcheObject.keys()) {
            // Extract the items in the array
            val itemsArray = parcheObject.getJSONArray(key)
            for (j in 0 until itemsArray.length()) {
                val itemData = itemsArray.getJSONArray(j)
                val name = itemData.getString(0)
                val quantity = itemData.getString(1)
                val unit = itemData.getString(2)

                itemList.add(Item(name, quantity, unit, true))
            }
        }
        return Pair(userInfo, itemList)
    }

    private fun convertToJSON(parchePair: Pair<UserInfo, Triple<String, String, ArrayList<Item>?>>): JSONObject {
        // Create the main JSON object
        val jsonObject = JSONObject()

        // Add basic info
        jsonObject.put("title", parchePair.first.title)
        jsonObject.put("name", parchePair.first.name)
        jsonObject.put("phone no.", parchePair.first.phoneNo)

        // Create the "parche" JSON array
        val parcheArray = JSONArray()
        val parcheObject = JSONObject()

        // Add each category (e.g., "Havan", "Sundarkand")
        val categoryArray = JSONArray()

        // Convert the items into JSON arrays
        parchePair.second.third?.forEach { item ->
            val itemArray = JSONArray()
            itemArray.put(item.name)
            itemArray.put(item.quantity)
            itemArray.put(item.unit)
            categoryArray.put(itemArray)
        }

        // Add the category array to the parche object
        parcheObject.put(parchePair.second.second, categoryArray)

        // Add the parche object to the main parche array
        parcheArray.put(parcheObject)

        // Add "parche" array to the main JSON object
        jsonObject.put("parche", parcheArray)

        return jsonObject
    }

    fun updateUserInfo(userInfo: UserInfo, fileName: String) {
        val file = File(directory.path.plus("/${fileName}"))
        val updatedJsonObj = updateInfoSection(file.readText(), userInfo)
        fileWriter(updatedJsonObj, file)
    }

    private fun updateInfoSection(jsonString: String, userInfo: UserInfo): JSONObject {
        // Parse the JSON string into a JSONObject
        val jsonObject = JSONObject(jsonString)

        // Update the fields in the `info` object
        jsonObject.put("title", userInfo.title)
        jsonObject.put("name", userInfo.name)
        jsonObject.put("phone no.", userInfo.phoneNo)

        // Return the updated JSON as a string
        return jsonObject
    }

    fun updateUserInfoInAllParche(userInfo: UserInfo) {
        directory.list()?.filter { it.contains(".json") }?.onEach {
            updateUserInfo(userInfo, it)
        }
    }

}