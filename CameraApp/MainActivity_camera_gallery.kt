package com.example.mygallery

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mygallery.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val FLAG_REQ_CAMRA = 101 // 카메라 호출 플래그
    val FLAG_REQ_STORAGE = 102 // 갤러리 호출 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setPermission() // 권한체크

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            setPermission()
        } else {
            binding.buttonCamera.setOnClickListener {
                openCamera()
            }
            binding.buttonGallery.setOnClickListener {
                openGallery()
            }
        }
    }

    // Ted Permission 권한 설정
    private fun setPermission() {

        val permission = object : PermissionListener {
            override fun onPermissionGranted() { // 권한이 허용되었을 경우 이곳이 수행
                Toast.makeText(this@MainActivity, "권한이 혀용 되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  // 권한이 거부되었을 경우
                Toast.makeText(this@MainActivity, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("갤러리를 사용하시려면 필수 권한을 허용해주세요.")
            .setDeniedMessage("카메라, 저장소 접근 권한이 필요합니다. [앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, )
            .check()
    }

    fun openCamera() {
        // 카메라 앱을 호출
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, FLAG_REQ_CAMRA)
    }

    fun openGallery() {
        // 갤러리 앱을 호출
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, FLAG_REQ_STORAGE)
    }

    // 촬영후 onActivityResult에 결과값이 전달됨.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)  // 사진은 data에 저장됨.

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FLAG_REQ_CAMRA -> {
                    if (data?.extras?.get("data") != null) { // 촬영 이미지가 있으면...
                        val bitmap = data?.extras?.get("data") as Bitmap
                        val uri = saveImageFile(newFileName(), "image/jpg", bitmap)
                        binding.imageView.setImageURI(uri)  // 이미지뷰에 뿌려줌.
                    }
                }
                // 갤러리 앱에서 선택한 이미지를 뷰에 뿌려줌
                FLAG_REQ_STORAGE -> {
                    val uri = data?.data
                    binding.imageView.setImageURI(uri)
                }
            }
        }
    }

    fun newFileName() : String {
        // 시간으로 파일명을 생성 - 중복되지 않게
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }

    //이미지 파일을 저장
    fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap) : Uri? {

        // MediaStore에 저장
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename) //파일이름
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)  //마임타입

        //Q버전 이상에서 다른곳에서 내가 사용하는 데이터를 요청할 경우 무시 -> IS_PENDING 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        // Mediastore에 파일을 등록
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            // 파일 디스크립터로 파일을 읽거나 쓸수있다.
            uri?.let {
                val descriptor = contentResolver.openFileDescriptor(uri, "w")

                descriptor?.let {
                    // FileOutputStream으로 Bitmap 파일을 저장 / 압축률 100
                    val fos = FileOutputStream(it.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0) // 위에서 1로 설정 -> 원래데로 복귀
                        contentResolver.update(uri, values, null, null)
                    }
                }
            }
        } catch (e:java.lang.Exception) {
            Log.e("File", "error=${e.localizedMessage}")
        }
        return uri
    }
}