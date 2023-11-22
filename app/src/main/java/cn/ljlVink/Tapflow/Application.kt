package cn.ljlVink.Tapflow

import android.app.Application

class MyApplication : Application() {
    // 在这里声明你想要存储的全局数据
     var info_usr: FileSystemInfo? = null
     var info_opt: FileSystemInfo? = null
}
