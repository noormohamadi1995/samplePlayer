package ir.noormohammadi.sampleplayer.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.permissionx.guolindev.PermissionX
import ir.noormohammadi.sampleplayer.databinding.ActivityMainBinding
import ir.noormohammadi.sampleplayer.presentation.adapter.MediaAdapter
import ir.noormohammadi.sampleplayer.presentation.viewModel.MediaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMainBinding
    private val viewModel: MediaViewModel by viewModel()
    private lateinit var mediaAdapter: MediaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        requestPermissions()
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    setupRecyclerView()
                } else {
                    finish()
                }
            }
    }

    private fun setupRecyclerView() {
        mediaAdapter = MediaAdapter(emptyList())
        mBinding.recyclerViewMedia.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            adapter = mediaAdapter
        }

        viewModel.mediaList.observe(this) {
            mediaAdapter.updateData(it)
        }
    }

    override fun onStart() {
        viewModel.loadMedia()
        super.onStart()
    }
}