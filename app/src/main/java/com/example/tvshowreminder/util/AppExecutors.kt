package com.example.tvshowreminder.util

import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


const val THREAD_COUNT = 3

class AppExecutors {

    val diskIO = DiskIOThreadExecutor()
    val networkIO: ScheduledExecutorService = Executors.newScheduledThreadPool(THREAD_COUNT)
    val main = MainThreadExecutor()


    class DiskIOThreadExecutor : Executor {
        private val diskIO = Executors.newSingleThreadExecutor()
        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }

    class MainThreadExecutor : Executor {
        private val mainThreadHandler = android.os.Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }


}