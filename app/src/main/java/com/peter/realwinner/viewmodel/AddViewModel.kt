package com.peter.realwinner.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peter.realwinner.R
import com.peter.realwinner.RealWinnerApplication
import com.peter.realwinner.model.db.dao.FriendInfoDao
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.util.LogUtil
import com.peter.realwinner.util.SharedPreferencesUtil
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AddViewModel : ViewModel() {

    companion object {
        private const val FILE_CHILD_IMAGE = "image"
    }

    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)

    private var friendInfoEntity: FriendInfoEntity? = null
    private val friendInfoDao by inject(FriendInfoDao::class.java)

    val errorMsgLiveData = MutableLiveData<String>()
    val validateLiveData = MutableLiveData<Boolean>()
    val saveLiveData = MutableLiveData<Boolean>()
    val saveToInternalLiveData = MutableLiveData<String>()
    val newBitmapLiveData = MutableLiveData<Bitmap>()

    private var post: String? = null

    fun getTheme() : LiveData<Int> {
        return MutableLiveData(sharedPreferencesUtil.getStyle())
    }

    fun getHeightArray() : LiveData<ArrayList<Int>> {
        val array = ArrayList<Int>()
        for (i in 100 until 251) {
            array.add(i)
        }
        return MutableLiveData(array)
    }

    fun getBodyWeightArray() : LiveData<ArrayList<Int>> {
        val array = ArrayList<Int>()
        for (i in 30 until 151) {
            array.add(i)
        }
        return MutableLiveData(array)
    }

    fun validate(friendInfoEntity: FriendInfoEntity) {
        if (friendInfoEntity.name.isBlank()) {
            errorMsgLiveData.value = RealWinnerApplication.getBaseContext().getString(R.string.add_full_name_validate_error)
            return
        }
        val newFriendEntity = FriendInfoEntity (
            name = friendInfoEntity.name,
            nickName = friendInfoEntity.nickName,
            birth = friendInfoEntity.birth,
            blood = friendInfoEntity.blood,
            height = friendInfoEntity.height,
            cellphone = friendInfoEntity.cellphone,
            personality = friendInfoEntity.personality,
            hobby = friendInfoEntity.hobby,
            line = friendInfoEntity.line,
            facebook = friendInfoEntity.facebook,
            instagram = friendInfoEntity.instagram,
            other = friendInfoEntity.other,
            post = post
        )
        this.friendInfoEntity = newFriendEntity
        validateLiveData.value = true
    }

    fun saveToDb() {
        friendInfoEntity?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    friendInfoDao.updateIfExist(it)
                }
                saveLiveData.value = true
            }
        }
    }

    fun saveToInternal(uri: Uri) {
        val inputStream = RealWinnerApplication.getBaseContext().contentResolver.openInputStream(uri)
        inputStream?.let { input ->
            val filePackage = File(RealWinnerApplication.getBaseContext().filesDir, FILE_CHILD_IMAGE)
            if (!filePackage.exists()) {
                filePackage.mkdir()
            }
            val fileName = "img_${System.currentTimeMillis()}.png"
            val dest = File(filePackage, fileName)
            if (dest.exists()) {
                dest.delete()
            }
            dest.createNewFile()
            try {
                val outputStream = FileOutputStream(dest)
                val buffer = ByteArray(1024)
                while (true) {
                    val len = input.read(buffer)
                    if (len <= 0) break
                    outputStream.write(buffer, 0, len)
                }
                input.close()
                outputStream.close()
                post = dest.absolutePath
                saveToInternalLiveData.value = dest.absolutePath
            } catch (e: IOException) {
                LogUtil.showError(e.localizedMessage ?: String())
            }
        }
    }

    fun decodeFile(dest: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                options.inSampleSize = selectBitmapSize(dest)
                options.inJustDecodeBounds = false

                val newBitmap = getOrientationBitmap(dest , options)
                newBitmapLiveData.postValue(newBitmap)
            }
        }
    }

    private fun selectBitmapSize(filePath: String) : Int {
        var sizeFile = getFileSize(File(filePath))
        sizeFile /= 1000

        return when {
            sizeFile <= 250 -> {
                2
            }
            sizeFile in 251..1499 -> {
                2
            }
            sizeFile in 1500..2999 -> {
                2
            }
            sizeFile in 3000..4499 -> {
                4
            }
            else -> {
                4
            }
        }
    }

    private fun getFileSize(file: File?) : Long {
        if (file == null || !file.exists()) {
            return 0
        }
        if (!file.isDirectory) {
            return file.length()
        }
        val dirs = LinkedList<File>()
        dirs.add(file)
        var result : Long = 0
        while (!dirs.isEmpty()) {
            val dir = dirs.removeAt(0)
            if (!dir.exists()) {
                continue
            }
            val listFiles = dir.listFiles()
            if (listFiles == null || listFiles.isEmpty()) {
                continue
            }
            for (child in listFiles) {
                result += child.length()
                if (child.isDirectory) {
                    dirs.add(child)
                }
            }
        }
        return result
    }

    private fun getOrientationBitmap(filePath: String, options: BitmapFactory.Options) : Bitmap {
        var srcBitmap = BitmapFactory.decodeFile(filePath, options)
        try {
            val orientation = resolveBitmapOrientation(File(filePath))
            srcBitmap = applyOrientation(srcBitmap, orientation)
        } catch (e: Exception) {
            throw e
        }
        return srcBitmap
    }

    private fun resolveBitmapOrientation(bitmapFile: File) : Int {
        val exif = ExifInterface(bitmapFile.absolutePath)

        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    }

    private fun applyOrientation(bitmap: Bitmap, orientation: Int) : Bitmap {
        var rotate = 0f
        rotate = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            else -> return bitmap
        }
        val width = bitmap.width
        val height = bitmap.height
        val mtx = Matrix()
        mtx.postRotate(rotate)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true)
    }

}